# Introduction
Command Line Interface for hybris Administrator Console
- Execute groovy scripts via command line.
- Execute flex search queries via command line.
- Execute impex export via command line and download the resulting file.
- Use the UNIX pipe system for preprocessing.
- Manage a list of hybris servers grouped by enviroment / stage.
- Execute scripts on multiple servers with one statement
- Use of bash completion to find configured enviroments

## Tested with hybris version

* 2205.0

# Usage
## Groovy
```
    hac local test/test.groovy
```
```
    -------------------------------------------------------------------------------
    https://my-server
    -------------------------------------------------------------------------------
    Result: Groovy Rocks!

    Output
    ------
    org.springframework.security.filterChains
    org.springframework.security.filterChainProxy
    org.springframework.security.web.DefaultSecurityFilterChain#0
```
## Flexserarch
```
    hac local test/test.flex
```
```
    -------------------------------------------------------------------------------
    https://my-server
    -------------------------------------------------------------------------------
    Execution time: 15ms
    HJMPTS PK               TYPEPKSTRING      OWNERPKSTRING CREATEDTS               ACLTS MODIFIEDTS              PROPTS ACTIVEFLAG ISOCODE 
    124    9014067975342480 23087816810119820               2000-08-08 17:37:17.594 0     2015-12-23 10:34:35.814 0      1          de      
    33     9014067975342624 23087816810119820               2000-08-08 17:37:17.602 0     2015-12-23 10:34:35.864 0      1          en      
```
## ImpEx (Export) (script file contains string "impex.exportItems")
```
    hac local test/test.impex
```
```
    -------------------------------------------------------------------------------
    https://my-server
    -------------------------------------------------------------------------------
    Result: dataexport_0000NHJ9.zip
```
## ImpEx (Import)
```
    hac local test/test.impex
```
```
    -------------------------------------------------------------------------------
    https://my-server
    -------------------------------------------------------------------------------
    Import finished successfully
```

## Piping
```
    cat test/test.flex | hac local --type flex
```
```
    cat test/test.groovy | hac local --type groovy
```
```
    cat test/test.impex | hac local --type impex
```


## Configuration

Add your target server at: config.json
You have several options for providing a password.

1) Type it into "password": "nimda" --> insecure. Useful for local development systems.
2) Provide a password from console if at config.json is no password specified.
3) Read password from stdin. Type into "password": "-" (a dash).
try: echo -n "nimda" | hac.sh -e local -t flex -i test/test.flex
or better: secret-tool lookup username <aUserName> | hac.sh -e local -t flex -i test/test.flex

## store the password securely

-- not required, daemon is already started
1. Create the keyring manually with a dummy password in stdin
eval "$(printf '\n' | gnome-keyring-daemon --unlock)"

2. Start the daemon, using the password to unlock the just-created keyring:
eval "$(printf '\n' | /usr/bin/gnome-keyring-daemon --start)"


secret-tool lookup foo bar
printf "aPassword" | secret-tool store --label="test" foo bar
secret-tool search --all foo bar
secret-tool clear foo bar

use with
secret-tool lookup system test | hac.sh .. other parameter

HINT: configure in config.json in "password" a dash "-"


# Installation
## Dependencies for execution
 - groovy (http://groovy-lang.org/) min. Version 2.4.7
 
## Dependencies for bash completion
 - jq (https://stedolan.github.io/jq/) 
 - perl (https://www.perl.org/)
 - 'bash-completion' to allow bash completion ;)

If you are using a mac, please make sure that you have 'bash-completion' installed.
Configure the COMPLETION_DIR path in 'setup.sh' accordingly.

## use hac with proxy

I have a wrapper script hac-proxy.sh
```
#!/usr/bin/env bash
# copy the hac-proxy.sh into your bin folder and change correct location of hac.groovy and config.json

echo "execute with $@"

export http_proxy=http://<proxy host>:3128
groovy -Djava.net.useSystemProxies=true ~/git/github/hac/src/hac.groovy --configfile ~/git/github/hac/src/config.json $@

```


## Steps to do
```
    https://github.com/neuland/hac.git
```

* Checkout code
* Copy the hac/src/hac.sh into you bin folder
* Edit hac.sh in bin folder and modify the file locations of hac.groovy and config.json
* change config.json with your settings
