FROM openjdk:17-temurin

ARG JAR_FILE=build/libs/api-server.jar
COPY ${JAR_FILE} api-server.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","-Duser.timezone=America/New_York","/api-server.jar"]