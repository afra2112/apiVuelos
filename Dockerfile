# ---------- Etapa 1: Construir ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar archivos de Maven y descargar dependencias (para cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Etapa 2: Ejecutar ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiar solo el jar compilado desde la etapa anterior
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Comando para ejecutar la app
CMD ["java", "-jar", "app.jar"]
