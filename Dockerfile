# Dockerfile multi-stage para aplicación Spring Boot

# Etapa 1: Compilación
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY src ./src

# Compilar la aplicación (saltando tests para acelerar el build)
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario no-root para mayor seguridad
RUN addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001

# Copiar el JAR compilado desde la etapa anterior
COPY --from=build /app/target/product-api-0.0.1-SNAPSHOT.jar app.jar

# Cambiar propietario del archivo JAR
RUN chown spring:spring app.jar

# Cambiar al usuario no-root
USER spring:spring

# Exponer el puerto 8080
EXPOSE 8080

# Variables de entorno por defecto (se pueden sobrescribir en Render)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 