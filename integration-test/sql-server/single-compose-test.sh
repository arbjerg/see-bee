#!/bin/bash

# Build JAR file
docker-compose run maven mvn -T 2C clean package

# Run tests
docker-compose run tester

# Clean up running containers and the data volume
docker-compose down -v --rmi local
