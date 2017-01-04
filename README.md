# Introduction
Command Line Interface for hybris Administrator Console
- Execute groovy scripts via command line.
- Execute flex search queries via command line.
- Execute impex export via command line and download the resulting file.
- Use the UNIX pipe system for preprocessing.
- Manage a list of hybris servers grouped by enviroment / stage.
- Execute scripte on multiple servers with one statement
- Use of bash completion to find configured enviroments

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
## ImpEx (Export)
```
    hac local test/test.impex
```
```
    -------------------------------------------------------------------------------
    https://my-server
    -------------------------------------------------------------------------------
    Result: dataexport_0000NHJ9.zip
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
 - groovy (http://groovy-lang.org/)
 
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

Run ./setup.sh to set up the system: (only run ./setup.sh, _don't use_ 'sudo ./setup.sh', that would use 'root's home directory!)
 - a .hac directory will be created in your home dir
 - there will be links set up in that directory to the files in the checkout out repository
 - there will be a link set up in /usr/local/bin
 - there will be a link for the bash completion under /etc/bash_completion.d/

The last two actions need root access, so the setup script will use 'sudo'.
Please have a look at the script _before running it_ to make sure that nothing bad will happen ;)
