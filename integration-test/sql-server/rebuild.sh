#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
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
docker run -it --rm --name seebee-maven ${M2} -v ${PATH_TO_TOP}:/usr/src/seebee \
    -w /usr/src/seebee maven:${MAVEN_IMAGE_VERSION} mvn -T 2C package

DOCKER_RETURN_CODE=$?
if [[ ${DOCKER_RETURN_CODE} != 0 ]]; then
    echo "See Bee build failed"
    exit ${DOCKER_RETURN_CODE}
fi

# Rebuild containers
docker-compose build
