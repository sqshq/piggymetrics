FROM amazoncorretto:8u372-al2023-jre
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

ADD ./target/statistics-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/statistics-service.jar"]

EXPOSE 7000