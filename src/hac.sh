#!/usr/bin/env bash

currentLang=$LANG
unset LANG
tmpFile=`cat /dev/urandom | tr -cd 'a-f0-9' | head -c 32`
export LANG=$currentLang

if [[ $# -eq 3 ]]; then
    mkdir -p ~/.hac/tmp
    cat - > ~/.hac/tmp/$tmpFile
    exec </dev/tty >/dev/tty
    groovy ~/.hac/hac.groovy --env $1 --file ~/.hac/tmp/$tmpFile $2 $3
else
    groovy ~/.hac/hac.groovy --env $1 --file $2
fi

rm -f ~/.hac/tmp/$tmpFile
