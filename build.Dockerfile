# Start with a base image containing Java runtime
FROM maven:3.5-jdk-8

# Add Maintainer Info
MAINTAINER "Sharvil"

# Run Maven clean install
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD . /usr/src/app
RUN mvn clean install
