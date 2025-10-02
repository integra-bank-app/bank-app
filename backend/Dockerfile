FROM gradle:9-jdk24 AS build
WORKDIR /backend
COPY . .
RUN gradle build --no-daemon

FROM eclipse-temurin:24-jre
WORKDIR /backend
COPY --from=build /backend/build/libs/backend.jar .
CMD ["java", "-jar", "backend.jar"]