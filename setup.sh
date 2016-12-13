#!/usr/bin/env bash

BASE_DIR=~/.hac
REPO_DIR=$(pwd)/src
BIN_DIR=/usr/local/bin
COMPLETION_DIR=/etc/bash_completion.d

COMPLETION_FILE=hac-completion.sh
GROOVY_FILE=hac.groovy
SHELL_WRAPPER=hac.sh
CONFIG_FILE=config.json

[[ -d "$BASE_DIR" ]] || mkdir $BASE_DIR

[[ -L "$BASE_DIR/$COMPLETION_FILE" ]] || ln -s "$REPO_DIR/$COMPLETION_FILE" "$BASE_DIR"
[[ -L "$BASE_DIR/$SHELL_WRAPPER" ]]   || ln -s "$REPO_DIR/$SHELL_WRAPPER" "$BASE_DIR"
[[ -L "$BASE_DIR/$GROOVY_FILE" ]]     || ln -s "$REPO_DIR/$GROOVY_FILE" "$BASE_DIR"
[[ -f "$BASE_DIR/$CONFIG_FILE" ]]     || cp "$REPO_DIR/$CONFIG_FILE" "$BASE_DIR"

[[ -L "$COMPLETION_DIR/$COMPLETION_FILE" ]] || sudo ln -s "$BASE_DIR/$COMPLETION_FILE" "$COMPLETION_DIR"
[[ -L "$BIN_DIR/hac" ]]                     || sudo ln -s "$BASE_DIR/$SHELL_WRAPPER" "$BIN_DIR/hac"

echo "Please source the following config file to use completion in this session."
echo "> 'source $COMPLETION_DIR/$COMPLETION_FILE'"
echo "Completion will work automatically after restart."
