FROM bellsoft/liberica-openjdk-debian:21

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]