# Post-contenido — Unidad 12: Despliegue y CI/CD

## Descripción
Repositorio del laboratorio de la Unidad 12 de Programación Web —
Séptimo Semestre. Contiene la aplicación Spring Boot dockerizada
con un Dockerfile multi-stage y perfiles dev/prod (Parte 1), que se
extenderá con un pipeline de integración continua con GitHub
Actions y monitoreo con Spring Boot Actuator (Parte 2).

La aplicación es la de la Unidad 9 (Usuario/Tarea con login por
sesión para las vistas Thymeleaf y API REST con JWT en `/api/**`).

## Parte 1 — Dockerización con Perfiles y Variables de Entorno
El Dockerfile multi-stage compila el proyecto con Maven en una
etapa JDK y ejecuta el JAR resultante en una etapa JRE Alpine con
un usuario no root. La configuración sensible se externaliza en
application.properties con placeholders ${VAR:default}: sin
variables de entorno, la aplicación arranca con el perfil dev
(H2 en memoria); con SPRING_PROFILES_ACTIVE=prod y las variables
DATASOURCE_URL / DATASOURCE_USER / DATASOURCE_PASSWORD y
JWT_SECRET, arranca con la configuración de producción (PostgreSQL).

## Decisiones de diseño
1. Placeholders con valor por defecto (${VAR:default}) en vez de
   placeholders estrictos: permite arrancar sin fricción en local
   y en CI, y deja los valores reales para producción. La excepción
   es `JWT_SECRET` en prod, que no tiene valor por defecto.
2. application-dev.properties y application-prod.properties como
   archivos separados en vez de un único archivo con bloques
   condicionales: hace explícito qué configuración corresponde a
   cada ambiente.
3. Imagen final eclipse-temurin JRE Alpine en vez de distroless:
   reduce el tamaño de la imagen sin sacrificar la posibilidad de
   depurar el contenedor con docker exec.

## Cómo construir y ejecutar con Docker
```bash
# Construir la imagen
docker build -t mi-spring-app:local .

# Perfil dev (valores por defecto, H2 en memoria)
docker run -d --name mi-app-dev -p 8080:8080 mi-spring-app:local

# Perfil prod con variables de entorno explícitas (PostgreSQL)
docker run -d --name mi-app-prod -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/appdb \
  -e DATASOURCE_USER=appuser \
  -e DATASOURCE_PASSWORD=apppass \
  -e JWT_SECRET=una-clave-larga-solo-para-produccion \
  mi-spring-app:local
docker logs -f mi-app-prod   # debe decir: The following 1 profile is active: "prod"
```
