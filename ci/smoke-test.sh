#!/usr/bin/env sh

#    --no-scan --parallel \
#    --build-cache --continue \
./gradlew clean :service:test \
    --no-scan --parallel \
    --continue \
    --tests *Smoke* \
    --info \
    $1