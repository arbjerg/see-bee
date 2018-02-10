#!/bin/bash

SEE_BEE_VERSION=0.0.2-SNAPSHOT
PARQUET_DIR=/tmp/parquet

mkdir -p ${PARQUET_DIR}/spool

echo "Cleaning up old containers"

docker kill sparks sparku seebee sqlserver 2&> /dev/null
docker rm sparks sparku seebee sqlserver 2&> /dev/null

echo "Starting database"

#xterm -e docker stats &
#xterm -e watch -d tree ${PARQUET_DIR} &
xterm -title sqlserver -e docker run --name sqlserver -ti -p 1433:1433 db-sqlserver &

sleep 10

echo "Starting Seebee"

xterm  -geometry 200x20 -title SeeBee -e docker run --name seebee --link sqlserver:sqlserver -ti -v ${PARQUET_DIR}:/parquet namely/seebee:${SEE_BEE_VERSION} &

sleep 5

xterm -title sparks -e docker run --name sparks -ti -v ${PARQUET_DIR}/spool:/spool db-spark spark-submit /stream_table.py SWITCHES &
xterm -title sparku -e docker run --name sparku -ti -v ${PARQUET_DIR}/spool:/spool db-spark spark-submit /stream_table.py USERS &

echo "All systems up. Start inserting data to the sqlserver on localhost:1433"