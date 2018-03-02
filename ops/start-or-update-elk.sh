#!/usr/bin/env sh

sudo sysctl -w vm.max_map_count=262144

docker-compose --file opt/docker-compose-elk.yaml -p tmp down
docker-compose --file opt/docker-compose-elk.yaml -p tmp up -d --force-recreate
docker-compose --file opt/docker-compose-elk.yaml -p tmp ps

# Provide Basic License
#docker exec -it ops-elasticsearch bash
#vi license-sitesearch.json # paste license
#curl -X PUT -u elastic http://localhost:9200/_xpack/license -H "Content-Type: application/json" -d @license-*.json

# Delete index
#curl localhost:9200/_cat/indices?v
#curl -X DELETE localhost:9200/logstash-2017.11.20?pretty