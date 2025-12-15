# FROM java:8-jre
# MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

# ADD ./target/registry.jar /app/
# CMD ["java", "-Xmx200m", "-jar", "/app/registry.jar"]

# EXPOSE 8761

# ================================
# Stage 1: Build / Prepare runtime
# ================================
FROM eclipse-temurin:8-jdk-alpine AS builder

WORKDIR /app

# Copy jar from target
COPY target/registry.jar registry.jar

# Optional: verify jar
RUN java -version && ls -lh registry.jar


# ================================
# Stage 2: Distroless Runtime
# ================================
FROM gcr.io/distroless/java8-debian11

WORKDIR /app

# Copy only the jar from builder stage
COPY --from=builder /app/registry.jar registry.jar

# Expose Eureka port
EXPOSE 8761

# Run as non-root (distroless best practice)
USER nonroot

# Start application
ENTRYPOINT ["java", "-Xmx200m", "-jar", "/app/registry.jar"]
