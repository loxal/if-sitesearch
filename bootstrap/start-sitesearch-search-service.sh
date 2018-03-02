#!/usr/bin/env sh

docker_network=sitesearch
service_name=sitesearch-search-service
docker_image="intrafind/$service_name"
persistence_dir=srv
HOME=/home/alexander_orlov

#docker network create $docker_network

if [ -f "$HOME/tmp/$service_name.tar" ]; then
#if(Test-Path $HOME/tmp/$service_name.tar){
    docker load -i "$HOME/tmp/$service_name.tar"

#    mkdir -p $HOME/$persistence_dir/$service_name
#    sudo chown -R 1000:1000 $HOME/$persistence_dir/$service_name
#    sudo chmod -R 744 $HOME/$persistence_dir/$service_name
#
#    docker rm -f $service_name
#    docker run -d --name $service_name --network $docker_network `
#        -p 9605:9605 `
#        -v $HOME/$persistence_dir/$service_name/logs:/home/app_user/logs `
#        $docker_image
else
    echo $HOME/tmp/$service_name.tar file is missing
fi