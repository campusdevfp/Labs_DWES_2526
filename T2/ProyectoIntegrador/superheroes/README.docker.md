# Docker (DEV + PROD)

Este proyecto tiene:
- Backend: Spring Boot (Java 21)
- Frontend: Angular
- DB: MySQL

## PROD (imágenes inmutables: Angular en Nginx + backend jar)

- Frontend: http://localhost:4200
- Backend (directo): http://localhost:8080
- Swagger UI: http://localhost:4200/swagger-ui-rest.html (proxy)

```powershell
cd D:\ws\Spring-Boot-and-Angular\Chapter-17\superheroes
Copy-Item .env.example .env
# (edita .env si quieres)

docker compose -f compose.prod.yml up --build
```

### Cómo funciona la API en PROD
El frontend (Nginx) hace reverse proxy:
- `/api/*`  -> `backend:8080/api/*`
- `/authenticate` y `/register` -> backend

Así Angular puede llamar a rutas relativas (o seguir usando las absolutas), y evitamos problemas de CORS.

## DEV (hot reload con contenedores)

```powershell
cd D:\ws\Spring-Boot-and-Angular\Chapter-17\superheroes
Copy-Item .env.example .env

docker compose -f compose.dev.yml up
```

Notas:
- En DEV el backend corre con `mvn spring-boot:run` dentro del contenedor Maven.
- El frontend corre con `ng serve` dentro de Node.
- Si el watcher en Windows da problemas, se está forzando polling con `CHOKIDAR_USEPOLLING=true`.

## Apagar y limpiar

```powershell
docker compose -f compose.prod.yml down -v
docker compose -f compose.dev.yml down -v
```
