#!/usr/bin/env bash
DIRNAME=`dirname "$(readlink -f "$0")"`
groovy $DIRNAME/hac.groovy --env $1 --file $2
