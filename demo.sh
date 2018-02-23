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


# Shall match the current version
SEE_BEE_VERSION=0.0.2-SNAPSHOT

MAVEN_IMAGE_VERSION=3.5-jdk-9-slim

# Working directory for See Bee
PARQUET_DIR=/tmp/parquet

# Cache of .m2 for maven, allows for faster rebuilds
M2_CACHE=/tmp/seebee/cache/m2

# See Bee is configured to use (and if necessary create) the directory "spool" under its work directory for output of finished files.
# Here we make sure it exists at startup so that the Spark container can successfully mount it.
mkdir -p ${PARQUET_DIR}/spool

# Variables needed by the maven build
export SEE_BEE_APP_PATH=applications/sql-server-parquet-application
export SEE_BEE_JAR=sql-server-parquet-application-${SEE_BEE_VERSION}-jar-with-dependencies.jar

# Rebuild the jar if it does not exist
if [ ! -e "${SEE_BEE_APP_PATH}/target/${SEE_BEE_JAR}" ]; then
    mkdir -p ${M2_CACHE}
    docker run -it --rm --name seebee-maven -v "${M2_CACHE}":/root/.m2 -v "${PWD}":/usr/src/mymaven -w /usr/src/mymaven maven:${MAVEN_IMAGE_VERSION} mvn clean -DskipTests package
    docker-compose -f demo-compose.yaml build
fi

# Open a window that displays the current state of the working directory
if (which xterm && which tree) > /dev/null; then
    xterm -e watch -t "(echo -n \"At version: \"; cat /tmp/parquet/consumed-version; echo; echo; tree /tmp/parquet/)" &
fi

docker-compose -f demo-compose.yaml up