# 🧪 Guía de Pruebas: Ejemplos Prácticos de Sesiones

## 🚀 Cómo probar los ejemplos

Todos los endpoints bajo `/ejemplos/**` son **públicos** (no requieren autenticación) para demostrar casos de uso como carritos anónimos.

---

## 1️⃣ CARRITO DE COMPRAS (E-commerce sin login)

### Agregar productos al carrito
```bash
curl -c cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Laptop&cantidad=1"
```
**Respuesta:**
```
✅ Producto agregado. Total items: 1
```

### Agregar más productos (reutilizando la misma sesión)
```bash
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Mouse&cantidad=2"
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Teclado&cantidad=1"
```

### Ver el carrito
```bash
curl -b cookies.txt http://localhost:8080/ejemplos/carrito/ver
```
**Respuesta:**
```json
{
  "items": [
    {"producto": "Laptop", "cantidad": 1, "agregadoEn": "2025-12-08T..."},
    {"producto": "Mouse", "cantidad": 2, "agregadoEn": "2025-12-08T..."},
    {"producto": "Teclado", "cantidad": 1, "agregadoEn": "2025-12-08T..."}
  ],
  "total": 3
}
```

### Vaciar el carrito
```bash
curl -b cookies.txt -X DELETE http://localhost:8080/ejemplos/carrito/vaciar
```

**💡 Nota:** La opción `-c cookies.txt` guarda las cookies (incluido JSESSIONID), y `-b cookies.txt` las envía en peticiones siguientes.

---

## 2️⃣ PREFERENCIAS DEL USUARIO

### Cambiar idioma
```bash
curl -c cookies.txt -X POST "http://localhost:8080/ejemplos/preferencias/idioma?idioma=en"
```
**Respuesta:**
```
🌍 Idioma cambiado a: en
```

### Cambiar tema (claro/oscuro)
```bash
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/preferencias/tema?tema=oscuro"
```
**Respuesta:**
```
🎨 Tema cambiado a: oscuro
```

### Ver preferencias actuales
```bash
curl -b cookies.txt http://localhost:8080/ejemplos/preferencias
```
**Respuesta:**
```json
{
  "idioma": "en",
  "tema": "oscuro"
}
```

---

## 3️⃣ HISTORIAL DE NAVEGACIÓN

### Registrar páginas visitadas
```bash
curl -c cookies.txt -X POST "http://localhost:8080/ejemplos/historial/agregar?pagina=/productos/123"
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/historial/agregar?pagina=/productos/456"
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/historial/agregar?pagina=/categorias/electronicos"
```

### Ver historial completo
```bash
curl -b cookies.txt http://localhost:8080/ejemplos/historial
```
**Respuesta:**
```json
{
  "historial": [
    "/productos/123",
    "/productos/456",
    "/categorias/electronicos"
  ],
  "cantidad": 3
}
```

**💡 Uso típico:** Mostrar "Productos vistos recientemente" o generar recomendaciones.

---

## 4️⃣ FORMULARIO MULTI-PASO (Wizard de Registro)

### Paso 1: Datos personales
```bash
curl -c cookies.txt -X POST "http://localhost:8080/ejemplos/registro/paso1?nombre=Juan&email=juan@example.com"
```
**Respuesta:**
```
✅ Paso 1/3 completado. Datos guardados en sesión.
```

### Paso 2: Datos de contacto
```bash
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/registro/paso2?telefono=123456789&ciudad=Madrid"
```
**Respuesta:**
```
✅ Paso 2/3 completado. Datos guardados en sesión.
```

### Verificar estado del registro
```bash
curl -b cookies.txt http://localhost:8080/ejemplos/registro/estado
```
**Respuesta:**
```json
{
  "paso1": "✅ Completado",
  "paso2": "✅ Completado",
  "paso3": "❌ Pendiente"
}
```

