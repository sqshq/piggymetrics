FROM java:8-jre
ADD ./target/notification-service.jar /app/
CMD ["java", "-jar", "/app/notification-service.jar"]

RUN java -jar /app/notification-service.jar

EXPOSE 8888