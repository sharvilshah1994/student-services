# Start with a base image containing Java runtime
FROM openjdk:8-jdk

# Add Maintainer Info
MAINTAINER "Sharvil"

# Application's env variables
ENV JAR_FILE=target/studentapp-0.0.1-SNAPSHOT.jar
ENV server.port=5000
ENV spring.datasource.url=jdbc:mysql://docker-mysql:3306/school
ENV spring.datasource.password=abc123
ENV spring.datasource.username=sharvil

# Make port 5000 available to the world outside this container
EXPOSE 5000

# Add the application's jar to the container
ADD ${JAR_FILE} student-services.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/student-services.jar"]
