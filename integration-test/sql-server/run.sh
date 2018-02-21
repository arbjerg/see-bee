#!/bin/bash

TOP_DIR=../..
MAVEN_IMAGE_VERSION=3.5-jdk-9-slim
export COMPOSE_PROJECT_NAME=parquet

# docker needs absolute paths for volumes
PATH_TO_TOP=`(cd ${TOP_DIR}; pwd)`

# The environment variable SEEBEE_CACHE can be used to point to a persistent cache for build dependencies
# Setting SEEBEE_CACHE=/tmp/seebee for example, will use that directory to store all jar dependencies used
# to build SeeBee.
if [[ -v SEEBEE_CACHE ]]; then
    M2="-v ${SEEBEE_CACHE}/m2:/root/.m2"
else
    M2=""
fi

# Rebuild all SeeBee jar files
docker run -it --rm --name seebee-maven ${M2} -v ${PATH_TO_TOP}:/usr/src/seebee -w /usr/src/seebee maven:${MAVEN_IMAGE_VERSION} mvn -T 2C -DskipTests clean package

DC="docker-compose -f docker/docker-compose.yaml"

# Tests assume empty spool directories, so we make sure the volume is deleted
docker volume rm ${COMPOSE_PROJECT_NAME}_data 2&> /dev/null || true

#${DC} build >/dev/null
${DC} up -d >/dev/null

echo "Running tests"

TESTER_EXIT_CODE=$(docker wait tester)

RESULT=2
if [[ ${TESTER_EXIT_CODE} != 0 ]]; then
    echo Tests failed with code ${TESTER_EXIT_CODE}
    echo "--- Tester:"
    docker logs tester
    echo "--- Seebee:"
    docker logs seebee
    echo "---"
    RESULT=1
else
    docker logs tester | grep "Tests run:"
    echo "Tests OK"
    RESULT=0
fi

${DC} stop
${DC} rm -f -v

echo -n "Removing volume: "; docker volume rm ${COMPOSE_PROJECT_NAME}_data

exit ${RESULT}
