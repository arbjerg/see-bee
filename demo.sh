#!/bin/bash

SEE_BEE_VERSION=0.0.2-SNAPSHOT
PARQUET_DIR=/tmp/parquet

mkdir -p ${PARQUET_DIR}/spool

docker run -it --rm --name seebee-maven -v "$PWD":/usr/src/mymaven -w /usr/src/mymaven maven:3.5-jdk-9-slim mvn clean -DskipTests package

docker-compose -f demo-compose.yaml up