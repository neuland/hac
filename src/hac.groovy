import groovy.json.JsonSlurper
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

import static groovyx.net.http.ContentType.URLENC

@Grapes([
        @Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7.1')
])
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

def config = new JsonSlurper().parse(new File(System.getProperty("user.home"), '.hac/config.json').toURI().toURL())

def serverList = getServerList(config, options)
def script = getScript(options)
def type = getType(options)
def username = getUsername(config, options)
def password = getPassword(config, options)

serverList.each { serverUrl ->
    println "-------------------------------------------------------------------------------"
    println serverUrl
    println "-------------------------------------------------------------------------------"

    def hAC = new RESTClient(serverUrl)
    hAC.headers['Authorization'] = 'Basic ' + "${username}:${password}".getBytes('iso-8859-1').encodeBase64()
    switch (type) {
        case 'groovy':
            executeGroovy(hAC, script)
            break
        case 'flex':
            executeFlexSearch(hAC, script)
            break
        default:
            println "unknown type $type"
    }
}

private void executeGroovy(RESTClient hAC, script) {
    hAC.request(Method.POST) { req ->
        uri.path = '/console/groovy/execute'
        requestContentType = URLENC
        body = ['script'  : script,
                'maxCount': 200]

        response.success = { resp, json ->
            println "Result: ${json.executionResult}"

            if (json.outputText) {
                println "\nOutput\n------"
                println json.outputText
            }
            if (json.stacktraceText) {
                println "\nStacktrace\n----------"
                println json.stacktraceText
            }
        }

        response.failure = { resp, body ->
            println "request failed $body"
            assert resp.status >= 400
        }
    }
}

private void executeFlexSearch(RESTClient hAC, script) {
    hAC.request(Method.POST) { req ->
        uri.path = '/console/flexsearch/execute'
        requestContentType = URLENC
        body = ['flexibleSearchQuery': script]

        response.success = { resp, json ->
            println "Execution time: ${json.executionTime}ms"

            if (json.exception) {
                println "\nException\n---------"
                println json.exception
            }
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

        response.failure = { resp, body ->
            println "request failed $body"
            assert resp.status >= 400
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
        if (file.name.endsWith('.groovy')) {
            return 'groovy'
        } else if (file.name.endsWith('.flex')) {
            return 'flex'
        } else {
            println "Unkown file ending for file ${file.name}"
        }
    }
}
