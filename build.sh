#!/bin/bash
rm -rf out build
gradle clean build -x test -Dfile.encoding=UTF-8
echo ./gradlew bootRun