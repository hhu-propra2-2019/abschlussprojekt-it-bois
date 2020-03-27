FROM gradle:jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar --no-daemon

FROM openjdk:11-jre-slim
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/gruppen2.jar
COPY --from=build /home/gradle/src/wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh
#ENTRYPOINT ["java"]
#CMD ["-Dspring.profiles.active=docker", "-jar", "/app/gruppen2.jar"]
