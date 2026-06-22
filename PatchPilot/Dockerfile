FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY PatchPilot/pom.xml ./PatchPilot/pom.xml
COPY PatchPilot/src ./PatchPilot/src

RUN mvn -pl PatchPilot -am -DskipTests package

FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends git ca-certificates nodejs npm \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/PatchPilot/target/patchpilot-backend-*.jar /app/patchpilot-backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/patchpilot-backend.jar"]
