#!/bin/bash
VERSION=$(cat pom.xml | grep "<version>.*-SNAPSHOT" | grep -o "[0-9.]*")
echo $VERSION
sed -i "s/VERSION = \"[0-9\.]*\";/VERSION = \"$VERSION\";/g" src/main/java/filmeUtils/MainCLI.java
mvn3 clean package release:prepare
mvn3 release:perform -Dgoals=deploy
