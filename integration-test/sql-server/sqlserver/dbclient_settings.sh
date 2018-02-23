# Settings specific for this DB

# Binary of the SQL client in the docker container
CLIENT=sqsh

# DB password
PASSWORD=Password1

# DB user
USER=sa

# Options used for running the client in the docker container
OPTS="-S localhost -U ${USER} -P ${PASSWORD}"

# Command line switch used by the client to take input from the following file
SWITCH_FOR_SCRIPT=-i

# A simple SQL query that can be run to check DB connectivity
PING_SQL="SELECT 1"