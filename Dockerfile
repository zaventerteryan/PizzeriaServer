# Use an official OpenJDK image
FROM openjdk:17-jdk

# Install xargs (provided by findutils)
RUN apt-get update && apt-get install -y findutils

# Set the working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Build the project
RUN ./gradlew installDist

# Expose your server port
EXPOSE 8080

# Run the app
CMD ["./app/build/install/app/bin/app"]