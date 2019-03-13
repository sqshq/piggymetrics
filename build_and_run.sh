#!/bin/bash
#1 sudo apt-get install maven
#2 https://docs.docker.com/install/linux/docker-ce/ubuntu/#install-using-the-repository
#3 https://success.docker.com/article/how-do-i-enable-the-remote-api-for-dockerd
export CONFIG_SERVICE_PASSWORD=Qwerty123!
export NOTIFICATION_SERVICE_PASSWORD=Qwerty123!
export STATISTICS_SERVICE_PASSWORD=Qwerty123!
export ACCOUNT_SERVICE_PASSWORD=Qwerty123!
export MONGODB_PASSWORD=Qwerty123!
export DOCKER_HOST=tcp://192.168.2.44:2376
printenv | grep CONFIG_SERVICE_PASSWORD
echo "INFO: Maven Package"
mvn package -DskipTests
echo "INFO: Docker Compose"
docker-compose -f docker-compose.yml up
echo "INFO: Finish!"