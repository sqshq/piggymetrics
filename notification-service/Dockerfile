FROM java:8-jre
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

ADD ./target/notification-service.jar /app/
CMD ["java", "-jar", "/app/notification-service.jar"]

EXPOSE 8000