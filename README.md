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

* 2011.8

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

# Installation
## Dependencies for execution
 - groovy (http://groovy-lang.org/) min. Version 2.4.7
 
## Dependencies for bash completion
 - jq (https://stedolan.github.io/jq/) 
 - perl (https://www.perl.org/)
 - 'bash-completion' to allow bash completion ;)

If you are using a mac, please make sure that you have 'bash-completion' installed.
Configure the COMPLETION_DIR path in 'setup.sh' accordingly.

## Steps to do
```
    https://github.com/neuland/hac.git
```

* Checkout code
* Copy the hac/src/hac.sh into you bin folder
* Edit hac.sh in bin folder and modify the file locations of hac.groovy and config.json
* change config.json with your settings
