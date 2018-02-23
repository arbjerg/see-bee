#!/bin/bash
#
#
# Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"); You may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at:
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
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
