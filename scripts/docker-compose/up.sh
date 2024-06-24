#!/bin/bash

BASE_PATH=$(
  cd "$(dirname "$0")"
  pwd
)
cd $BASE_PATH
# Define the directory containing the Docker Compose file
DOCKER_COMPOSE_DIR="$BASE_PATH"

# Start the MySQL container
docker-compose -f ${DOCKER_COMPOSE_DIR}/docker-compose.yml up -d

# Wait for the container to start
sleep 30s

# Create the database and table
docker exec -i dbs-mysql mysql -uroot -p'root' < ${DOCKER_COMPOSE_DIR}/init/mysql.sql
docker exec -i dbs-postgres psql -U postgres < ${DOCKER_COMPOSE_DIR}/init/postgres.sql
docker exec -d dbs-oracle11g /bin/bash /init/oracle/wait-for-oracle.sh