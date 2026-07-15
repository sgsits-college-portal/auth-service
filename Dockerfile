
FROM eclipse-temurin:24-jdk-alpine AS build

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first to cache dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]