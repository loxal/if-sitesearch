#!/usr/bin/env sh

./gradlew \
    :gadget:clean :gadget:build \
    --parallel --continuous \
    $1
#    :finder:build \
#./gradlew :client:build  --continuous --parallel --build-cache --no-scan --continue $args