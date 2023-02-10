# AS <NAME> to name this stage as maven
FROM maven:3.8 AS maven

WORKDIR /usr/src/app
COPY . /usr/src/app

# Compile and package the application to an executable JAR
RUN mvn package


# For Java 11,
FROM eclipse-temurin:17-alpine

ARG JAR_FILE=tetkole-backend-api.jar

WORKDIR /opt/app

COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/

ENTRYPOINT ["java","-jar","tetkole-backend-api.jar"]