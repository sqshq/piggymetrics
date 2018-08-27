FROM java:8-jre
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

ADD ./target/auth-service.jar /app/
CMD ["java", "-Xmx400m", "-jar", "/app/auth-service.jar"]

EXPOSE 5000
