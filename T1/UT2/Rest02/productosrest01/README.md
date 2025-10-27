Perfecto 💪 — aquí tienes el **set completo de peticiones `curl`** actualizado con la validación que evita productos repetidos (`409 CONFLICT`).

Cada comando está listo para ejecutar en tu terminal o Postman.
👉 Si ejecutas un `POST` dos veces con el mismo nombre, ahora verás que devuelve **409** la segunda vez.

---

## ⚙️ **Configuración previa**

Asegúrate de tener tu aplicación ejecutándose con:

```bash
mvn spring-boot:run
```

Y el backend escuchando en:

```
http://localhost:8080
```

---

## 🟢 **1️⃣ — GET: Obtener todos los productos**

```bash
curl -X GET http://localhost:8080/api/productos \
     -H "Accept: application/json"
```

📥 **Respuesta (200 OK):**

```json
[
  {"id":1,"nombre":"Portátil Lenovo","precio":749.99},
  {"id":2,"nombre":"Monitor LG 24”","precio":179.90},
  {"id":3,"nombre":"Ratón Logitech","precio":29.99}
]
```

---

## 🟢 **2️⃣ — GET: Obtener un producto por ID**

```bash
curl -X GET http://localhost:8080/api/productos/2 \
     -H "Accept: application/json"
```

📥 **Respuesta (200 OK):**

```json
{"id":2,"nombre":"Monitor LG 24”","precio":179.90}
```

📥 **Si no existe (404 Not Found):**

```bash
curl -X GET http://localhost:8080/api/productos/999
```

```
(no body)
```

---

## 🟩 **3️⃣ — POST: Crear un nuevo producto**

```bash
curl -X POST http://localhost:8080/api/productos \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Altavoz JBL Go 4","precio":49.99}'
```

📥 **Respuesta (201 Created):**

```json
{"id":4,"nombre":"Altavoz JBL Go 4","precio":49.99}
```

📥 **Si intentas crear el mismo producto otra vez (409 Conflict):**

```bash
curl -X POST http://localhost:8080/api/productos \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Altavoz JBL Go 4","precio":49.99}'
```

📤 **Respuesta:**

```json
"Ya existe un producto con ese nombre"
```

📥 **Si envías datos inválidos (400 Bad Request):**

```bash
curl -X POST http://localhost:8080/api/productos \
     -H "Content-Type: application/json" \
     -d '{"precio":49.99}'
```

📤 **Respuesta:**

```json
"El nombre del producto es obligatorio"
```

---

## 🟦 **4️⃣ — PUT: Actualizar producto completo**

```bash
curl -X PUT http://localhost:8080/api/productos/2 \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Monitor Samsung 27”","precio":229.90}'
```

📥 **Respuesta (200 OK):**

```json
{"id":2,"nombre":"Monitor Samsung 27”","precio":229.90}
```

📥 **Si el producto no existe (404):**

```bash
curl -X PUT http://localhost:8080/api/productos/999 \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Monitor","precio":100.00}'
```

---

## 🟧 **5️⃣ — PATCH: Actualizar parcialmente**

Solo cambia un campo (por ejemplo, el precio).

```bash
curl -X PATCH http://localhost:8080/api/productos/3 \
     -H "Content-Type: application/json" \
     -d '{"precio":34.50}'
```

📥 **Respuesta (200 OK):**

```json
{"id":3,"nombre":"Ratón Logitech","precio":34.50}
```

📥 **Si el producto no existe (404):**

```bash
curl -X PATCH http://localhost:8080/api/productos/999 \
     -H "Content-Type: application/json" \
     -d '{"precio":100.0}'
```

---

## 🟥 **6️⃣ — DELETE: Eliminar producto**

```bash
curl -X DELETE http://localhost:8080/api/productos/4
```

📥 **Respuesta (204 No Content):**
*(sin cuerpo)*

📥 **Si el producto no existe (404):**

```bash
curl -X DELETE http://localhost:8080/api/productos/999
```

---

## 💡 **Opcional: salida formateada (si tienes `jq`)**

```bash
curl -s http://localhost:8080/api/productos | jq
```

---

## 🧠 **Resumen rápido**

| Operación          | Método | Ruta                  | Códigos devueltos |
| ------------------ | ------ | --------------------- | ----------------- |
| Listar todos       | GET    | `/api/productos`      | 200               |
| Obtener uno        | GET    | `/api/productos/{id}` | 200 / 404         |
| Crear nuevo        | POST   | `/api/productos`      | 201 / 400 / 409   |
| Actualizar todo    | PUT    | `/api/productos/{id}` | 200 / 404         |
| Actualizar parcial | PATCH  | `/api/productos/{id}` | 200 / 404         |
| Eliminar           | DELETE | `/api/productos/{id}` | 204 / 404         |

---

¿Quieres que te deje el fragmento de código exacto del método `POST` con la validación `409 CONFLICT` para copiarlo directamente en tu controlador?
