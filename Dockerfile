FROM gradle:5.4.0-jdk8-alpine AS build
COPY --chown=gradle:gradle . /Users/dineshkumar/GradleProject/Repo/Osync/src
WORKDIR /Users/dineshkumar/GradleProject/Repo/Osync/src
RUN gradle build

FROM openjdk:8-jre-slim

EXPOSE 8080

RUN mkdir -p app

COPY --from=build /build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/Users/dineshkumar/GradleProject/Repo/Osync/app/spring-boot-application.jar"]
