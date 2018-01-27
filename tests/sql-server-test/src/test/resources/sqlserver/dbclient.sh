#!/bin/bash
#
#
# Copyright (c) 2006-2017, Speedment, Inc. All Rights Reserved.
#


# Intended for reuse by other database setups, configured by DB specific dbclient_settings.sh

source dbclient_settings.sh

while [[ $# -gt 0 ]]; do
    OPT="$1"
    case $OPT in
        -c|--check)
        CHECK="true"
        ;;
        -s|--script)
        shift
        SCRIPTS=${@}
        break
        ;;
        -w|--wait)
        WAIT="true"
    esac
    shift
done

if [[ ${WAIT} ]]; then
    while true; do
        if echo ${PING_SQL} | ${CLIENT} ${OPTS} &> /dev/null; then
            break;
        fi;
        sleep 1;
    done
    echo "Database connectivity established"
    exit
fi

if [[ ${SCRIPTS} ]]; then
    echo "Running SQL script " ${SCRIPTS}
    ${CLIENT} ${OPTS} ${SWITCH_FOR_SCRIPT} ${SCRIPTS}
    exit
fi

if [[ ${CHECK} ]]; then
    echo ${PING_SQL} | ${CLIENT} ${OPTS} &> /dev/null
else
    echo "Starting interactive prompt"
    ${CLIENT} ${OPTS}
fi
