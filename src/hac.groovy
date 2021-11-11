@Grab(group = 'org.ccil.cowan.tagsoup', module = 'tagsoup', version = '1.2')

import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult

import javax.net.ssl.*
import java.security.SecureRandom

def cli = new CliBuilder(usage: "${this.class.name}.groovy --env <live> -f <my-script.groovy>")
cli.with {
    h longOpt: 'help', 'Show usage information'
    e longOpt: 'env', args: 1, argName: 'env', required: true, 'enviroment'
    i longOpt: 'file', args: 1, argName: 'file', required: false, 'file containing the script'
    t longOpt: 'type', args: 1, argName: 'type', required: false, 'specify the input type [groovy|flex]'
}
def options = cli.parse(args)
if (options == null) {
    return
} else if (options.h) {
    cli.usage()
    return
}

def config = new JsonSlurper().parse(new File('src/config.json').toURI().toURL())

def serverList = getServerList(config, options)
def script = getScript(options)
def type = getType(options)
def username = getUsername(config, options)
def password = getPassword(config, options)
def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
def slurper = new XmlSlurper(tagsoupParser)

// handle cookies
cookieManager = new java.net.CookieManager();
CookieHandler.setDefault(cookieManager);
cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

//bypass SSL cert verification
def sc = SSLContext.getInstance("SSL")
def trustAll = [getAcceptedIssuers: {}, checkClientTrusted: { a, b -> }, checkServerTrusted: { a, b -> }]
sc.init(null, [trustAll as X509TrustManager] as TrustManager[], new SecureRandom())
hostnameVerifier = [verify: { hostname, session -> true }]
HttpsURLConnection.defaultSSLSocketFactory = sc.socketFactory
HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier as HostnameVerifier)

serverList.each { serverUrl ->
    println "-------------------------------------------------------------------------------"
    println serverUrl
    println "-------------------------------------------------------------------------------"

    def connection = loginToHac(serverUrl, username, password)
    def csrfToken = getCsrfToken(slurper.parseText(connection.inputStream.text))

    switch (type) {
        case 'groovy':
            executeGroovy(serverUrl, csrfToken, script)
            break
        case 'flex':
            executeFlexSearch(serverUrl, csrfToken, script)
            break

        case 'export':
            executeExport(serverUrl, csrfToken, script)
            break
        default:
            println "unknown type $type"
    }

}

private void executeGroovy(String serverUrl, String csrfToken, script) {

    def con = getConnection(serverUrl + '/console/scripting/execute', csrfToken)

    con.outputStream.withWriter { Writer writer ->
        writer << "script=" + URLEncoder.encode(script, "UTF-8") + "&scriptType=groovy&commit=false"
    }

    String response = con.inputStream.withReader { Reader reader -> reader.text }

    def jsonSlurper = new JsonSlurper()
    def json = jsonSlurper.parseText(response)

    if (json.executionResult) {
        println "\nExecutionResult\n---------------"
        println json.executionResult
    }
    if (json.outputText) {
        println "\nOutput\n------"
        println json.outputText
    }
    if (json.stacktraceText) {
        println "\nStacktrace\n----------"
        println json.stacktraceText
    }
}


private void executeFlexSearch(String serverUrl, String csrfToken, script) {

    def con = getConnection(serverUrl + '/console/flexsearch/execute', csrfToken)

    con.outputStream.withWriter { Writer writer ->
        writer << "flexibleSearchQuery=" + URLEncoder.encode(script, "UTF-8") + "&commit=false"
    }

    String response = con.inputStream.withReader { Reader reader -> reader.text }

    def jsonSlurper = new JsonSlurper()
    def json = jsonSlurper.parseText(response)

    if (json.exception) {
        println "\nException\n---------"
        println json.exception
    }

    println "Execution time: ${json.executionTime}ms"

    def columnWidth = []
    if (json.headers) {
        def headerRow = new StringBuffer()
        json.headers.eachWithIndex { header, idx ->
            columnWidth[idx] = [header.size(), json.resultList.collect { row -> row[idx]?.toString()?.size() }.max()].max()
            headerRow << header.padRight(columnWidth[idx] + 1)
        }
        println headerRow
    }
    if (json.resultList) {
        json.resultList.each { row ->
            def rowOutput = new StringBuffer()
            row.eachWithIndex { column, idx ->
                rowOutput << (column ? column : '').padRight(columnWidth[idx] + 1)
            }
            println rowOutput
        }
    }
}

private void executeExport(String serverUrl, String csrfToken, script) {

    def con = getConnection(serverUrl + '/console/impex/export', csrfToken)

    con.outputStream.withWriter { Writer writer ->
        writer << "scriptContent=" + URLEncoder.encode(script, "UTF-8") + "&validationEnum=EXPORT_ONLY&encoding=UTF-8"
    }

    String response = con.inputStream.withReader { Reader reader -> reader.text }

    def slurper = new XmlSlurper(new org.ccil.cowan.tagsoup.Parser())

    def xml = slurper.parseText(response)
    def error = xml.depthFirst().find { it.@id == 'impexResult' && it['@data-level'] == "error" }
    if (error) {
        println error['@data-result']
    } else {

        def downloadExportResultData = xml.depthFirst().find { it.@id == 'downloadExportResultData' }.children()
        def filename = downloadExportResultData.text()
        def pathToResultFile = downloadExportResultData.@href.text()

        new URL(serverUrl + "/console/impex/" + pathToResultFile).openConnection().with { conn ->
            new File(filename).withOutputStream { out ->
                out << conn.inputStream
            }
            println "Result: ${filename}"
        }
    }
}

private def getUsername(config, options) {
    config[options.env].username
}

private def getPassword(config, options) {
    if (config[options.env].password) {
        return config[options.env].password
    } else {
        return System.console().readPassword("Password for user '${getUsername(config, options)}': ")
    }
}

private def getServerList(config, options) {
    config[options.env].server
}

private def getScript(OptionAccessor options) {
    new File(options.file).text
}

private def getType(OptionAccessor options) {
    if (options.type) {
        return options.type
    } else {
        def file = new File(options.file)
        if (file.name.toLowerCase().endsWith('.groovy')) {
            return 'groovy'
        } else if (file.name.toLowerCase().endsWith('.flex')) {
            return 'flex'
        } else if (file.name.toLowerCase().endsWith('.impex')) {
            return 'export'
        } else {
            println "Unkown file ending for file ${file.name}"
        }
    }
}

/**
 * <meta name="_csrf" content="4fed96a3-8267-4449-8d85-76036b1a53c2" />
 * @return the token string
 */
private String getCsrfToken(GPathResult htmlParser) {
    assert htmlParser instanceof groovy.util.slurpersupport.GPathResult
    //print htmlParser.head.meta[3].@name
    return htmlParser.head.'*'.findAll { node ->
        node.@name == '_csrf'
    }.@content
}

private def loginToHac(String serverUrl, username, password) {

    def connection = new URL(serverUrl).openConnection() as HttpURLConnection
    // set some headers
    connection.setRequestProperty('Authorization', 'Basic ' + "${username}:${password}".getBytes('iso-8859-1').encodeBase64())
    assert connection.responseCode == 200

    return connection
}

private def getConnection(String url, String csrfToken) {
    def con = new URL(url).openConnection() as HttpURLConnection

    cookieManager?.getCookieStore()?.getCookies().each { cookie ->
        con.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
    }

    con.setRequestProperty("X-CSRF-TOKEN", csrfToken);
    con.setRequestMethod("POST");
    con.setDoOutput(true);

    return con
}
