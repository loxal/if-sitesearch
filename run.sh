#!/usr/bin/env sh

{
    SPRING_CONFIG_NAME="application, local" ./gradlew bootRun \
        --continue --continuous --no-scan --parallel --build-cache \
        $1
} || {
    hangingJavaProcessToStop=`jps | grep Application | awk '{print $1}'`
    kill -9 $hangingJavaProcessToStop
    echo "Gracefully killed hanging process: $hangingJavaProcessToStop"
}