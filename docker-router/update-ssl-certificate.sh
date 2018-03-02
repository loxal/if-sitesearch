#!/usr/bin/env sh

# https://certbot.eff.org

sudo apt install software-properties-common -y
sudo add-apt-repository ppa:certbot/certbot -y
sudo apt update -y
sudo apt install certbot -y

sudo certbot certonly --webroot -w /etc/letsencrypt --expand \
    -d main.sitesearch.cloud \
    -d test.sitesearch.cloud \
    -d ci.sitesearch.cloud \
    -d crawler.sitesearch.cloud \
    -d api.sitesearch.cloud \
    -d dev.sitesearch.cloud \
    -d cdn.sitesearch.cloud \
    -d doc.sitesearch.cloud \
    -d logs.sitesearch.cloud \
    -d docker-registry.sitesearch.cloud \
    -d team.sitesearch.cloud \
    -d blue.sitesearch.cloud \
    -d green.sitesearch.cloud
#    -d sitesearch.cloud \
#    -d www.sitesearch.cloud \

