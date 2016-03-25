FROM java:8-jre
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

ADD ./target/config.jar /app/
CMD ["java", "-jar", "/app/config.jar"]

EXPOSE 8888