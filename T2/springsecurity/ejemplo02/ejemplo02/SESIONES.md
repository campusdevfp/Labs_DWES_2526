```bash
curl -b "JSESSIONID=ABC123XYZ" http://localhost:8080/test/authenticated
```

**Resultado:** Error 401 (la sesión ya no existe).

---

## 🎯 Resumen de conceptos clave

| Concepto | Explicación |
|----------|-------------|
| **JSESSIONID** | Cookie que identifica la sesión en el servidor |
| **HttpSession** | Objeto Java que almacena atributos de la sesión |
| **SecurityContext** | Contiene el `Authentication` (usuario autenticado) |
| **SessionCreationPolicy** | Estrategia de creación de sesiones (IF_REQUIRED, ALWAYS, NEVER, STATELESS) |
| **Session Timeout** | Tiempo de inactividad antes de que expire la sesión |
| **Session Fixation** | Ataque que Spring Security previene automáticamente |
| **Concurrent Sessions** | Control de cuántas sesiones puede tener un usuario |
| **session.invalidate()** | Método para cerrar/destruir una sesión |

---

## ⚠️ Consideraciones importantes

1. **Las sesiones ocupan memoria**: En producción con muchos usuarios, considera Redis o JDBC.
2. **Las sesiones NO funcionan en modo STATELESS**: Si usas JWT, no tendrás sesiones HTTP.
3. **HTTPS en producción**: Siempre usa `cookie.secure=true` con HTTPS.
4. **Timeout apropiado**: 30 minutos es razonable, ajusta según tu caso de uso.
5. **Logout explícito**: Siempre ofrece un botón de logout para cerrar la sesión.
6. **Sesiones en clúster**: Usa Spring Session con Redis si tienes múltiples servidores.

---

¡Ahora tienes control total sobre las sesiones en tu aplicación Spring Security! 🎉
# Guía Completa: Gestión de Sesiones en Spring Security

## 📚 ¿Cómo funciona la sesión en Spring Security?

Cuando te autenticas con Spring Security usando **HTTP Basic** o **Form Login**, el framework automáticamente:

1. **Crea una sesión HTTP** en el servidor (si no existe)
2. **Genera un JSESSIONID** (cookie que se envía al navegador)
3. **Guarda el `Authentication`** en el `SecurityContext`
4. **Almacena el `SecurityContext`** en la sesión HTTP bajo la clave `SPRING_SECURITY_CONTEXT`

### Flujo de autenticación con sesión:

```
Cliente                    Spring Security              Servidor
  |                              |                          |
  |---(1) Login con credenciales--->                        |
  |                              |                          |
  |                    (2) Valida usuario en BD             |
  |                              |                          |
  |                    (3) Crea Authentication              |
  |                              |                          |
  |                    (4) Guarda en SecurityContext        |
  |                              |                          |
  |                    (5) Almacena SecurityContext ------->|
  |                              |      en HttpSession      |
  |                              |                          |
  |<--(6) Responde con Set-Cookie: JSESSIONID=xyz----------|
  |                              |                          |
  |---(7) Siguientes peticiones con Cookie: JSESSIONID---->|
  |                              |                          |
  |                    (8) Recupera SecurityContext <-------|
  |                              |      desde HttpSession   |
  |                              |                          |
  |<--(9) Usuario ya autenticado (sin volver a pedir creds)|
```

---

## 🔑 Endpoints disponibles para gestionar sesiones

### 1. **Ver información de la sesión actual**

```bash
curl -u user:password http://localhost:8080/session/info
```

**Respuesta:**
```json
{
  "sessionId": "A1B2C3D4E5F6G7H8",
  "username": "user",
  "roles": ["ROLE_USER"],
  "creationTime": "2025-12-08T10:30:00.000+00:00",
  "lastAccessedTime": "2025-12-08T10:35:00.000+00:00",
  "isAuthenticated": true
}
```

**¿Qué obtienes?**
- **sessionId**: Identificador único de la sesión (JSESSIONID)
- **username**: Usuario autenticado
- **roles**: Roles/authorities del usuario
- **creationTime**: Cuándo se creó la sesión
- **lastAccessedTime**: Última vez que se accedió a la sesión
- **isAuthenticated**: Si el usuario está autenticado

---

### 2. **Ver todos los atributos de la sesión**

```bash
curl -u user:password http://localhost:8080/session/attributes
```

