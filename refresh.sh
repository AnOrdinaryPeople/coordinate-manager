#!/bin/bash

rm -rf .gradle .vscode build run
./gradlew --refresh-dependencies
./gradlew vscode
