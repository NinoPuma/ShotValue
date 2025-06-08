# Etapa 1: Construcción del artefacto con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiamos el POM y el código fuente
COPY pom.xml .
COPY src ./src

# Ejecutamos el package sin tests para generar el JAR
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución con JDK
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiamos el JAR generado desde la etapa de build
COPY --from=builder /app/target/analizador-xgot-0.0.1-SNAPSHOT.jar app.jar

# Puerto en el que escucha la aplicación Spring Boot
EXPOSE 8080

# Comando para arrancar el contenedor ejecutando el JAR
ENTRYPOINT ["java", "-jar", "app.jar"]

# ------------------------------------------------------------
# Para crear la imagen:
#   docker build -t analizador-xgot:latest .
#
# Para ejecutar el contenedor en background:
#   docker run -d \
#     --name analizador-xgot \
#     -p 8080:8080 \
#     analizador-xgot:latest
#
# Comprobar contenedor en marcha:
#   docker ps --filter "name=analizador-xgot"
# Detener y eliminar el contenedor:
#   docker stop analizador-xgot && docker rm analizador-xgot
# ------------------------------------------------------------