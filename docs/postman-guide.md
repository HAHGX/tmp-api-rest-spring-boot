# Guía para utilizar la colección de Postman

Este proyecto incluye una colección de Postman que facilita las pruebas de los endpoints de la API del desafío técnico de Tenpo.

## Importar la colección

1. Abre Postman.
2. Haz clic en "Import" en la esquina superior izquierda.
3. Arrastra el archivo `postman/Tenpo_Challenge_Collection.json` o selecciónalo navegando por tu sistema de archivos.
4. Confirma la importación.

## Configurar el entorno

La colección usa una variable de entorno `baseUrl` que por defecto está configurada como `http://localhost:8080/api`.

Para personalizar esta configuración:

1. En Postman, haz clic en "Environments" en la barra lateral.
2. Crea un nuevo entorno (por ejemplo, "Tenpo Challenge").
3. Agrega la variable `baseUrl` con el valor que corresponda a tu entorno.
4. Guarda y selecciona este entorno antes de ejecutar las peticiones.

## Estructura de la colección

La colección está organizada en las siguientes carpetas:

1. **Calculation**: Endpoints para realizar cálculos
   - `Calculate (Synchronous)`: Realiza un cálculo utilizando el enfoque sincrónico
   - `Calculate (Reactive)`: Realiza un cálculo utilizando el enfoque reactivo de WebFlux

2. **History**: Endpoints para consultar el historial de llamadas
   - `Get API Call History`: Consulta el historial de llamadas a la API con paginación

3. **Mock External Service**: Endpoints del servicio mock
   - `Get Percentage`: Obtiene un porcentaje aleatorio del servicio mock

4. **Rate Limit Test**: Pruebas para el control de tasas
   - `Test Rate Limit`: Endpoint para probar la funcionalidad de rate limiting

## Probar el Rate Limiting

Para probar la funcionalidad de rate limiting:

1. Selecciona la petición "Test Rate Limit" en la carpeta "Rate Limit Test".
2. Haz clic en "Send" varias veces en rápida sucesión.
3. Observa cómo después de 3 solicitudes (configuración predeterminada), las siguientes solicitudes devuelven un error 429 (Too Many Requests).
4. Las peticiones incluyen un script de test que registra estadísticas sobre cuántas solicitudes tuvieron éxito y cuántas fueron bloqueadas por el rate limiting.

## Explorar la API

Utiliza la colección para explorar todas las funcionalidades implementadas en el desafío técnico:

- Prueba diferentes combinaciones de números en los endpoints de cálculo.
- Explora el historial de llamadas después de realizar varias solicitudes.
- Observa cómo funciona la caché realizando varias llamadas mientras el servicio mock ocasionalmente simula fallos.

La colección te permitirá verificar de forma rápida y sencilla que la implementación cumple con todos los requisitos del desafío técnico.
