# Usa una imagen base oficial de Java
FROM openjdk:17-jdk-slim

# Directorio de trabajo
WORKDIR /app

# Copia tu archivo jar compilado
COPY build/libs/app-service-0.0.1.jar app.jar

# Expón el puerto por donde Spring Boot sirve
EXPOSE 8080

# Comando para ejecutar tu app
ENTRYPOINT ["java", "-jar", "app.jar"]