### Paso 3: Confirmar y finalizar
```bash
curl -b cookies.txt -X POST http://localhost:8080/ejemplos/registro/paso3
```
**Respuesta:**
```json
{
  "mensaje": "✅ Registro completado exitosamente",
  "datos": {
    "nombre": "Juan",
    "email": "juan@example.com",
    "telefono": "123456789",
    "ciudad": "Madrid"
  }
}
```

**💡 Uso típico:** Procesos de registro largos, solicitudes de crédito, reservas con múltiples pasos.

---

## 5️⃣ FLASH MESSAGES (Mensajes Temporales)

### Guardar un mensaje flash
```bash
curl -c cookies.txt -X POST "http://localhost:8080/ejemplos/flash/mensaje?mensaje=Producto guardado correctamente&tipo=success"
```
**Respuesta:**
```
💬 Mensaje flash guardado (se mostrará en la siguiente petición)
```

### Obtener el mensaje flash (se elimina después de leer)
```bash
curl -b cookies.txt http://localhost:8080/ejemplos/flash/obtener
```
**Respuesta (primera vez):**
```json
{
  "mensaje": "Producto guardado correctamente",
  "tipo": "success"
}
```

### Intentar obtenerlo de nuevo
```bash
curl -b cookies.txt http://localhost:8080/ejemplos/flash/obtener
```
**Respuesta (segunda vez):**
```json
{
  "mensaje": "No hay mensajes flash"
}
```

**💡 Uso típico:** Mensajes de confirmación después de guardar/eliminar/actualizar datos.

---

## 6️⃣ CONTADOR DE VISITAS

### Primera visita
```bash
curl -c cookies.txt http://localhost:8080/ejemplos/visitas
```
**Respuesta:**
```json
{
  "usuario": "Anónimo",
  "visitasEnEstaSesion": 1,
  "mensaje": "Has visitado este endpoint 1 vez/veces en esta sesión"
}
```

### Visitas siguientes (con la misma sesión)
```bash
curl -b cookies.txt http://localhost:8080/ejemplos/visitas
curl -b cookies.txt http://localhost:8080/ejemplos/visitas
```
**Respuesta:**
```json
{
  "usuario": "Anónimo",
  "visitasEnEstaSesion": 3,
  "mensaje": "Has visitado este endpoint 3 vez/veces en esta sesión"
}
```

**💡 Uso típico:** Estadísticas de navegación, detección de bots, límites de uso.

---

## 7️⃣ DATOS TEMPORALES CON EXPIRACIÓN (TTL)

### Guardar dato temporal que expira en 60 segundos
```bash
curl -c cookies.txt -X POST "http://localhost:8080/ejemplos/temporal/guardar?clave=codigoVerificacion&valor=ABC123&segundos=60"
```
**Respuesta:**
```
⏱️ Dato guardado. Expira en 60 segundos.
```

### Obtener el dato inmediatamente
```bash
curl -b cookies.txt http://localhost:8080/ejemplos/temporal/obtener/codigoVerificacion
```
**Respuesta:**
```json
{
  "valor": "ABC123",
  "expiraEn": "58 segundos"
}
```

### Esperar 60 segundos e intentar obtenerlo de nuevo
```bash
# Esperar 60+ segundos
curl -b cookies.txt http://localhost:8080/ejemplos/temporal/obtener/codigoVerificacion
```
**Respuesta:**
```json
{
  "error": "El dato ha expirado"
}
```

**💡 Uso típico:** Códigos OTP, tokens de verificación, datos de reset de contraseña.

---

## 🧩 ESCENARIO COMPLETO: E-commerce

Simula un usuario navegando una tienda online:

