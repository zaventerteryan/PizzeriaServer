# Use an official OpenJDK image
FROM openjdk:17-jdk

# Set the working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Build the project (if you haven’t built locally)
RUN ./gradlew installDist

# Expose your server port if needed (e.g., 8080)
EXPOSE 8080

# Set the default command — adjust YOUR_APP_NAME to match your build output
CMD ["./app/build/install/app/bin/app"]