#!/bin/bash
rm release.properties
mvn clean package release:prepare
mvn release:perform 
#cp target/filmeUtils.jar filmeUtils-$VERSION.jar
