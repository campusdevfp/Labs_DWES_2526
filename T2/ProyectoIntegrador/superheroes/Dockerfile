# syntax=docker/dockerfile:1

# --- Build stage ---
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiamos primero los manifests para aprovechar cache
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn/ .mvn/

# Descarga de dependencias (capa cacheable)
RUN mvn -B -ntp -DskipTests dependency:go-offline

# Copiamos el código fuente y construimos el jar
COPY src/ src/
RUN mvn -B -ntp -DskipTests package

# --- Runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Por convención, Spring Boot lee env vars tipo SPRING_DATASOURCE_URL, etc.
ENV JAVA_OPTS=""

COPY --from=build /workspace/target/*SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
