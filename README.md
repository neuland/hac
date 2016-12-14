# Introduction
Command Line Interface for hybris Administrator Console
- Execute groovy scripts via command line.
- Execute flex search queries via command line.
- Manage a list of hybris servers grouped by enviroment / stage.
- Execute scripte on multiple servers with one statement
- Use of bash completion to find configured enviroments

# Usage
```
    hac local test/test.groovy
```

# Installation
## Dependencies for execution
 - groovy
 
## Dependencies for bash completion
 - 'jq' (https://stedolan.github.io/jq/) 
 - 'perl' to parse the config file
 - 'bash-completion' to allow bash completion ;)

## Steps to do
Clone this repository.

If you are using a mac, please make sure that you have 'bash-completion' installed.
Configure the COMPLETION_DIR path in 'setup.sh' accordingly.

Run ./setup.sh to set up the system: (only run ./setup.sh, _don't use_ 'sudo ./setup.sh', that would use 'root's home directory!)
 - a .hac directory will be created in your home dir
 - there will be links set up in that directory to the files in the checkout out repository
 - there will be a link set up in /usr/local/bin
 - there will be a link for the bash completion under /etc/bash_completion.d/

The last two actions need root access, so the setup script will use 'sudo'.
Please have a look at the script _before running it_ to make sure that nothing bad will happen ;)

## Usage
hac local my-script.groovy
