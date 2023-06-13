#!/usr/bin/env bash
# copy the hac.sh into your bin folder and change correct location of hac.groovy and config.json

groovy hac.groovy --configfile config.json $@
