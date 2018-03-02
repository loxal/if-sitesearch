#!/usr/bin/env sh

SITE_CRAWL_STATUS_FILE=site-crawl-status.json

# get all sites crawl status
curl -X GET \
    "https://api.sitesearch.cloud/sites/crawl/status?serviceSecret=$ADMIN_SITE_SECRET" \
    -o $SITE_CRAWL_STATUS_FILE

# apply fetched site status to run the crawler
curl -X POST \
    "https://api.sitesearch.cloud/sites/crawl?serviceSecret=$ADMIN_SITE_SECRET" \
    -H 'content-type: application/json' \
    -T $SITE_CRAWL_STATUS_FILE

# update local crawl status
curl -X GET \
    "https://api.sitesearch.cloud/sites/crawl/status?serviceSecret=$ADMIN_SITE_SECRET" \
    -o $SITE_CRAWL_STATUS_FILE

# TODO additionally introduce check for site ID
if grep -q `date -I` $SITE_CRAWL_STATUS_FILE; then
    echo CRAWLING_SUCCESS
else
    echo CRAWLING_FAILED
fi