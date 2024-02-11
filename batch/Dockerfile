FROM openjdk:17-temurin

ARG JAR_FILE=build/libs/batch.jar
COPY ${JAR_FILE} batch.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","-Duser.timezone=America/New_York","/batch.jar"]