FROM java:8-jre
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

ADD ./target/gateway.jar /app/
CMD ["java", "-jar", "/app/gateway.jar"]

EXPOSE 4000