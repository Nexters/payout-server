FROM eclipse-temurin:17

ARG JAR_FILE=build/libs/api-server.jar
COPY ${JAR_FILE} api-server.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod,file-logging","-Duser.timezone=UTC","/api-server.jar"]