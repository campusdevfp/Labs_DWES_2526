# --- Etapa 1: Build ---
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el descriptor de proyecto primero (para cachear dependencias)
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copiamos el código fuente y compilamos el proyecto
COPY src ./src

# Construimos el JAR ejecutable con el manifest correcto
RUN mvn -q -DskipTests package spring-boot:repackage


# --- Etapa 2: Runtime ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el JAR resultante desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto de la aplicación
EXPOSE 8080

# Pequeña espera opcional (para que MySQL esté listo antes de conectar)
# ENTRYPOINT ["/bin/sh", "-c", "sleep 15 && java -jar /app/app.jar"]

# Arranque directo (sin delay)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
