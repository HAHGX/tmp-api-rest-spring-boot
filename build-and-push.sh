#!/bin/bash

# Script para construir y publicar la imagen Docker del desafío de Tenpo
# Uso: ./build-and-push.sh [username] [tag]

# Variables
USERNAME=${1:-"tenpo"}
TAG=${2:-"latest"}
IMAGE_NAME="tenpo-challenge"
FULL_IMAGE_NAME="$USERNAME/$IMAGE_NAME:$TAG"

echo "Construyendo la imagen Docker: $FULL_IMAGE_NAME"

# Construir la imagen
docker build -t $FULL_IMAGE_NAME .

# Verificar si la construcción fue exitosa
if [ $? -ne 0 ]; then
    echo "Error construyendo la imagen Docker"
    exit 1
fi

echo "Imagen Docker construida exitosamente"
echo "Para publicar la imagen en Docker Hub, ejecuta:"
echo "  docker login"
echo "  docker push $FULL_IMAGE_NAME"