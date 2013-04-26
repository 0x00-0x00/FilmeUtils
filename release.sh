#!/bin/bash
rm release.properties
VERSION=$(cat pom.xml | grep "<version>.*-SNAPSHOT" | grep -o "[0-9.]*")
VERSION_FILE=README.md
echo $VERSION
sed -i "s/v[0-9]\.[0-9]/v$VERSION/g" $VERSION_FILE
git add $VERSION_FILE
git commit -m"New version on readme"
mvn clean package release:prepare
mvn release:perform 
rm release.properties
