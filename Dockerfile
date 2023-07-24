# Define the build stage
FROM openjdk:17 as build
WORKDIR /app

# Copy files and build the project
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

# Define the final stage
FROM openjdk:17-jdk-alpine
WORKDIR /app
# Correct the source path of the COPY command
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
