# Utilisation d'une image de base avec Java
FROM eclipse-temurin:17-jdk

# Définition du répertoire de travail
WORKDIR /app

# Copie du fichier JAR généré par Maven
COPY target/*.jar app.jar

# Installation des dépendances nécessaires pour JavaFX
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libglu1-mesa \
    xorg \
    libgl1-mesa-glx \
    && rm -rf /var/lib/apt/lists/*

# Variables d'environnement pour JavaFX
ENV DISPLAY=:0

# Commande pour exécuter l'application
CMD ["java", "-jar", "app.jar"]