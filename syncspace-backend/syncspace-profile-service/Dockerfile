# Stage 1: Build the JAR
FROM gradle:9.2.1-jdk21 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle buildFatJar --no-daemon

# Stage 2: Minimal Runtime (ARM64 & AMD64 compatible)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/profile-service.jar
EXPOSE 50051
ENTRYPOINT ["java", "-jar", "/app/profile-service.jar"]