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

PARALLELISM=5

cd "$( dirname "${BASH_SOURCE[0]}" )"

STANDARD_PARAMS="--system.loggingLevel=ALL"

COUNT=0
PARALLEL_SEMAPHORE=${PARALLELISM}

spawn_test() {
    export COMPOSE_PROJECT_NAME="pp${COUNT}"
    COUNT=$((COUNT+1))

    SEEBEE_PARAMS="${STANDARD_PARAMS} $1" ./run.sh &
    sleep 30  # spacing startup times to avoid thrashing of docker and disks

    PARALLEL_SEMAPHORE=$((PARALLEL_SEMAPHORE-1))
    if [[ ${PARALLEL_SEMAPHORE} == 0 ]]; then
        wait -n
        CODE=$?
        if [[ ${CODE} != 0 ]]; then
            return ${CODE}
        fi
        PARALLEL_SEMAPHORE=1
    fi
}

start() {
    echo "Spooling tasks"

    spawn_test "--parquet.operationTypeColumnName=CB_TYPE"
    spawn_test "--sqlserver-reactor.schemaReloadIntervalMillis=100 --sqlserver-reactor.changesPollIntervalMillis=10"
    spawn_test "--parquet.dictionaryEncodingEnabled=false"
    spawn_test "--parquet.mirrorDbSchema=false"
    spawn_test "--parquet.writeInOrder=false"

    RELOAD_TIME=2000
    POLL_TIME=500
    for STRATEGY in "SNAPSHOT" "FAST" "ISOLATED" "CONSISTENT"; do
        SP="--sqlserver-reactor.pollingStrategy=${STRATEGY}"
        PP="--sqlserver-reactor.changesPollIntervalMillis=${POLL_TIME}"
        RP="--sqlserver-reactor.schemaReloadIntervalMillis=${RELOAD_TIME}"

        spawn_test "${SP} ${PP} ${RP}"
    done
}

wait_for_all() {
    RETURN=0
    while true; do
        wait -n
        CODE=$?
        if [[ ${CODE} == 127 ]]; then
            return ${RETURN}  # No more tasks. We are done!
        elif [[ ${CODE} != 0 ]]; then
            RETURN=${CODE}
        fi
    done
}

start
RETURN=$?
if [[ ${RETURN} == 0 ]]; then
    wait_for_all
    RETURN=$?
else
    wait_for_all  # We do not care about return code since we already have one error
fi

if [[ ${RETURN} == 0 ]]; then
    echo "All parallel tests finished OK"
else
    echo "Some test failed with code ${RETURN}"
fi
exit ${RETURN}