**Respuesta:**
```json
{
  "SPRING_SECURITY_CONTEXT": {
    "tipo": "SecurityContext",
    "username": "user",
    "authenticated": true,
    "authorities": "[ROLE_USER]"
  },
  "miAtributoCustom": "valor guardado"
}
```

**Explicación:**
- **SPRING_SECURITY_CONTEXT**: El contexto de seguridad con la información de autenticación
- Cualquier otro atributo que hayas guardado manualmente

---

### 3. **Guardar un atributo personalizado en la sesión**

```bash
curl -u user:password -X POST "http://localhost:8080/session/attribute?key=carritoCompra&value=producto1,producto2"
```

**Respuesta:**
```
Atributo 'carritoCompra' guardado con valor: producto1,producto2
```

**Uso típico:**
- Carrito de compras
- Preferencias temporales del usuario
- Datos del flujo de trabajo (wizards)

---

### 4. **Obtener un atributo específico de la sesión**

```bash
curl -u user:password http://localhost:8080/session/attribute/carritoCompra
```

**Respuesta:**
```
producto1,producto2
```

---

### 5. **Ver el tiempo de vida (timeout) de la sesión**

```bash
curl -u user:password http://localhost:8080/session/timeout
```

**Respuesta:**
```json
{
  "maxInactiveIntervalSegundos": 1800,
  "maxInactiveIntervalMinutos": 30
}
```

Por defecto, Spring Boot configura las sesiones con **30 minutos de inactividad**.

---

### 6. **Configurar el timeout de la sesión**

```bash
curl -u user:password -X POST "http://localhost:8080/session/timeout?segundos=3600"
```

**Respuesta:**
```
Timeout configurado a 3600 segundos (60 minutos)
```

---

### 7. **Cerrar la sesión (Logout)**

```bash
curl -u user:password -X POST http://localhost:8080/session/logout
```

**Respuesta:**
```
Sesión cerrada exitosamente
```

**¿Qué hace el logout?**
1. **Invalida la sesión HTTP** → `session.invalidate()`
2. **Elimina todos los atributos** de la sesión
3. **Limpia el SecurityContext** → `SecurityContextHolder.clearContext()`
4. **Elimina la cookie JSESSIONID** del navegador

---

## 💻 Cómo usar sesiones desde el código

### Opción 1: Inyectar `HttpSession` directamente

```java
@GetMapping("/ejemplo")
public String ejemplo(HttpSession session) {
    // Guardar atributo
    session.setAttribute("clave", "valor");
    
    // Obtener atributo
    String valor = (String) session.getAttribute("clave");
    
    // ID de la sesión
    String sessionId = session.getId();
    
    // Invalidar sesión
    session.invalidate();
    
    return "OK";
}
```

### Opción 2: Obtener sesión desde `HttpServletRequest`

```java
@GetMapping("/ejemplo2")
public String ejemplo2(HttpServletRequest request) {
    // Obtener sesión existente (o null si no existe)
    HttpSession session = request.getSession(false);
    
    // Crear sesión si no existe
    HttpSession sessionNueva = request.getSession(true);
    
    return "OK";
}
```

### Opción 3: Acceder al `SecurityContext` directamente

```java
@GetMapping("/ejemplo3")
public String ejemplo3() {
    // Obtener el contexto de seguridad del thread actual
    SecurityContext context = SecurityContextHolder.getContext();
    
    // Obtener la autenticación
    Authentication auth = context.getAuthentication();
    
    if (auth != null && auth.isAuthenticated()) {
        String username = auth.getName();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
    }
    
    return "OK";
}
```

### Opción 4: Inyectar `Authentication` automáticamente

```java
@GetMapping("/ejemplo4")
public String ejemplo4(Authentication authentication) {
    // Spring Security inyecta automáticamente el usuario autenticado
    String username = authentication.getName();
    boolean isAuthenticated = authentication.isAuthenticated();
    
    return "Hola " + username;
}
```

---

## ⚙️ Configuración de sesiones en `application.properties`

```properties
# Timeout de sesión (en segundos) - por defecto 1800 (30 minutos)
server.servlet.session.timeout=3600

# Nombre de la cookie de sesión (por defecto JSESSIONID)
server.servlet.session.cookie.name=MYSESSIONID

# Hacer la cookie HttpOnly (más seguro, no accesible desde JavaScript)
server.servlet.session.cookie.http-only=true

# Hacer la cookie Secure (solo HTTPS) - para producción
server.servlet.session.cookie.secure=true

# SameSite para protección CSRF
server.servlet.session.cookie.same-site=strict

# Path de la cookie
server.servlet.session.cookie.path=/
```

