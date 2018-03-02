#!/usr/bin/env sh

docker-compose --file opt/docker-compose-infrastructure.yaml -p tmp down
docker-compose --file opt/docker-compose-infrastructure.yaml -p tmp up -d --force-recreate
docker-compose --file opt/docker-compose-infrastructure.yaml -p tmp ps
