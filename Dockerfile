# Use a base image with Java 17 (or your required version)
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built jar from your local machine to the container
COPY target/rag-docker.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]