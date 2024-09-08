# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Stage 2: Run the application
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/invoice-system.jar ./invoice-system.jar
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1  # Healthcheck configuration

ENTRYPOINT ["java", "-jar", "invoice-system.jar"]
