#!/bin/bash
rm release.properties
VERSION=$(cat pom.xml | grep "<version>.*-SNAPSHOT" | grep -o "[0-9.]*")
VERSION_FILE=src/main/java/filmeUtils/Version.java
echo $VERSION
sed -i "s/VERSION = \"[0-9\.]*\";/VERSION = \"$VERSION\";/g" $VERSION_FILE
git add $VERSION_FILE
git commit -m"New version"
mvn clean package release:prepare
mvn release:perform 
cp target/filmeUtils.jar filmeUtils-$VERSION.jar
