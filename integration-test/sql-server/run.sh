#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"

if [[ ! -v COMPOSE_PROJECT_NAME ]]; then
    export COMPOSE_PROJECT_NAME=parquet
fi

# Tests assume empty spool directories, so we make sure the volume is deleted even if previous tests have been aborted
docker volume rm ${COMPOSE_PROJECT_NAME}_data 2&> /dev/null || true

export TESTER_CONTAINER_NAME=${COMPOSE_PROJECT_NAME}_tester
export SEEBEE_CONTAINER_NAME=${COMPOSE_PROJECT_NAME}_seebee

echo "${COMPOSE_PROJECT_NAME}: Setting up"

if ! docker-compose up -d >/dev/null; then
    echo "Starting failed. Rebuilding."
    if ! ./rebuild.sh; then
        exit $?
    fi
    if ! docker-compose up -d; then
        echo "Rebuilding did not help. Panic."
        exit 1
    fi
fi

echo "${COMPOSE_PROJECT_NAME}: Running tests"

TESTER_EXIT_CODE=$(docker wait ${TESTER_CONTAINER_NAME})

RESULT=2
if [[ ${TESTER_EXIT_CODE} != 0 ]]; then
    echo "Tests failed with code ${TESTER_EXIT_CODE}"
    echo "--- Tester:"
    docker logs ${TESTER_CONTAINER_NAME}
    echo "--- Seebee:"
    docker logs ${SEEBEE_CONTAINER_NAME}
    echo "---"
    RESULT=1
else
    docker logs ${TESTER_CONTAINER_NAME} | grep "Tests run:"
    echo "${COMPOSE_PROJECT_NAME}: All Tests OK"
    RESULT=0
fi

docker-compose down -v --rmi local

exit ${RESULT}
