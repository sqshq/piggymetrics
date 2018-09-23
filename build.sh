#!/bin/bash

echo "Building piggymetrics-config ..."
mvn clean install package -f ./config/pom.xml
echo "done."

echo "Building piggymetrics-registry ..."
mvn clean install package -f ./registry/pom.xml
echo "done."

echo "Building piggymetrics-gatway ..."
mvn clean install package -f ./gateway/pom.xml
echo "done."

echo "Building piggymetrics-auth-service ..."
mvn clean install package -f ./auth-service/pom.xml
echo "done."

echo "Building piggymetrics-account-service ..."
mvn clean install package -f ./account-service/pom.xml
echo "done."

echo "Building piggymetrics-statistics-service ..."
mvn clean install package -f ./statistics-service/pom.xml
echo "done."

echo "Building piggymetrics-notification-service ..."
mvn clean install package -f ./notification-service/pom.xml
echo "done."

echo "Building piggymetrics-monitoring ..."
mvn clean install package -f ./monitoring/pom.xml
echo "done."

echo "Building piggymetrics-turbine-stream-service ..."
mvn clean install package -f ./turbine-stream-service/pom.xml
echo "done."
