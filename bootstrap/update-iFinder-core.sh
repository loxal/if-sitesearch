#!/usr/bin/env sh

sh ./bootstrap/start-sitesearch-elasticsearch.sh
sh ./bootstrap/start-sitesearch-search-service.sh

export HOME=/home/alexander_orlov
docker-compose --file ./opt/docker-compose-sitesearch.yaml -p sitesearch down
docker-compose --file ./opt/docker-compose-sitesearch.yaml -p sitesearch up -d