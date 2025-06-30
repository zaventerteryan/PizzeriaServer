# Use a JVM base image
FROM openjdk:17-jdk

# Set working dir
WORKDIR /app

# Copy everything
COPY . .

# Build the app
RUN ./gradlew installDist

# Run the server
CMD ["./build/install/PizzeriaServer/bin/PizzeriaServerVerified"]