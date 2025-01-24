FROM gradle:8.11.1-jdk21 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY build.gradle.kts settings.gradle.kts $APP_HOME
COPY gradle /app/gradle
RUN gradle dependencies --no-daemon
COPY . .
RUN gradle clean build -x test --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=TEMP_BUILD_IMAGE /app/build/libs/tasklist-0.0.1-SNAPSHOT.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]