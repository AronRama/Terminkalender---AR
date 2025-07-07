# Start mit Java Base Image
FROM openjdk:17-jdk-slim

# Arbeitsverzeichnis im Container
WORKDIR /app

# JAR-Datei in den Container kopieren
COPY target/*.jar app.jar

# Port freigeben (anpassen falls du was anderes als 8080 nutzt)
EXPOSE 8090

# Startbefehl
ENTRYPOINT ["java", "-jar", "app.jar"]
