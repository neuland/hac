#!/usr/bin/env bash

BASE_DIR=~/.hac
REPO_DIR=$(pwd)
BIN_DIR=/usr/local/bin
COMPLETION_DIR=/etc/bash_completion.d

[[ -d $BASE_DIR ]] || mkdir $BASE_DIR

ln -s $REPO_DIR/src/hac-completion.sh $BASE_DIR
ln -s $REPO_DIR/src/hac.sh $BASE_DIR
ln -s $REPO_DIR/src/hac.groovy $BASE_DIR
cp $REPO_DIR/src/config.json $BASE_DIR

sudo ln -s $BASE_DIR/hac-completion.sh $COMPLETION_DIR
sudo ln -s $BASE_DIR/hac.sh $BIN_DIR/hac

echo "Please source the following config file to use completion in this session."
echo "> 'source $COMPLETION_DIR/hac-completion.sh'"
echo "Completion will work automatically after restart."
