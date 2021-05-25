# build stage
FROM gradle:7.0.2-jdk11 as build-stage
WORKDIR /app
COPY . .
RUN gradle bootJar

# production stage
FROM openjdk:11 as production-stage
COPY --from=build-stage /app/build/libs/auction-0.0.1-SNAPSHOT.jar auction.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "auction.jar", "--server.port=8080"]
