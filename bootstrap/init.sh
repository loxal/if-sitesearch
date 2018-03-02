#!/usr/bin/env sh

curl -s https://get.sdkman.io | bash
sdk install kotlin

kotlinc -script init.kts

