# hac
hybris Administrator Console

## Installation
To use the config completion, https://stedolan.github.io/jq/ has to be installed.

Clone the repository.

Run ./setup.sh to set up the system:
 - a .hac directory will be created in your home dir
 - there will be links set up in that directory to the files in the checkout out repository
 - there will be a link set up in /usr/local/bin
 - there will be a link for the bash completion under /etc/bash_completion.d/

The last two actions need root access, so the setup script will use 'sudo'.
Please have a look at the script _before running it_ to make sure that nothing bad will happen ;)

## Usage
hac local my-script.groovy
