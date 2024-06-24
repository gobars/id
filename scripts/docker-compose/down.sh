#!/bin/bash

BASE_PATH=$(
  cd "$(dirname "$0")"
  pwd
)
cd $BASE_PATH
docker-compose down