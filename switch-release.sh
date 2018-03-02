#!/usr/bin/env sh

docker_tag=latest
docker_image_name=if-sitesearch
img_fqn=docker-registry.sitesearch.cloud/intrafind/${docker_image_name}:${docker_tag}
docker_network=sitesearch

#./gradlew clean build --info -x test
#cd service
#docker build --tag intrafind/${docker_image_name}:${docker_tag} .
#cd ..
#docker ps

isBlueUp() {
    if [ -f "./blue-green-deployment.lock" ]; then
        rm ./blue-green-deployment.lock
        return 0
    else
        touch ./blue-green-deployment.lock
        return 1
    fi
}

runService() {
    docker run -d --name $1 \
        --log-driver=gelf \
        --log-opt gelf-address=udp://localhost:12201 \
        --env ADMIN_SITE_SECRET=$ADMIN_SITE_SECRET \
        --env RECAPTCHA_SITE_SECRET=$RECAPTCHA_SITE_SECRET \
        --env SPRING_SECURITY_USER_PASSWORD=$SPRING_SECURITY_USER_PASSWORD \
        --env BUILD_NUMBER=$BUILD_NUMBER \
        --env SCM_HASH=$SCM_HASH \
        --env SECURITY_OAUTH2_CLIENT_CLIENT_SECRET=$SECURITY_OAUTH2_CLIENT_CLIENT_SECRET \
        --network $docker_network \
        intrafind/${docker_image_name}:${docker_tag}
}

startComponent() {
    docker rm -f $1
    runService $1
}

docker push $img_fqn

if isBlueUp; then
    echo "blue is active"
    green="${docker_image_name}-green"

    startComponent ${green}
    startComponent ${green}-1
    sleep 21
    docker exec router switch.sh green

else
    echo "blue is inactive"
    blue="${docker_image_name}-blue"

    startComponent ${blue}
    startComponent ${blue}-1
    sleep 21
    docker exec router switch.sh blue
fi
