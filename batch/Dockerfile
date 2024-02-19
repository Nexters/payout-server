FROM eclipse-temurin:17

ARG JAR_FILE=build/libs/batch.jar
COPY ${JAR_FILE} batch.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod,file-logging","-Duser.timezone=UTC","/batch.jar"]