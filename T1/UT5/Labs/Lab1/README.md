
---

# 🧾 **LAB1 – API REST de Gestión de Clientes y Pedidos (Spring Boot + MySQL + Docker)**

## 📘 Descripción general

Este laboratorio desarrolla una **API RESTful completa** con **Spring Boot 3**, **Java 21** y **MySQL 8** en **Docker**, que gestiona dos entidades relacionadas:

* `Cliente` (puede tener varios pedidos)
* `Pedido` (pertenece a un único cliente)

El proyecto implementa:

* CRUD completo para ambas entidades.
* Validaciones de entrada.
* Manejo profesional de errores HTTP (`400`, `404`, `409`).
* Persistencia con Spring Data JPA.
* Recarga automática con Spring DevTools (modo desarrollo).
* Entorno Docker Compose reproducible.

---

## 🧱 Estructura del sistema

### 🔹 Entidades

#### `Cliente`

| Campo  | Tipo        | Restricciones     |
| ------ | ----------- | ----------------- |
| id     | Long        | PK, autoincrement |
| nombre | String(100) | NOT NULL          |
| email  | String(150) | NOT NULL, UNIQUE  |

#### `Pedido`

| Campo         | Tipo          | Restricciones      |
| ------------- | ------------- | ------------------ |
| id            | Long          | PK, autoincrement  |
| descripcion   | String(255)   | NOT NULL           |
| total         | Decimal(10,2) | NOT NULL           |
| fechaCreacion | Timestamp     | default NOW()      |
| cliente_id    | Long          | FK a `clientes.id` |

Relación:

> **Cliente (1) — (N) Pedido**

---

## ⚙️ Arquitectura general

```
src/
 ├─ main/java/com/example/demo/
 │   ├─ models/          → Entidades JPA
 │   ├─ repositories/    → Interfaces JPA
 │   ├─ services/        → Lógica de negocio y validaciones
 │   └─ controllers/     → Endpoints REST
 ├─ resources/
 │   ├─ application.properties
 │   └─ mysql/init.sql   → Script inicial
 └─ test/java/           → Pruebas unitarias e integración
```

---

## 🧩 Principales clases

### **ClienteService.java**

* Valida datos obligatorios (`nombre`, `email`).
* Controla duplicados (`email` único).
* Lanza excepciones HTTP controladas con `ResponseStatusException`.

### **PedidoService.java**

* Verifica que el cliente exista antes de crear un pedido.
* Valida campos `descripcion` y `total`.
* Devuelve errores 404 o 400 según el caso.

---

## 🧰 Controladores REST

### **ClientesRestController.java**

| Método   | Endpoint             | Descripción                    | Código HTTP         |
| -------- | -------------------- | ------------------------------ | ------------------- |
| `GET`    | `/api/clientes`      | Lista todos los clientes       | `200 OK`            |
| `GET`    | `/api/clientes/{id}` | Obtiene cliente por id         | `200`, `404`        |
| `POST`   | `/api/clientes`      | Crea un cliente nuevo          | `201`, `400`, `409` |
| `PUT`    | `/api/clientes/{id}` | Actualiza un cliente existente | `200`, `404`        |
| `DELETE` | `/api/clientes/{id}` | Elimina un cliente             | `204`, `404`        |

---

### **PedidosRestController.java**

| Método   | Endpoint                     | Descripción                              | Código HTTP         |
| -------- | ---------------------------- | ---------------------------------------- | ------------------- |
| `GET`    | `/api/pedidos`               | Lista todos los pedidos                  | `200`               |
| `GET`    | `/api/clientes/{id}/pedidos` | Lista pedidos de un cliente              | `200`, `404`        |
| `POST`   | `/api/clientes/{id}/pedidos` | Crea un pedido para un cliente existente | `201`, `400`, `404` |
| `DELETE` | `/api/pedidos/{id}`          | Elimina un pedido                        | `204`, `404`        |

---

## 💡 Manejo profesional de errores

Se usa `ResponseStatusException` para devolver errores semánticos REST:

| Escenario             | Código            | Mensaje                 |
| --------------------- | ----------------- | ----------------------- |
| Cliente inexistente   | `404 Not Found`   | “Cliente no encontrado” |
| Pedido inexistente    | `404 Not Found`   | “Pedido no encontrado”  |
| Campos nulos o vacíos | `400 Bad Request` | “Campos obligatorios”   |
| Email duplicado       | `409 Conflict`    | “Email ya registrado”   |

