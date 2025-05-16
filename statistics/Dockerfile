FROM eclipse-temurin:23-jre
ADD ./bootstrap/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
