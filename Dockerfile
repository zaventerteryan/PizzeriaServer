# Use a Debian-based OpenJDK image
FROM openjdk:17-jdk-slim

# Install xargs (provided by findutils)
RUN apt-get update && apt-get install -y findutils

# Set the working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Build the project with Gradle
RUN ./gradlew installDist

# Expose your server port
EXPOSE 8080

# Run the generated app binary
CMD ["./app/build/install/app/bin/app"]