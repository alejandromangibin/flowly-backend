# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy only pom.xml and download dependencies first (for Docker cache)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw dependency:go-offline

# Now copy the full source and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/flowly-backend-*.jar app.jar

EXPOSE 8080

# Use the Spring profile `prod` by default
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