### Ejemplo

```json
{
  "timestamp": "2025-11-09T18:58:44.716+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente no encontrado",
  "path": "/api/clientes/999/pedidos"
}
```

---

## 🧠 Validación y DTOs

En esta versión, las entidades JPA se devuelven directamente (simplificado).
Para entornos reales, se recomienda usar **DTOs** (`ClienteDTO`, `PedidoDTO`) para desacoplar el modelo de dominio de la API pública, evitando:

* Ciclos de serialización (`cliente → pedidos → cliente`).
* Exposición de relaciones internas.
* Cambios en la estructura interna que rompan la API.

---

## 🐳 Docker Compose

`docker-compose.yml`:

```yaml
version: "3.9"
services:
  mysql:
    image: mysql:8.0
    container_name: mysql-pedidos
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: pedidosdb
      MYSQL_USER: spring
      MYSQL_PASSWORD: spring
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./mysql/init:/docker-entrypoint-initdb.d
    networks:
      - app-net

  app:
    build: .
    container_name: spring-pedidos
    restart: on-failure
    depends_on:
      - mysql
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/pedidosdb?useSSL=false&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: spring
      SPRING_DATASOURCE_PASSWORD: spring
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    networks:
      - app-net

networks:
  app-net:

volumes:
  mysql_data:
```

---

## 🚀 Ejecución

```bash
# 1. Compilar
mvn clean package

# 2. Levantar entorno
docker compose up -d

# 3. Ver logs
docker compose logs -f app

# 4. Probar API
curl http://localhost:8080/api/clientes
```

---

## 🧪 Pruebas en Postman

### Crear cliente

```
POST http://localhost:8080/api/clientes
Body:
{ "nombre": "Carlos", "email": "carlos@demo.es" }
→ 201 Created
```

### Crear pedido

```
POST http://localhost:8080/api/clientes/1/pedidos
Body:
{ "descripcion": "Pedido prueba", "total": 89.99 }
→ 201 Created
```

### Error cliente inexistente

```
POST http://localhost:8080/api/clientes/999/pedidos
→ 404 Not Found
```

### Error email duplicado

```
POST http://localhost:8080/api/clientes
Body:
{ "nombre": "Demo", "email": "demo@example.com" }
→ 409 Conflict
```

---

## 🧰 Troubleshooting

| Problema                                                        | Causa                                                               | Solución                                                    |
| --------------------------------------------------------------- | ------------------------------------------------------------------- | ----------------------------------------------------------- |
| `IllegalArgumentException: Name for argument ... not specified` | Falta `("id")` en `@PathVariable` o no se compiló con `-parameters` | Añadir `@PathVariable("id")` o configurar el compilador     |
| `SQLIntegrityConstraintViolationException`                      | Campos obligatorios nulos                                           | Validar `nombre`, `email`, `total`, `descripcion`           |
| `500 Internal Server Error`                                     | Excepciones no controladas                                          | Usar `ResponseStatusException`                              |
| `MySQL connection refused`                                      | Contenedor MySQL aún no inició                                      | Esperar unos segundos o revisar `docker compose logs mysql` |

---

## 📊 Resultado final

| Capa                   | Tecnología                | Descripción                    |
| ---------------------- | ------------------------- | ------------------------------ |
| **Framework**          | Spring Boot 3 + JPA       | Backend REST                   |
| **Persistencia**       | MySQL 8 en Docker         | Base de datos relacional       |
| **Cliente REST**       | Postman / curl            | Pruebas manuales               |
| **Gestión de errores** | `ResponseStatusException` | Mapeo HTTP semántico           |
| **Validaciones**       | Lógicas en servicios      | Campos requeridos y duplicados |
| **Compilación**        | `mvn clean package`       | Java 21 + Maven                |
| **Recarga**            | `spring-boot-devtools`    | Hot reload durante desarrollo  |

---

## 🧾 Conclusión

Este laboratorio demuestra un ciclo de desarrollo **profesional con Spring Boot y Docker**, aplicando buenas prácticas REST:

✅ Controladores limpios
✅ Servicios con validaciones
✅ Manejo semántico de errores HTTP
✅ Entorno reproducible y portable
✅ Listo para evolución con DTOs y pruebas automáticas

---
