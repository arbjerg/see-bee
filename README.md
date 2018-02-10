# See Bee


**This is work in progress**

The purpose of this repo is to track, and propagate to event streams,
all changes - updates, insert, and deletes alike - for relational 
database tables in real-time. Supported database platforms will be 
SQL Server (2017 and above) and Postgres (9.6 and above).

**Demo**

For a basic demo of end-to-end communication, first build seebee and its
docker images.

```commandline
> mvn package
```

Thus running the tests will create docker images used by the tests as well
as a docker image of See Bee, called `namely/seebee`, `db-spark` and `db-sqlserver`. 

```commandline
> docker images | head -4 | cut -f1 -d" "
REPOSITORY
namely/seebee
db-spark
db-sqlserver

```

Having these images, we can start them all to create the end-to-end demo;
We will have a See Bee container tracking changes of tables in a SQL Server 
container and creating Parquet files in spool directories watched by a streaming
Spark container. 

The plumping needed is:

1. See Bee must be able to access port 1433 of the SQL Server instance
2. See Bee must have a working directory mounted in its files system
3. The Spark container must have a sub directory of the See Bee work directory, 
the spool directory, mounted in its file system.

A script is available that does this:

```commandline
> ./demo.sh
```

Actually, this script will create four containers in total, there are two Spark 
containers, one for each tracked table.

Having the three containers running, updating the tables of the SQL Server container
(using a connection to port 1433) will yield printouts in the running Spark container
logs.

![See Bee](logo/see-bee.png)

Bee's eyes contains about 6,900 facets and are well suited for detecting movement.
Some honeybees can perceive movements that are separated by 1/300th of a second
(humans can only sense movements separated by 1/50th of a second).