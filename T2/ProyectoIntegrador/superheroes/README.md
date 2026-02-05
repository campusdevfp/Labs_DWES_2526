# Superheroes (Spring Boot + Angular + MySQL)

Monorepo con:
- **Backend**: Spring Boot (Java 21), API REST + JWT
- **Frontend**: Angular (sirviendo SPA)
- **Base de datos**: MySQL 8

La forma recomendada de ejecutar el proyecto es con **Docker Compose**, tanto en **PROD** (imágenes inmutables) como en **DEV** (hot reload en contenedores).

---

## URLs rápidas (DEV vs PROD)

> Regla mental: **en PROD entra siempre por el frontend (Nginx)**, y desde ahí llama al backend por proxy. En DEV puedes entrar por frontend y backend por separado.

| Servicio | PROD (compose.prod.yml) | DEV (compose.dev.yml) |
|---|---|---|
| Frontend | http://localhost:4200 | http://localhost:4200 |
| Backend (directo) | http://localhost:8080 | http://localhost:8080 |
| Backend vía proxy (recomendado para el navegador) | http://localhost:4200/* | (solo si configuras proxy en `ng serve`) |
| Swagger UI | http://localhost:4200/swagger-ui-rest.html *(proxy)* | http://localhost:8080/swagger-ui-rest.html *(directo)* |
| OpenAPI JSON | http://localhost:4200/v3/api-docs *(proxy)* | http://localhost:8080/v3/api-docs *(directo)* |
| MySQL | host: `localhost`, puerto: `3306` (si lo expones) | host: `localhost`, puerto: `3306` |

### Rutas importantes (cuando entras por el frontend, puerto 4200)

Estas rutas las sirve **Nginx**:
- **SPA (Angular Router):** `GET http://localhost:4200/`, `GET http://localhost:4200/register`, etc.
- **API (proxy al backend):** `GET/POST http://localhost:4200/api/...`

Endpoints más usados vía proxy:
- `POST http://localhost:4200/api/authenticate`
- `POST http://localhost:4200/api/register`
- `GET  http://localhost:4200/api/v1/...`
- `GET  http://localhost:4200/api/swagger-ui-rest.html`
- `GET  http://localhost:4200/api/v3/api-docs`

> Así evitamos la colisión entre la ruta SPA `GET /register` y el endpoint backend `POST /register`.

---

## Requisitos

### Opción A (recomendada): Docker
- Docker Desktop (con `docker compose`)

### Opción B (sin Docker)
- JDK 21
- Maven (o usar `mvnw.cmd`)
- Node.js 20+
- MySQL 8 local

---

## Arranque con Docker Compose

### 1) Variables de entorno

Copia el ejemplo y ajusta si quieres:

```powershell
Copy-Item .env.example .env
```

Valores habituales:
- `BACKEND_PORT` (por defecto `8080`)
- `FRONTEND_PORT` (por defecto `4200`)
- `MYSQL_PORT` (por defecto `3306`)
- `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD`
- `JWT_SECRET`

---

## Modo PROD (recomendado)

En PROD se construyen imágenes reproducibles:
- Backend: se compila el `.jar` dentro de Docker (multi-stage) y se ejecuta con JRE 21.
- Frontend: se compila Angular y se sirve con **Nginx**.
- Nginx hace **reverse proxy** hacia el backend para evitar problemas de CORS.

Arranque:

```powershell
cd D:\ws\Spring-Boot-and-Angular\Chapter-17\superheroes

docker compose -f compose.prod.yml up --build
```

URLs:
- Frontend: http://localhost:4200
- Backend (directo): http://localhost:8080
- Swagger UI (vía proxy desde el frontend): http://localhost:4200/swagger-ui-rest.html

### Cómo funciona el proxy en PROD
El contenedor `frontend` (Nginx) reenvía:
- `/api/*` -> `backend:8080/*` (incluye `/api/v1/*`, `/authenticate` y `/register` vía `/api/...`)

Por eso el Angular usa URLs relativas:
- API REST: `apiURL: "/api/v1"`
- Auth/Register: `POST /api/authenticate` y `POST /api/register`

---

## Modo DEV (hot reload con contenedores)

En DEV se prioriza iterar rápido:
- MySQL: contenedor
- Backend: contenedor Maven ejecutando `mvn spring-boot:run` con el código montado
- Frontend: contenedor Node ejecutando `ng serve` con el código montado

Arranque:

```powershell
cd D:\ws\Spring-Boot-and-Angular\Chapter-17\superheroes

docker compose -f compose.dev.yml up
```

URLs:
- Frontend DEV: http://localhost:4200
- Backend DEV: http://localhost:8080

Notas DEV:
- En Windows puede fallar la detección de cambios de ficheros; por eso está activado `CHOKIDAR_USEPOLLING=true`.
- En DEV, si notas que Angular no llega a la API, lo más limpio es añadir proxy de `ng serve` (pendiente si lo quieres). Alternativa: apuntar a `http://localhost:8080`.

---

## Apagar y limpiar

Parar (mantener volúmenes):

```powershell
docker compose -f compose.prod.yml down
docker compose -f compose.dev.yml down
```

Parar y borrar datos (⚠️ borra el volumen de MySQL):

```powershell
docker compose -f compose.prod.yml down -v
docker compose -f compose.dev.yml down -v
```

---

## Ejecutar sin Docker (opcional)

### Backend
1. Arranca MySQL local y crea la DB (por defecto `springDevDb`).
2. Edita `src/main/resources/application.properties` si no usas el usuario/puerto por defecto.
3. Compila/ejecuta:

```powershell
cd D:\ws\Spring-Boot-and-Angular\Chapter-17\superheroes
.\mvnw.cmd spring-boot:run
```

### Frontend

```powershell
cd D:\ws\Spring-Boot-and-Angular\Chapter-17\superheroes\frontend
npm ci
npm start
```

---

## Endpoints útiles

- `POST /authenticate` (login)
- `POST /register` (registro)
- `GET /swagger-ui-rest.html`
- `GET /v3/api-docs`

> Nota: rutas como `GET /register` o `GET /login` son **rutas del router de Angular** (SPA). En PROD se sirven como `index.html` y no deben confundirse con los endpoints `POST /register` y `POST /authenticate`.

---

## Troubleshooting

### MySQL no arranca / puerto ocupado
- Cambia `MYSQL_PORT` en `.env`.

### El frontend recibe 403 en `/authenticate`
- En PROD debería ir por el proxy del frontend (mismo origen). Asegúrate de entrar por `http://localhost:4200`.
- Revisa `client.url` en backend (CORS). En compose prod se inyecta para permitir el origen del frontend.

### El backend no conecta a MySQL en Docker
- En Docker el host de MySQL es el nombre del servicio: `mysql`.
- Comprueba que `mysql` está healthy (healthcheck).

---

## Infraestructura (resumen)

- Contenedor `mysql`: MySQL 8 + volumen persistente
- Contenedor `backend`: Spring Boot (Java 21)
- Contenedor `frontend`: Nginx sirviendo Angular + reverse proxy a backend

---

### Documentación adicional
- Docker: ver también `README.docker.md`
