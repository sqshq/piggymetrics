@echo off
@setlocal
:: 1. Check the repository is cloned locally without auto CRLF change

::Define LF variable containing a linefeed (0x0A)
set LF=^


::Above 2 blank lines are critical - do not remove
@setlocal enabledelayedexpansion
::Define CR variable containing a carriage return (0x0D)
for /f %%a in ('copy /Z "%~dpf0" nul') do set "CR=%%a"
findstr /n /r /c:"bash!CR!!LF!" "%~dp0\mongodb\init.sh"  > nul
if not errorlevel 1 (echo please remove CR in "%~dp0\mongodb\init.sh"
pause && exit /B)


if not defined GITHUB_USER_NAME (
echo Unable to detect your docker hub username, please run: set GITHUB_USER_NAME=^<your docker hub username^>
pause && exit /B
)

:: 2. Check local docker is RUNNING
WHERE docker > nul
if errorlevel 1 (echo Unable to detect docker command, please install docker first
    pause && exit /B)
WHERE kompose > nul
if errorlevel 1 (echo Unable to detect kompose command, please install kompose first
    pause && exit /B)

WHERE helm > nul
if errorlevel 1 (echo Unable to detect helm command, please install helm first
    pause && exit /B)

docker images > nul
if errorlevel 1 (echo Unable to detect docker instance running, please start docker daemon first
    pause && exit /B)


:: 2. Check AZURE AKS is here
WHERE kubectl > nul
if errorlevel 1 (echo Unable to detect kubectl command, please install kubectl first
    pause && exit /B)

call kubectl version > nul
if errorlevel 1 (echo Unable to detect a Kubernetes cluster, please connect to an existing Kubernetes cluster
    pause && exit /B)


:: 2. Check maven is here
call mvn -v> nul
if errorlevel 1 (echo Unable to detect maven in your machine please install maven at https://maven.apache.org/
    pause && exit /B)


echo Executing mvn build...
mvn package -DskipTests -B -V


echo Building docker images

if not defined GITHUB_USER_NAME (
echo Unable to detect your docker hub username, please run: set GITHUB_USER_NAME=^<your docker hub username^>
pause && exit /B
) else (

cls
echo Building docker images
:: build image piggymetrics-mongodb
docker build .\mongodb -t %GITHUB_USER_NAME%/piggymetrics-mongodb
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-mongodb


if errorlevel 1 (echo Cannot push images, have you logged in the dockerhub?
    pause && exit /B)
)


:: build image piggymetrics-config
docker build .\config -t %GITHUB_USER_NAME%/piggymetrics-config
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-config

:: build image piggymetrics-registry
docker build .\registry -t %GITHUB_USER_NAME%/piggymetrics-registry
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-registry

:: build image piggymetrics-gateway
docker build .\gateway -t %GITHUB_USER_NAME%/piggymetrics-gateway
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-gateway

:: build image piggymetrics-auth-service
docker build .\auth-service -t %GITHUB_USER_NAME%/piggymetrics-auth-service
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-auth-service

:: build image piggymetrics-account-service
docker build .\account-service -t %GITHUB_USER_NAME%/piggymetrics-account-service
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-account-service

:: build image piggymetrics-statistics-service
docker build .\statistics-service -t %GITHUB_USER_NAME%/piggymetrics-statistics-service
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-statistics-service

:: build image piggymetrics-notification-service
docker build .\notification-service -t %GITHUB_USER_NAME%/piggymetrics-notification-service
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-notification-service

:: build image piggymetrics-monitoring
docker build .\monitoring -t %GITHUB_USER_NAME%/piggymetrics-monitoring
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-monitoring

:: build image piggymetrics-turbine-stream-service
docker build .\turbine-stream-service -t %GITHUB_USER_NAME%/piggymetrics-turbine-stream-service
if errorlevel 1 (echo Cannot build image on your machine.
    pause && exit /B)
docker push %GITHUB_USER_NAME%/piggymetrics-turbine-stream-service

@echo off
if not exist output (
    mkdir output
) else (
    rmdir /Q /S output
    mkdir output
)
powershell -Command "(gc docker-compose.yml) -replace '<Your Docker hub username>', '%GITHUB_USER_NAME%' | Out-File output\docker-compose.yml"


@setlocal
if not defined CONFIG_SERVICE_PASSWORD (
set CONFIG_SERVICE_PASSWORD=root
)
if not defined NOTIFICATION_SERVICE_PASSWORD (
set NOTIFICATION_SERVICE_PASSWORD=root
)
if not defined STATISTICS_SERVICE_PASSWORD (
set STATISTICS_SERVICE_PASSWORD=root
)

if not defined ACCOUNT_SERVICE_PASSWORD (
set ACCOUNT_SERVICE_PASSWORD=root
)
if not defined MONGODB_PASSWORD (
set MONGODB_PASSWORD=root
)
kompose convert -f output\docker-compose.yml -o output -c
cls
echo 1. deploy using helm chart
helm install .\output
echo you have successfully deployed the Piggy Metrics to your Kubernetes cluster.
)
