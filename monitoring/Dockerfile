FROM amazoncorretto:8u372-al2023-jre
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

ADD ./target/monitoring.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/monitoring.jar"]

EXPOSE 8080