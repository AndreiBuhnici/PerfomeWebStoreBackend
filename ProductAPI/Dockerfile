FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
