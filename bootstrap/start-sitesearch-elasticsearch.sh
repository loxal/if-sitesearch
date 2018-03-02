#!/usr/bin/env sh

docker_network=sitesearch
service_name=sitesearch-elasticsearch
docker_image="intrafind/$service_name"
persistence_dir=srv
HOME=/home/alexander_orlov

if [ -f "$HOME/tmp/$service_name.tar" ]; then
    docker load -i "$HOME/tmp/$service_name.tar"

    mkdir -p ${HOME}/$persistence_dir/$service_name
    sudo chown -R 1000:1000 ${HOME}/$persistence_dir/$service_name
    sudo chmod -R 744 ${HOME}/$persistence_dir/$service_name
else
    echo $HOME/tmp/$service_name.tar file is missing
fi