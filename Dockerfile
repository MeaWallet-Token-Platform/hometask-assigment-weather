FROM openjdk:21-slim
COPY ./build/libs/weather-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-XX:+UseG1GC", "--enable-preview", "-jar", "/app.jar"]