```bash
# 1. Usuario llega a la página (se crea sesión)
curl -c cookies.txt http://localhost:8080/ejemplos/visitas

# 2. Cambia idioma a inglés
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/preferencias/idioma?idioma=en"

# 3. Navega por productos
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/historial/agregar?pagina=/producto/laptop-123"
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/historial/agregar?pagina=/producto/mouse-456"

# 4. Agrega productos al carrito
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Laptop&cantidad=1"
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Mouse&cantidad=2"

# 5. Verifica el carrito
curl -b cookies.txt http://localhost:8080/ejemplos/carrito/ver

# 6. Ve su historial de navegación
curl -b cookies.txt http://localhost:8080/ejemplos/historial

# 7. Guarda un mensaje flash
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/flash/mensaje?mensaje=Productos agregados al carrito&tipo=success"

# 8. Obtiene el mensaje flash
curl -b cookies.txt http://localhost:8080/ejemplos/flash/obtener

# 9. Verifica cuántas veces ha visitado
curl -b cookies.txt http://localhost:8080/ejemplos/visitas
```

---

## 🔐 ESCENARIO CON AUTENTICACIÓN

Algunos endpoints requieren autenticación. Combina sesión + autenticación:

```bash
# 1. Autenticarse (crea sesión con Authentication)
curl -u user:password -c cookies.txt http://localhost:8080/session/info

# 2. Usar endpoints que requieren autenticación (sin volver a poner credenciales)
curl -b cookies.txt http://localhost:8080/test/authenticated
curl -b cookies.txt http://localhost:8080/test/me

# 3. Contador de visitas (ahora con usuario identificado)
curl -b cookies.txt http://localhost:8080/ejemplos/visitas

# 4. Cerrar sesión
curl -b cookies.txt -X POST http://localhost:8080/session/logout

# 5. Intentar acceder después del logout (falla)
curl -b cookies.txt http://localhost:8080/test/authenticated
```

---

## 🌐 Probar desde el Navegador

Abre la consola de desarrollador (F12) y ejecuta:

```javascript
// Agregar productos al carrito
fetch('/ejemplos/carrito/agregar?producto=Laptop&cantidad=1', { method: 'POST' })
  .then(r => r.text())
  .then(console.log);

// Ver carrito
fetch('/ejemplos/carrito/ver')
  .then(r => r.json())
  .then(console.log);

// Cambiar idioma
fetch('/ejemplos/preferencias/idioma?idioma=en', { method: 'POST' })
  .then(r => r.text())
  .then(console.log);

// Ver preferencias
fetch('/ejemplos/preferencias')
  .then(r => r.json())
  .then(console.log);
```

**💡 Ventaja:** El navegador maneja automáticamente las cookies (JSESSIONID), no necesitas `-c` ni `-b`.

---

## 📊 Verificar la Sesión en H2 Console

Si usas Spring Session con JDBC, puedes ver las sesiones en la BD:

1. Ve a `http://localhost:8080/h2-console`
2. Conecta con `jdbc:h2:mem:testdb`
3. Ejecuta:
```sql
SELECT * FROM SPRING_SESSION;
SELECT * FROM SPRING_SESSION_ATTRIBUTES;
```

---

## 🎯 Resumen de Casos de Uso Implementados

| Caso de Uso | Endpoints | Autenticación Requerida |
|-------------|-----------|-------------------------|
| Carrito de compras | `/ejemplos/carrito/*` | ❌ No (carrito anónimo) |
| Preferencias | `/ejemplos/preferencias/*` | ❌ No |
| Historial navegación | `/ejemplos/historial/*` | ❌ No |
| Wizard multi-paso | `/ejemplos/registro/*` | ❌ No |
| Flash messages | `/ejemplos/flash/*` | ❌ No |
| Contador visitas | `/ejemplos/visitas` | ❌ No (pero muestra username si estás autenticado) |
| Datos temporales | `/ejemplos/temporal/*` | ❌ No |
| Info de sesión | `/session/info` | ✅ Sí |
| Gestión de sesión | `/session/*` | ✅ Sí |

---

¡Ahora tienes ejemplos prácticos y reales de cómo usar sesiones! 🎉

