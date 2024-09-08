# Using Maven image to build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Use the official OpenJDK image to run the application
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/invoice-system.jar ./invoice-system.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "invoice-system.jar"]
