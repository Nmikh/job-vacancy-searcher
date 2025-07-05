# Stage 1: Build the Kotlin Spring Boot application
FROM gradle:8.7-jdk21 AS build

WORKDIR /app

# Copy only files necessary for dependency resolution and build
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Download dependencies (helps with caching)
RUN gradle build -x test --no-daemon || return 0

# Copy the rest of the app
COPY . .

# Build the application
RUN gradle bootJar -x test --no-daemon

# Stage 2: Create a lightweight container to run the app
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy built JAR from previous stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]