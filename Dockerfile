# Primera etapa: Construir la aplicación
FROM maven:3.9.6-eclipse-temurin-21-alpine as builder
WORKDIR /app
COPY pom.xml .
# Descargar todas las dependencias
RUN mvn dependency:go-offline

# Copiar el código fuente y construir la aplicación
COPY src ./src
RUN mvn package -DskipTests

# Segunda etapa: Crear la imagen final
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiar el JAR construido en la primera etapa
COPY --from=builder /app/target/*.jar app.jar
# Exponer puerto
EXPOSE 8080
# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]