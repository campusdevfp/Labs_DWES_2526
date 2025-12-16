#  Spring Security con Sesiones HTTP - Guía Completa

##  Índice

1. [Flujo de autenticación con sesión](#flujo-de-autenticación-con-sesión)
2. [Endpoints Disponibles](#-endpoints-disponibles)
3. [Gestión de Carrito y Sesiones](#-gestión-de-carrito-y-sesiones)
4. [Ejemplos Prácticos (curl, Postman, Angular)](#-ejemplos-prácticos-curl-postman-angular)
5. [Gestión de Sesiones](#-gestión-de-sesiones)
6. [Ejemplos Prácticos](#-ejemplos-prácticos)
7. [Configuración de Seguridad](#-configuración-de-seguridad)
8. [Consola H2 (Ver Base de Datos)](#-consola-h2-ver-base-de-datos)
9. [Conceptos Clave](#-conceptos-clave)
10. [Consideraciones](#-consideraciones)
11. [Integración con Angular](#-integración-con-angular)

---

### Flujo de autenticación con sesión

## 🚦 Endpoints Disponibles

| Método | Endpoint                        | Descripción detallada                                                                 | Autenticación |
|--------|----------------------------------|---------------------------------------------------------------------------------------|---------------|
| POST   | `/api/auth/login`               | Inicia sesión. Recibe usuario y contraseña, y crea la sesión si son correctos.        | No            |
| GET    | `/api/auth/me`                  | Devuelve información del usuario autenticado (nombre, roles, etc).                    | Sí            |
| POST   | `/ejemplos/carrito/agregar`     | Añade un producto al carrito de la sesión actual.                                     | Sí            |
| GET    | `/ejemplos/carrito/ver`         | Muestra el contenido actual del carrito de la sesión.                                 | Sí            |
| DELETE | `/ejemplos/carrito/vaciar`      | Vacía el carrito de la sesión actual.                                                 | Sí            |
| GET    | `/test/public`                  | Endpoint público de prueba, accesible sin autenticación.                              | No            |
| GET    | `/test/private`                 | Endpoint privado de prueba, solo accesible si estás autenticado.                      | Sí            |

---

### Explicación de cada endpoint

- **POST `/api/auth/login`**: Permite iniciar sesión enviando un JSON con `username` y `password`. Si las credenciales son correctas, se crea una sesión y se devuelve la cookie de sesión. Úsalo para autenticarte antes de acceder a recursos protegidos.
  - Ejemplo de uso:
    ```bash
    curl -c cookies.txt -X POST -H "Content-Type: application/json" -d '{"username":"user","password":"password"}' http://localhost:8080/api/auth/login
    ```

- **GET `/api/auth/me`**: Devuelve los datos del usuario actualmente autenticado (por ejemplo, nombre y roles). Útil para mostrar información del usuario en el frontend.
  - Ejemplo de uso:
    ```bash
    curl -b cookies.txt http://localhost:8080/api/auth/me
    ```

- **POST `/ejemplos/carrito/agregar`**: Añade un producto al carrito de la sesión. Requiere parámetros `producto` y `cantidad` en la URL. El carrito se almacena en la sesión del usuario.
  - Ejemplo de uso:
    ```bash
    curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Laptop&cantidad=1"
    ```

- **GET `/ejemplos/carrito/ver`**: Muestra el contenido actual del carrito asociado a la sesión. Útil para que el usuario vea qué productos ha añadido.
  - Ejemplo de uso:
    ```bash
    curl -b cookies.txt http://localhost:8080/ejemplos/carrito/ver
    ```

- **DELETE `/ejemplos/carrito/vaciar`**: Elimina todos los productos del carrito de la sesión actual. Úsalo para vaciar el carrito antes de una nueva compra.
  - Ejemplo de uso:
    ```bash
    curl -b cookies.txt -X DELETE http://localhost:8080/ejemplos/carrito/vaciar
    ```

- **GET `/test/public`**: Endpoint de prueba accesible para cualquier usuario, sin autenticación. Útil para comprobar que el servidor responde.
  - Ejemplo de uso:
    ```bash
    curl http://localhost:8080/test/public
    ```

- **GET `/test/private`**: Endpoint de prueba que solo responde si el usuario está autenticado. Útil para comprobar que la autenticación funciona.
  - Ejemplo de uso:
    ```bash
    curl -b cookies.txt http://localhost:8080/test/private
    ```

---

## Gestión de Carrito y Sesiones

- El carrito de compras se almacena en la sesión HTTP del usuario.
- Cada usuario autenticado tiene su propio carrito.
- Si la sesión expira o se cierra, el carrito se pierde.

### Configuración de expiración de la sesión
Puedes configurar el tiempo de expiración de la sesión en `application.properties`:

```properties
server.servlet.session.timeout=30m
```

---

##  Ejemplos Prácticos (curl, Postman, Angular)

### Usando curl (simulación de navegador con cookies)

```bash
# 1. Login y guarda la cookie de sesión
curl -c cookies.txt -X POST -H "Content-Type: application/json" -d '{"username":"user","password":"password"}' http://localhost:8080/api/auth/login

# 2. Añadir producto al carrito (usando la cookie de sesión)
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Laptop&cantidad=1"

# 3. Ver el carrito
curl -b cookies.txt http://localhost:8080/ejemplos/carrito/ver

# 4. Vaciar el carrito
curl -b cookies.txt -X DELETE http://localhost:8080/ejemplos/carrito/vaciar
```

> Puedes ver las cookies en el navegador en: DevTools → Application → Cookies

---

### Usando Postman

1. Haz una petición POST a `/api/auth/login` con el body:
   ```json
   {
     "username": "user",
     "password": "password"
   }
   ```
2. Postman guardará la cookie de sesión automáticamente.
3. Realiza las siguientes peticiones (no necesitas añadir la cookie manualmente):
   - POST `/ejemplos/carrito/agregar?producto=Mouse&cantidad=2`
   - GET `/ejemplos/carrito/ver`
   - DELETE `/ejemplos/carrito/vaciar`
4. Puedes ver las cookies en la pestaña "Cookies" de Postman (abajo a la derecha en la ventana de la petición).

---

### Usando Angular (ejemplo básico de integración)

```typescript
// auth.service.ts
login(username: string, password: string) {
  return this.http.post('/api/auth/login', { username, password }, { withCredentials: true });
}

// carrito.service.ts
agregarProducto(producto: string, cantidad: number) {
  return this.http.post(`/ejemplos/carrito/agregar?producto=${producto}&cantidad=${cantidad}`, {}, { withCredentials: true });
}
verCarrito() {
  return this.http.get('/ejemplos/carrito/ver', { withCredentials: true });
}
vaciarCarrito() {
  return this.http.delete('/ejemplos/carrito/vaciar', { withCredentials: true });
}
```

> **Nota:** Es importante usar `{ withCredentials: true }` en Angular para que se envíen las cookies de sesión.

---

##  Gestión de Sesiones

### 1. Probar autenticación básica

```bash
# Endpoint público (sin autenticación)
curl http://localhost:8080/test/public

# Endpoint protegido (con autenticación)
curl -u user:password http://localhost:8080/test/authenticated

# Ver información del usuario
curl -u user:password http://localhost:8080/test/me
```

### 2. Trabajar con la sesión

```bash
# Guardar cookie de sesión en archivo
curl -c cookies.txt -u user:password http://localhost:8080/test/authenticated

# Reutilizar la sesión (ya no necesitas credenciales)
curl -b cookies.txt http://localhost:8080/session/info

# Ver atributos de la sesión
curl -b cookies.txt http://localhost:8080/session/attributes

# Guardar un atributo personalizado
curl -b cookies.txt -X POST "http://localhost:8080/session/attribute?key=miDato&value=hola"

# Recuperar el atributo
curl -b cookies.txt http://localhost:8080/session/attribute/miDato
```

### 3. Cerrar sesión (Logout)

```bash
# Hacer logout
curl -b cookies.txt -X POST http://localhost:8080/session/logout

# Intentar usar la sesión después del logout (fallará con 401)
curl -b cookies.txt http://localhost:8080/session/info
```

### ¿Qué pasa cuando cierras sesión?

1. `session.invalidate()` - Destruye la sesión en el servidor
2. `SecurityContextHolder.clearContext()` - Limpia el contexto de seguridad
3. La cookie JSESSIONID ya no es válida
4. Siguientes peticiones recibirán error 401

---

## 🛒 Ejemplos Prácticos

### Carrito de Compras (sin autenticación)

```bash
# Crear sesión y agregar producto
curl -c cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Laptop&cantidad=1"

# Agregar más productos (misma sesión)
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Mouse&cantidad=2"

# Ver el carrito
curl -b cookies.txt http://localhost:8080/ejemplos/carrito/ver

# Vaciar carrito
curl -b cookies.txt -X DELETE http://localhost:8080/ejemplos/carrito/vaciar
```

### Preferencias de Usuario

```bash
# Cambiar idioma
curl -c cookies.txt -X POST "http://localhost:8080/ejemplos/preferencias/idioma?idioma=en"

# Cambiar tema
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/preferencias/tema?tema=oscuro"

# Ver preferencias
curl -b cookies.txt http://localhost:8080/ejemplos/preferencias
```

### Formulario Multi-Paso (Wizard)

```bash
# Paso 1: Datos personales
curl -c cookies.txt -X POST "http://localhost:8080/ejemplos/registro/paso1?nombre=Juan&email=juan@mail.com"

# Paso 2: Datos de contacto
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/registro/paso2?telefono=123456789&ciudad=Madrid"

# Ver estado
curl -b cookies.txt http://localhost:8080/ejemplos/registro/estado

# Paso 3: Confirmar
curl -b cookies.txt -X POST http://localhost:8080/ejemplos/registro/paso3
```

---

## 🔒 Configuración de Seguridad

### SecurityConfig.java explicado
```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF deshabilitado (para APIs REST)
            .csrf(csrf -> csrf.disable())
            
            // Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/test/public", "/h2-console/**").permitAll()
                .requestMatchers("/test/authenticated", "/test/me").authenticated()
                .requestMatchers("/session/**").authenticated()
                .requestMatchers("/ejemplos/**").permitAll()
                .anyRequest().authenticated()
            )
            
            // Permitir H2 Console (frames)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            
            // Habilitar HTTP Basic
            .httpBasic(httpBasic -> {})
            
            // Configurar logout
            .logout(logout -> logout
                .logoutUrl("/session/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            );
        return http.build();
    }
}
```

### Opciones de seguridad adicionales

#### Habilitar CSRF (para aplicaciones con formularios HTML)

```java
// Por defecto está habilitado, nosotros lo deshabilitamos para API REST
// Si usas formularios HTML tradicionales, déjalo habilitado:
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
)
```

#### Habilitar CORS (para permitir peticiones desde Angular/React)

```java
.cors(cors -> cors.configurationSource(request -> {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:4200")); // Angular
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true); // Importante para cookies
    return config;
}))
```

#### Headers de seguridad (XSS, etc.)

```java
.headers(headers -> headers
    .xssProtection(xss -> xss.enable())
    .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
    .frameOptions(frame -> frame.deny())
)
```

#### Control de sesiones concurrentes

```java
.sessionManagement(session -> session
    .maximumSessions(1)                    // Solo 1 sesión por usuario
    .maxSessionsPreventsLogin(true)        // Bloquea nuevo login si ya hay sesión
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
)
```

---

## 📊 Consola H2 (Ver Base de Datos)

Accede a: `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: `sa`
- **Password**: (vacío)

Podrás ver la tabla `users` con los usuarios de prueba.

---

## 🔑 Explicación de Conceptos Clave

### ¿Qué es una sesión en Spring Boot?

Una **sesión HTTP** es un mecanismo que permite al servidor recordar información sobre un usuario entre distintas peticiones HTTP. En aplicaciones web, esto es fundamental para mantener el estado del usuario, como su autenticación, carrito de compras o preferencias.

#### ¿Cómo funciona en Spring Boot?
- **HttpSession**: Es un objeto Java que se crea automáticamente por Spring y se asocia a cada usuario. Se almacena en el servidor (por defecto en memoria, pero puede ser en Redis o base de datos en producción).
- **Cookie JSESSIONID**: Cuando un usuario inicia sesión, Spring crea una sesión y envía una cookie llamada `JSESSIONID` al navegador. Esta cookie identifica la sesión en futuras peticiones.
- **Almacenamiento**: Puedes guardar cualquier objeto serializable en la sesión usando `session.setAttribute("clave", objeto)`. Por ejemplo, el carrito de compras se guarda como un atributo de la sesión.

#### Analogía simple
Imagina que vas a un supermercado:
- La **sesión** es tu carrito de compras: el supermercado (servidor) te da un carrito único cuando entras.
- La **cookie JSESSIONID** es el ticket que te dan: cada vez que vas a una sección, muestras el ticket para que sepan que eres tú.
- Si sales sin pagar (sesión expira), pierdes el carrito.

#### Ejemplo de código en Spring Boot
```java
@Controller
public class SessionController {

    @GetMapping("/session/info")
    public Map<String, Object> getSessionInfo(HttpSession session) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", session.getId());  // ID único de la sesión
        info.put("creationTime", new Date(session.getCreationTime()));
        info.put("lastAccessedTime", new Date(session.getLastAccessedTime()));
        return info;
    }

    @PostMapping("/session/attribute")
    public String setAttribute(@RequestParam String key, @RequestParam String value, HttpSession session) {
        session.setAttribute(key, value);  // Guardar dato en sesión
        return "Atributo guardado";
    }

    @GetMapping("/session/attribute/{key}")
    public Object getAttribute(@PathVariable String key, HttpSession session) {
        return session.getAttribute(key);  // Recuperar dato de sesión
    }
}
```

#### Consideraciones importantes
- **Timeout**: Por defecto, las sesiones expiran después de 30 minutos de inactividad. Puedes configurarlo en `application.properties`.
- **Seguridad**: Las sesiones son seguras porque el servidor valida la cookie. Si alguien roba la cookie, puede acceder a la sesión (por eso usa HTTPS en producción).
- **Escalabilidad**: En producción con muchos usuarios, usa Redis para almacenar sesiones en lugar de memoria.

---

## 📁 Explicación Detallada de los Archivos de Código

A continuación, explico cada archivo de código proporcionado, su propósito, métodos clave y cómo encaja en el proyecto de Spring Security con sesiones.

### 1. DbUserDetailsService.java (Servicio de carga de usuarios)
**Ubicación:** `src/main/java/app/ejemplo02/user/DbUserDetailsService.java`

**Propósito:** Implementa la interfaz `UserDetailsService` de Spring Security para cargar detalles de usuarios desde la base de datos durante la autenticación.

**Métodos clave:**
- `loadUserByUsername(String username)`: Busca al usuario en la BD usando `UserRepository`. Si no existe, lanza `UsernameNotFoundException`. Crea y devuelve un objeto `User` de Spring Security con username, password encriptada y roles.

**Cómo encaja:** Es usado automáticamente por Spring Security en el proceso de login. Sin este servicio, no se podrían autenticar usuarios desde la BD.

**Ejemplo de uso:** Cuando un usuario intenta loguearse, Spring llama a este método para obtener sus datos.

---

### 2. SecurityConfig.java (Configuración de seguridad)
**Ubicación:** `src/main/java/app/ejemplo02/config/SecurityConfig.java`

**Propósito:** Configura toda la seguridad de la aplicación usando Spring Security, definiendo reglas de autorización, autenticación y manejo de sesiones.

**Métodos clave:**
- `securityFilterChain(HttpSecurity http)`: Configura el filtro de seguridad.
  - Deshabilita CSRF para APIs REST.
  - Define rutas públicas (`/test/public`, `/h2-console/**`, `/api/auth/**`) y protegidas (`/session/**` requiere autenticación).
  - Habilita HTTP Basic y Form Login.
  - Configura logout en `/session/logout` con invalidación de sesión.
- `authenticationManager(AuthenticationConfiguration config)`: Proporciona el gestor de autenticación.
- `passwordEncoder()`: Usa un encoder delegante para passwords (soporta BCrypt, etc.).

**Cómo encaja:** Es el núcleo de la configuración de seguridad. Define qué endpoints proteger y cómo manejar la autenticación.

**Ejemplo de uso:** Sin esta configuración, Spring Security no sabría qué proteger o cómo autenticar.

---

### 3. AuthController.java (Controlador de autenticación REST)
**Ubicación:** `src/main/java/app/ejemplo02/controller/AuthController.java`

**Propósito:** Maneja endpoints de login/logout para APIs REST, permitiendo autenticación sin formularios HTML tradicionales.

**Métodos clave:**
- `login(LoginRequest loginRequest, HttpServletRequest request)`: Recibe JSON con username/password, autentica usando `AuthenticationManager`, guarda el `SecurityContext` en la sesión y devuelve info del usuario.
- `logout(HttpServletRequest request)`: Invalida la sesión y limpia el contexto de seguridad.

**Cómo encaja:** Complementa la autenticación básica de Spring Security con endpoints RESTful. Útil para frontends como Angular.

**Ejemplo de uso:** POST a `/api/auth/login` con `{"username":"user","password":"password"}` para iniciar sesión.

---

### 4. DebugController.java (Controlador de depuración)
**Ubicación:** `src/main/java/app/ejemplo02/controller/DebugController.java`

**Propósito:** Proporciona endpoints para depurar y gestionar usuarios en desarrollo/testing.

**Métodos clave:**
- `listUsers()`: Lista todos los usuarios con ID, username, password encriptada y role.
- `resetAdminPassword(String password)`: Cambia la contraseña del usuario "admin" y la guarda encriptada.

**Cómo encaja:** No es parte del flujo normal, pero ayuda a verificar usuarios y resetear credenciales en desarrollo.

**Ejemplo de uso:** GET `/debug/users` para ver todos los usuarios en la BD.

---

### 5. EjemplosSessionController.java (Ejemplos prácticos de sesiones)
**Ubicación:** `src/main/java/app/ejemplo02/controller/EjemplosSessionController.java`

**Propósito:** Demuestra usos reales de `HttpSession` con ejemplos como carrito, preferencias, historial, wizard, etc.

**Secciones clave:**
- **Carrito de compras:** Agrega/ver/vacía productos en sesión.
- **Preferencias:** Guarda idioma/tema.
- **Historial:** Lista páginas visitadas.
- **Wizard:** Formulario multi-paso guardando datos en sesión.
- **Flash messages, contador de visitas, datos temporales con TTL.**

**Cómo encaja:** Ilustra conceptos teóricos de sesiones con código funcional. Endpoints `/ejemplos/*` son públicos para probar sin login.

**Ejemplo de uso:** POST `/ejemplos/carrito/agregar?producto=Laptop&cantidad=1` para añadir al carrito.

---

### 6. SessionController.java (Gestión avanzada de sesiones)
**Ubicación:** `src/main/java/app/ejemplo02/controller/SessionController.java`

**Propósito:** Permite inspeccionar y manipular sesiones: ver info, atributos, configurar timeout, etc.

**Métodos clave:**
- `obtenerInfoSesion()`: Devuelve ID de sesión, usuario, roles, tiempos.
- `obtenerAtributosSesion()`: Lista atributos en la sesión.
- `guardarAtributo()` / `obtenerAtributo()`: Guarda/recupera datos personalizados.
- `cerrarSesion()`: Logout manual.
- `obtenerTimeout()` / `configurarTimeout()`: Gestiona tiempo de expiración.

**Cómo encaja:** Complementa ejemplos básicos, mostrando cómo Spring maneja sesiones internamente.

**Ejemplo de uso:** GET `/session/info` para ver detalles de la sesión actual.

---

### 7. TestController.java (Controlador de pruebas básicas)
**Ubicación:** `src/main/java/app/ejemplo02/controller/TestController.java`

**Propósito:** Endpoints simples para probar autenticación.

**Métodos clave:**
- `endpointPublico()`: Accesible sin login.
- `endpointAutenticado()`: Requiere autenticación, saluda al usuario.
- `obtenerMiInfo()`: Devuelve username y roles del usuario logueado.

**Cómo encaja:** Base para probar configuración de seguridad. Endpoints `/test/*` usados en ejemplos de curl/Postman.

**Ejemplo de uso:** GET `/test/me` para ver info del usuario autenticado.

---

### 8. UserEntity.java (Entidad JPA para usuarios)
**Ubicación:** `src/main/java/app/ejemplo02/models/UserEntity.java`

**Propósito:** Modelo de datos para la tabla `users` en la BD.

**Campos:** ID (auto-generado), username (único), password (encriptada), role (ej. "ROLE_USER").

**Cómo encaja:** Usado por `UserRepository` para persistir usuarios. Spring Data JPA crea la tabla automáticamente.

**Ejemplo de uso:** `UserEntity user = new UserEntity(); user.setUsername("user");` para crear un usuario.

---

### 9. UserService.java (Servicio de negocio para usuarios)
**Ubicación:** `src/main/java/app/ejemplo02/service/UserService.java`

**Propósito:** Lógica de negocio para gestionar usuarios: crear, buscar, listar.

**Métodos clave:**
- `crearUsuario()`: Crea usuario con password encriptada.
- `buscarPorUsername()`: Busca por username.
- `listarTodos()`: Lista todos los usuarios.

**Cómo encaja:** Abstrae lógica de BD, usado por controladores o servicios de autenticación.

**Ejemplo de uso:** `userService.crearUsuario("user", "password", "ROLE_USER");` para crear un usuario.

---

### 10. DbUserDetailsService.java (Duplicado)
Es idéntico al archivo 1. Explicación igual: carga usuarios desde BD para autenticación.

---

Estos archivos forman una aplicación completa de Spring Security con sesiones: configuración, autenticación, ejemplos prácticos y gestión de usuarios. Si necesitas más detalles sobre algún método o archivo específico, dime.