---

## 🔒 Estrategias de gestión de sesiones en Spring Security

En `SecurityConfig.java` puedes configurar diferentes estrategias:

### 1. **IF_REQUIRED** (por defecto)
Crea sesión solo si es necesario.

```java
.sessionManagement(sm -> sm
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
)
```

### 2. **ALWAYS**
Siempre crea una sesión, incluso para endpoints públicos.

```java
.sessionManagement(sm -> sm
    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
)
```

### 3. **NEVER**
Spring Security nunca crea una sesión, pero usará una si ya existe.

```java
.sessionManagement(sm -> sm
    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
)
```

### 4. **STATELESS** (para APIs REST con JWT)
No usa sesiones. Cada petición debe autenticarse independientemente.

```java
.sessionManagement(sm -> sm
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

**⚠️ Importante:** Si usas `STATELESS`, los endpoints de sesión no funcionarán porque no hay sesiones.

---

## 🛡️ Protección contra Session Fixation

Spring Security protege automáticamente contra **Session Fixation** (ataque donde se fija el ID de sesión). Opciones:

### 1. **migrateSession** (por defecto)
Crea una nueva sesión después del login, pero mantiene los atributos.

```java
.sessionManagement(sm -> sm
    .sessionFixation().migrateSession()
)
```

### 2. **newSession**
Crea una sesión completamente nueva, sin copiar atributos.

```java
.sessionManagement(sm -> sm
    .sessionFixation().newSession()
)
```

### 3. **changeSessionId** (recomendado para Servlet 3.1+)
Cambia solo el ID de sesión, más eficiente.

```java
.sessionManagement(sm -> sm
    .sessionFixation().changeSessionId()
)
```

---

## 👥 Control de sesiones concurrentes

Limitar cuántas sesiones puede tener un usuario al mismo tiempo:

```java
.sessionManagement(sm -> sm
    .maximumSessions(1) // Solo 1 sesión simultánea
    .maxSessionsPreventsLogin(true) // Bloquea nuevo login si ya hay sesión activa
    .expiredUrl("/session/expired") // Redirige a esta URL si la sesión expira
)
```

**Ejemplo de uso:**
- Usuario hace login desde PC → OK
- Usuario hace login desde móvil → Error (ya hay una sesión activa)

---

## 📊 Almacenamiento de sesiones

### Opción 1: En memoria (por defecto)
Las sesiones se guardan en la RAM del servidor. Se pierden al reiniciar.

### Opción 2: Spring Session con Redis
Para aplicaciones distribuidas (múltiples servidores).

**Dependencia:**
```gradle
implementation 'org.springframework.session:spring-session-data-redis'
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

**Configuración:**
```properties
spring.session.store-type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

### Opción 3: Spring Session con JDBC
Guarda sesiones en base de datos.

```gradle
implementation 'org.springframework.session:spring-session-jdbc'
```

```properties
spring.session.store-type=jdbc
spring.session.jdbc.initialize-schema=always
```

---

## 🧪 Probar sesiones paso a paso

### Escenario completo:

#### 1. Autenticarse y obtener el JSESSIONID

```bash
curl -v -u user:password http://localhost:8080/session/info
```

Busca en la respuesta:
```
< Set-Cookie: JSESSIONID=ABC123XYZ; Path=/; HttpOnly
```

#### 2. Usar el JSESSIONID en siguientes peticiones (sin credenciales)

```bash
curl -b "JSESSIONID=ABC123XYZ" http://localhost:8080/test/authenticated
```

**Resultado:** Funciona sin pedir usuario/contraseña porque el servidor reconoce la sesión.

#### 3. Guardar datos en la sesión

```bash
curl -b "JSESSIONID=ABC123XYZ" -X POST "http://localhost:8080/session/attribute?key=carrito&value=laptop,mouse"
```

#### 4. Recuperar datos de la sesión

```bash
curl -b "JSESSIONID=ABC123XYZ" http://localhost:8080/session/attribute/carrito
```

**Respuesta:**
```
laptop,mouse
```

#### 5. Cerrar sesión

```bash
curl -b "JSESSIONID=ABC123XYZ" -X POST http://localhost:8080/session/logout
```

#### 6. Intentar acceder después del logout


