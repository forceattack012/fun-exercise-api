# stage-1 build artifact
FROM amazoncorretto:17-alpine3.19 as builder
WORKDIR /app
ADD . .
RUN ["./gradlew","bootJar"]
ENTRYPOINT ["java", "-jar", "fintech-bank-0.0.1-SNAPSHOT.jar"]





