## Prerequisites

Jdk(8 is recommended) maven, docker, docker-compose, kompose, kubectl


## Steps
1.	Clone the code
```bash
git clone https://github.com/sqshq/PiggyMetrics
```
2. Build the code

```bash
cd PiggyMetrics
mvn package -DskipTests
```

3. Build the image

Modify the `docker-compose.dev.yml` file at the root folder, remove `rabbitmq` service and add images for each service, see the modified `docker-compose.dev.yml` at this forked repo.
Run the following command to build the docker image locally:

```bash
docker-compose -f docker-compose.dev.yml build
```
 
4. Publish the images, Login Docker hub using docker login, input your Docker hub username and password.
```bash
docker push <Your Docker hub username>/piggymetrics-config
docker push <Your Docker hub username>/piggymetrics-registry
docker push <Your Docker hub username>/piggymetrics-gateway
docker push <Your Docker hub username>/piggymetrics-auth-service
docker push <Your Docker hub username>/piggymetrics-mongodb
docker push <Your Docker hub username>/piggymetrics-account-service
docker push <Your Docker hub username>/piggymetrics-mongodb
docker push <Your Docker hub username>/piggymetrics-statistics-service
docker push <Your Docker hub username>/piggymetrics-mongodb
docker push <Your Docker hub username>/piggymetrics-notification-service
docker push <Your Docker hub username>/piggymetrics-mongodb
docker push <Your Docker hub username>/piggymetrics-monitoring
docker push <Your Docker hub username>/piggymetrics-turbine-stream-service
```
5. Modify `docker-compose.yml` at root folder, replace `sqshq/` with `<Your Docker hub username>/` to use your own images, then change the version in `docker-compose.yml` to '2', replace the unsupported statement 
```yaml
  depends_on:
      config:
        condition: service_healthy
```
to 
```yaml
  depends_on:
      - config 
```

see the modified `docker-compose.yml` at this forked repo.

6. Convert the `docker-compose.yml` into kubernetes manifest files using `kompose`

```bash
export CONFIG_SERVICE_PASSWORD=root
export NOTIFICATION_SERVICE_PASSWORD=root
export STATISTICS_SERVICE_PASSWORD=root
export ACCOUNT_SERVICE_PASSWORD=root
export MONGODB_PASSWORD=root 
kompose convert -f docker-compose.yml -o piggymetrics
``` 
  
7. start mongodb, RabbitMQ 
```bash
kubectl create -f ./piggymetrics/auth-mongodb-deployment.yaml -f ./piggymetrics/auth-mongodb-service.yaml
kubectl create -f ./piggymetrics/account-mongodb-deployment.yaml -f ./piggymetrics/account-mongodb-service.yaml
kubectl create -f ./piggymetrics/statistics-mongodb-deployment.yaml -f ./piggymetrics/statistics-mongodb-service.yaml
kubectl create -f ./piggymetrics/notification-mongodb-deployment.yaml -f ./piggymetrics/notification-mongodb-service.yaml
kubectl create -f ./piggymetrics/rabbitmq-deployment.yaml -f ./piggymetrics/rabbitmq-service.yaml

```

8. start `config-service`.
```bash
kubectl create -f ./piggymetrics/config-deployment.yaml -f ./piggymetrics/config-service.yaml
```

9. start `registry-service`
```bash
kubectl create -f ./piggymetrics/registry-deployment.yaml -f ./piggymetrics/registry-service.yaml
```

10. start other services
```bash
kubectl create -f ./piggymetrics/config-deployment.yaml -f ./piggymetrics/config-service.yaml
kubectl create -f ./piggymetrics/gateway-deployment.yaml -f ./piggymetrics/gateway-service.yaml
kubectl create -f ./piggymetrics/auth-service-deployment.yaml -f ./piggymetrics/auth-service-service.yaml
kubectl create -f ./piggymetrics/account-service-deployment.yaml -f ./piggymetrics/account-service-service.yaml
kubectl create -f ./piggymetrics/statistics-service-deployment.yaml -f ./piggymetrics/statistics-service-service.yaml
kubectl create -f ./piggymetrics/notification-service-deployment.yaml -f ./piggymetrics/notification-service-service.yaml
kubectl create -f ./piggymetrics/monitoring-deployment.yaml -f ./piggymetrics/monitoring-service.yaml
kubectl create -f ./piggymetrics/turbine-stream-service-deployment.yaml -f ./piggymetrics/turbine-stream-service-service.yaml
```

11. check the apps starts up correctly, execute 
```bash
kubectl get po
```
