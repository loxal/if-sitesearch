#!/usr/bin/env sh

gcloud auth activate-service-account --key-file=/srv/minion/compute-engine-admin.json

gcloud compute disks list --format='value(name,zone)'| while read DISK_NAME ZONE; do
  gcloud compute disks snapshot $DISK_NAME --snapshot-names ${DISK_NAME}-$(date "+%Y-%m-%d-%s") --zone $ZONE
done

if [ "$(uname)" = "Linux" ]; then
  from_date=$(date -d "-30 days" "+%Y-%m-%d")
else
  from_date=$(date -v -30d "+%Y-%m-%d")
fi
gcloud compute snapshots list --filter="creationTimestamp<$from_date" --uri | while read SNAPSHOT_URI; do
   gcloud compute snapshots delete $SNAPSHOT_URI  --quiet
done