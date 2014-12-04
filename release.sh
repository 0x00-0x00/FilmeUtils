#!/bin/bash
rm release.properties
mvn clean package release:prepare
mvn release:perform 
rm release.properties
