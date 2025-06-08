#!/bin/bash

## build.sh - Script de construcción para ReVanced Kitsune Patches

# Colores para la salida
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuración
OUTPUT_DIR="../revanced_kitsune_builds"
VERSION=$(grep 'version =' build.gradle | cut -d\' -f2)
JAR_NAME="revanced-patches-kitsune-${VERSION}.jar"

# Función para mostrar mensajes de error
error_exit() {
    echo -e "${RED}[ERROR] $1${NC}" >&2
    exit 1
}

# Función para ejecutar comandos Gradle
run_gradle() {
    echo -e "${YELLOW}Ejecutando: ./gradlew $1${NC}"
    if ./gradlew $1; then
        echo -e "${GREEN}✔ Comando '$1' completado con éxito${NC}"
    else
        error_exit "Falló el comando: $1"
    fi
}

# 1. Limpieza previa
echo -e "\n${YELLOW}=== Limpiando builds anteriores ===${NC}"
run_gradle clean

# 2. Construir el proyecto
echo -e "\n${YELLOW}=== Construyendo el proyecto ===${NC}"
run_gradle build

# 3. Generar documentos (opcional)
# echo -e "\n${YELLOW}=== Generando documentación ===${NC}"
# run_gradle javadoc

# 4. Preparar directorio de salida
echo -e "\n${YELLOW}=== Preparando directorio de salida ===${NC}"
mkdir -p "$OUTPUT_DIR" || error_exit "No se pudo crear el directorio $OUTPUT_DIR"

# 5. Copiar archivos generados
echo -e "\n${YELLOW}=== Copiando archivos de salida ===${NC}"
cp "build/libs/${JAR_NAME}" "$OUTPUT_DIR/" || error_exit "Error al copiar el JAR"
cp -r "build/docs/javadoc" "$OUTPUT_DIR/" 2>/dev/null || echo -e "${YELLOW}⚠ No se encontró documentación para copiar${NC}"

# 6. Publicar en Maven Local (opcional)
# echo -e "\n${YELLOW}=== Publicando en Maven Local ===${NC}"
# run_gradle publishToMavenLocal

# 7. Resumen final
echo -e "\n${GREEN}=== ¡Construcción completada con éxito! ===${NC}"
echo -e "Versión: ${VERSION}"
echo -e "Archivo JAR: ${OUTPUT_DIR}/${JAR_NAME}"
echo -e "Tamaño del JAR: $(du -h "${OUTPUT_DIR}/${JAR_NAME}" | cut -f1)"

# 8. Opción para instalar directamente (opcional)
# read -p "¿Deseas instalar el JAR en Maven Local? (y/n) " -n 1 -r
# echo
# if [[ $REPLY =~ ^[Yy]$ ]]; then
#     run_gradle publishToMavenLocal
# fi