# Tenpo Challenge - API REST con Spring Boot

Este proyecto es una API REST desarrollada con Spring Boot para el desafío técnico de Tenpo. La API proporciona funcionalidades para realizar cálculos con porcentajes dinámicos, historial de llamadas y control de tasas.

## Descripción del Proyecto

La API implementa las siguientes funcionalidades:

1. **Cálculo con porcentaje dinámico**:
   - Recibe dos números, los suma y aplica un porcentaje adicional obtenido de un servicio externo simulado.
   - Incluye implementaciones sincrónicas y reactivas.

2. **Caché del porcentaje**:
   - El porcentaje se almacena en caché (Redis) con un tiempo de validez de 30 minutos.
   - Si el servicio externo falla, se utiliza el último valor en caché.

3. **Reintentos ante fallos**:
   - Lógica de reintento configurada para un máximo de 3 intentos con el servicio externo.

4. **Historial de llamadas**:
   - Se registra cada llamada a los endpoints de la API de forma asíncrona.
   - El historial incluye detalles como fecha, endpoint, parámetros, respuesta o error.
   - Soporta paginación para consulta del historial.

5. **Control de tasas (Rate Limiting)**:
   - La API soporta un máximo de 3 RPM (Requests Per Minute).

6. **Manejo de errores HTTP**:
   - Implementación adecuada para los errores de las series 4XX y 5XX.

## Tecnologías utilizadas

- Java 21
- Spring Boot 3.2.0
- Spring WebFlux (programación reactiva)
- Spring Data JPA
- PostgreSQL
- Redis (para caché)
- Docker y Docker Compose
- Swagger/OpenAPI (documentación)
- Bucket4j (para rate limiting)
- Flyway (migraciones de base de datos)
- JUnit y Mockito (pruebas unitarias)

## Requisitos

- Java 21 o superior
- Docker y Docker Compose
- Maven (opcional, se puede usar el wrapper de Maven incluido)

## Cómo ejecutar el proyecto

### Con Docker Compose (recomendado)

1. Clone el repositorio:

   ```bash
   git clone <url-del-repositorio>
   cd ChallengeTenpo
   ```

2. Inicie los servicios con Docker Compose:

   ```bash
   docker-compose up -d
   ```

   Esto iniciará tres contenedores:
   - **app**: La aplicación Spring Boot
   - **postgres**: Base de datos PostgreSQL
   - **redis**: Servidor Redis para caché

3. La API estará disponible en: `http://localhost:8080/api`

### Desarrollo local sin Docker

1. Asegúrese de tener PostgreSQL y Redis ejecutándose localmente:
   - PostgreSQL en puerto 5432 con una base de datos `tenpo_challenge`
   - Redis en puerto 6379

2. Configure las credenciales de la base de datos en `application.yml` si son diferentes de las predeterminadas.

3. Ejecute la aplicación:

   ```bash
   ./mvnw spring-boot:run
   ```

## Estructura del proyecto

``` bash
├── docs/
├── postman/
├── src/
│   ├── main/
│   │   ├── java/com/tenpo/challenge/
│   │   │   ├── aspects/          # Aspectos para logging asíncrono
│   │   │   ├── config/           # Configuraciones (caché, swagger, etc.)
│   │   │   ├── controller/       # Controladores REST
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Excepciones personalizadas
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── repository/       # Repositorios para acceso a datos
│   │   │   ├── service/          # Servicios con lógica de negocio
│   │   │   └── ChallengeApplication.java
│   │   └── resources/
│   │       ├── application.yml   # Configuraciones de la aplicación
│   │       └── db/migration/     # Migraciones de Flyway
│   └── test/                    # Tests unitarios
├── build-and-push.sh            # Script para construir y publicar la imagen Docker del desafío de Tenpo
├── Dockerfile                   # Instrucciones para construir la imagen Docker
├── docker-compose.yml           # Configuración de servicios para Docker Compose
└── pom.xml                      # Dependencias y configuración del proyecto
```

## Endpoints disponibles

### 1. Cálculo con porcentaje dinámico

#### Enfoque sincrónico

``` bash
POST /api/calculation
```

Request body:

```json
{
  "num1": 5.0,
  "num2": 5.0
}
```

Response (ejemplo):

```json
{
  "num1": 5.0,
  "num2": 5.0,
  "appliedPercentage": 10.0,
  "result": 11.0
}
```

#### Enfoque reactivo

``` bash
POST /api/calculation/reactive
```

### 2. Consultar historial de llamadas

``` bash
GET /api/history?page=0&size=10
```

Parámetros:

- `page`: Número de página (comenzando desde 0)
- `size`: Tamaño de página

### 3. Servicio mock de porcentaje (para pruebas)

``` bash
GET /api/mock/percentage
```

## Documentación API

La documentación de la API está disponible a través de Swagger UI:

``` bash
http://localhost:8080/api/swagger-ui.html
```

La especificación OpenAPI está disponible en:

``` bash
http://localhost:8080/api/api-docs
```

## Manejo de errores

La API maneja los siguientes errores:

- **400 Bad Request**: Cuando los parámetros de la solicitud son inválidos.
- **429 Too Many Requests**: Cuando se excede el límite de solicitudes permitidas.
- **503 Service Unavailable**: Cuando el servicio externo no responde y no hay valor en caché.
- **500 Internal Server Error**: Para errores internos no controlados.

## Mejora de Escalabilidad

Esta implementación está preparada para ejecutarse en un entorno con múltiples réplicas:

- La caché está configurada con Redis, permitiendo compartir datos entre diferentes instancias.
- El registro de historial es asíncrono para no afectar los tiempos de respuesta.
- Las migraciones de base de datos se gestionan con Flyway.

## Decisiones Técnicas

### WebFlux vs MVC

Se implementaron ambos enfoques (sincrónico y reactivo) para mostrar las capacidades de Spring WebFlux como parte del requisito bonus, aunque para esta aplicación específica un enfoque tradicional MVC podría ser suficiente.

### Caché con Redis

Se eligió Redis como solución de caché para:

- Permitir escalabilidad horizontal con múltiples instancias.
- Gestionar TTL (Time-To-Live) de forma eficiente.
- Tener alta disponibilidad para los datos en caché.

### Rate Limiting con Bucket4j

La implementación actual de rate limiting con Bucket4j es por instancia. En un entorno productivo con múltiples instancias, se recomendaría:

- Integrar con Redis para un rate limiting distribuido.
- Considerar soluciones como API Gateway para gestionar el control de tasas a nivel de infraestructura.

### Arquitectura de Logs Asíncronos

Se utilizó AspectJ para capturar todas las llamadas a los controladores de forma no intrusiva, garantizando que el registro no afecte el rendimiento de la API.

### Tests

Se implementaron tests unitarios para los componentes principales. Para un proyecto productivo se recomendaría ampliar con:

- Tests de integración con TestContainers.
- Tests de rendimiento con JMeter o similar.

## Autor

Hugo Herrera, Tech Lead - For Tenpo Challenge
