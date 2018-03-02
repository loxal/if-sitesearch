#!/usr/bin/env sh

# add "--debug-jvm" to attach debugger
SPRING_CONFIG_NAME="application, local" ./gradlew :service:test \
    --no-scan --parallel \
    --build-cache --continuous --continue \
    $1
