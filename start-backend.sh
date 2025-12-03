#!/bin/bash

# Script para iniciar el backend Spring Boot
# Configuramos Java 17 y ejecutamos el servidor

echo "Iniciando Backend Spring Boot - Huerto Hogar"
echo "=============================================="
echo ""

# Configuramos JAVA_HOME para usar Java 17
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

# Verificamos que Java 17 está disponible
if [ ! -d "$JAVA_HOME" ]; then
    echo "Error: Java 17 no encontrado en $JAVA_HOME"
    echo "Por favor, instala Java 17 o actualiza la ruta en este script."
    exit 1
fi

echo "Usando Java: $JAVA_HOME"
$JAVA_HOME/bin/java -version
echo ""

# Iniciamos el servidor con profile local-mysql
echo "Iniciando servidor en http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Base de datos: MySQL (Docker) - puerto 3307"
echo "Database: huerto-hogar"
echo ""
echo "Presiona Ctrl+C para detener el servidor"
echo "=============================================="
echo ""

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
$JAVA_HOME/bin/java -version
./mvnw spring-boot:run -Dspring-boot.run.profiles=local-mysql -Dmaven.compiler.fork=true

