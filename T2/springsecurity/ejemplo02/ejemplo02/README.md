# Spring Security con Sesiones HTTP - Guía Completa

Esta aplicación es un ejemplo práctico de Spring Boot con Spring Security, enfocada en la gestión de sesiones HTTP. Incluye autenticación, autorización, manejo de sesiones, carrito de compras y otros ejemplos reales.

## Índice

1. [Descripción General](#descripción-general)
2. [Requisitos](#requisitos)
3. [Instalación y Ejecución](#instalación-y-ejecución)
4. [Estructura del Proyecto](#estructura-del-proyecto)
5. [Endpoints Disponibles](#endpoints-disponibles)
6. [Configuración de Seguridad](#configuración-de-seguridad)
7. [Base de Datos y Usuarios](#base-de-datos-y-usuarios)
8. [Ejemplos de Uso](#ejemplos-de-uso)
9. [Conceptos Clave](#conceptos-clave)
10. [Solución de Problemas](#solución-de-problemas)

---

## Descripción General

Esta aplicación demuestra cómo implementar autenticación y autorización en Spring Boot usando Spring Security, con énfasis en el manejo de sesiones HTTP. Incluye:

- **Autenticación REST**: Login/logout sin formularios HTML.
- **Gestión de Sesiones**: Almacenamiento de datos por usuario (carrito, preferencias).
- **Autorización**: Control de acceso basado en roles.
- **Base de Datos H2**: Persistencia en memoria para desarrollo.
- **Ejemplos Prácticos**: Carrito de compras, preferencias, historial, etc.

La aplicación se ejecuta en `http://localhost:8080` y usa H2 como BD en memoria.

---

## Requisitos

- **Java**: JDK 21 (o superior).
- **Gradle**: Incluido en el proyecto (wrapper).
- **IDE**: IntelliJ IDEA, Eclipse o VS Code (opcional).

---

## Instalación y Ejecución


### Paso 1: Construir el Proyecto

Ejecuta Gradle para descargar dependencias y compilar:

```bash
./gradlew build
```

### Paso 2: Ejecutar la Aplicación

Inicia el servidor:

```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`.

### Paso 3: Verificar que Funciona

Abre en el navegador: `http://localhost:8080/test/public` (debería mostrar un mensaje público).

Para acceder a la consola H2: `http://localhost:8080/h2-console` (usuario: `sa`, password: vacío).

---

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/app/ejemplo02/
│   │   ├── Ejemplo02Application.java          # Clase principal de Spring Boot
│   │   ├── config/
│   │   │   └── SecurityConfig.java            # Configuración de Spring Security
│   │   ├── controller/                        # Controladores REST
│   │   │   ├── AuthController.java            # Autenticación (login/logout)
│   │   │   ├── DebugController.java           # Endpoints de depuración
│   │   │   ├── SessionController.java         # Gestión de sesiones
│   │   │   
│   │   │   
│   │   ├── dto/                               # Objetos de Transferencia de Datos
│   │   │   ├── ItemCarrito.java               # DTO para items del carrito
│   │   │   ├── SessionInfo.java               # DTO para info de sesión
│   │   │   └── UsuarioInfo.java               # DTO para info de usuario
│   │   ├── models/                            # Entidades JPA
│   │   │   └── UserEntity.java                # Modelo de usuario
│   │   ├── repository/                        # Repositorios de datos
│   │   │   └── UserRepository.java            # Acceso a usuarios en BD
│   │   ├── service/                           # Servicios de negocio
│   │   │   └── UserService.java               # Lógica para usuarios
│   │   ├── user/                              # Servicios de usuario para Security
│   │   │   └── DbUserDetailsService.java      # Carga de usuarios para autenticación
│   │   └── util/                              # Utilidades
│   │       └── SecurityUtils.java             # Helpers para seguridad
│   └── resources/
│       ├── application.properties             # Configuración de la app
│       └── data.sql                           # Datos iniciales (usuarios)
└── test/
    └── java/app/ejemplo02/
        └── Ejemplo02ApplicationTests.java     # Tests básicos
```

### Explicación de Paquetes

- **config**: Configuraciones (seguridad, BD).
- **controller**: Manejan peticiones HTTP, definen endpoints.
- **dto**: Estructuras de datos para respuestas JSON.
- **models**: Entidades de BD (mapeadas con JPA).
- **repository**: Interfaces para acceder a BD (usando Spring Data JPA).
- **service**: Lógica de negocio (encriptación, validaciones).
- **user**: Integración con Spring Security (carga de usuarios).
- **util**: Funciones auxiliares.

---

## Endpoints Disponibles

La aplicación expone varios endpoints agrupados por funcionalidad. Todos requieren autenticación excepto los marcados como "No".

### 1. Endpoints de Autenticación (`/api/auth/*`)

| Método | Endpoint              | Descripción | Autenticación |
|--------|-----------------------|-------------|---------------|
| GET    | `/api/auth/public`    | Mensaje público | No |
| POST   | `/api/auth/login`     | Inicia sesión | No |
| GET    | `/api/auth/me`        | Info del usuario | Sí |
| POST   | `/api/auth/logout`    | Cierra sesión | Sí |

#### Explicaciones Detalladas:

- **GET `/api/auth/public`**:
  - **Descripción**: Endpoint público accesible sin autenticación, usado para probar que el servidor responde.
  - **Lógica**: Retorna un mensaje simple. No requiere sesión ni credenciales.
  - **Respuesta**: `{"message": "Este es un endpoint público - accesible sin autenticación"}`
  - **Ejemplo**:
    ```bash
    curl http://localhost:8080/api/auth/public
    ```

- **POST `/api/auth/login`**:
  - **Descripción**: Inicia sesión enviando username y password en JSON.
  - **Lógica**: Usa `AuthenticationManager` para validar credenciales. Si es exitoso, guarda la autenticación en la sesión y retorna info del usuario. Si falla, retorna 401.
  - **Parámetros**: Body JSON con `username` y `password`.
  - **Respuesta**: JSON con mensaje, usuario, roles y sessionId.
  - **Ejemplo**:
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{"username":"user","password":"password"}' http://localhost:8080/api/auth/login
    ```

- **GET `/api/auth/me`**:
  - **Descripción**: Devuelve información del usuario actualmente autenticado.
  - **Lógica**: Extrae datos de `Authentication` (username, roles) y los retorna en un DTO `UsuarioInfo`.
  - **Respuesta**: JSON con username y lista de roles.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt http://localhost:8080/api/auth/me
    ```

- **POST `/api/auth/logout`**:
  - **Descripción**: Cierra la sesión del usuario.
  - **Lógica**: Llama a `SecurityUtils.clearAuthentication()` para invalidar sesión y limpiar contexto de seguridad.
  - **Respuesta**: Mensaje de confirmación.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt -X POST http://localhost:8080/api/auth/logout
    ```

### 2. Endpoints de Prueba (`/test/*`)

| Método | Endpoint          | Descripción | Autenticación |
|--------|-------------------|-------------|---------------|
| GET    | `/test/public`    | Endpoint público | No |
| GET    | `/test/private`   | Endpoint privado | Sí |

#### Explicaciones Detalladas:

- **GET `/test/public`**:
  - **Descripción**: Endpoint de prueba público.
  - **Lógica**: Retorna un mensaje simple sin requerir autenticación.
  - **Respuesta**: JSON con mensaje público.
  - **Ejemplo**:
    ```bash
    curl http://localhost:8080/test/public
    ```

- **GET `/test/private`**:
  - **Descripción**: Endpoint de prueba que requiere autenticación.
  - **Lógica**: Verifica si el usuario está autenticado y retorna un saludo personalizado.
  - **Respuesta**: Mensaje con nombre del usuario.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt http://localhost:8080/test/private
    ```

### 3. Endpoints de Sesión (`/session/*`)

| Método | Endpoint                  | Descripción | Autenticación |
|--------|---------------------------|-------------|---------------|
| GET    | `/session/info`           | Info de sesión | Sí |
| GET    | `/session/attributes`     | Atributos de sesión | Sí |
| POST   | `/session/attribute`      | Guarda atributo | Sí |
| GET    | `/session/attribute/{key}`| Obtiene atributo | Sí |
| POST   | `/session/timeout`        | Configura timeout | Sí |
| POST   | `/session/carrito/agregar`| Agrega al carrito | Sí |
| GET    | `/session/carrito/ver`    | Ver carrito | Sí |
| DELETE | `/session/carrito/vaciar` | Vacía carrito | Sí |

#### Explicaciones Detalladas:

- **GET `/session/info`**:
  - **Descripción**: Devuelve información detallada de la sesión actual.
  - **Lógica**: Extrae datos de `HttpSession` (ID, tiempos, usuario) y los retorna en `SessionInfo`.
  - **Respuesta**: JSON con sessionId, username, roles, tiempos de creación/acceso.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt http://localhost:8080/session/info
    ```

- **GET `/session/attributes`**:
  - **Descripción**: Lista todos los atributos almacenados en la sesión.
  - **Lógica**: Itera sobre `session.getAttributeNames()` y retorna un mapa con valores.
  - **Respuesta**: JSON con atributos (incluyendo SecurityContext).
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt http://localhost:8080/session/attributes
    ```

- **POST `/session/attribute`**:
  - **Descripción**: Guarda un atributo personalizado en la sesión.
  - **Lógica**: Usa `session.setAttribute(key, value)`.
  - **Parámetros**: `key` y `value` como query params.
  - **Respuesta**: Mensaje de confirmación.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt -X POST "http://localhost:8080/session/attribute?key=miDato&value=hola"
    ```

- **GET `/session/attribute/{key}`**:
  - **Descripción**: Obtiene un atributo específico de la sesión.
  - **Lógica**: Retorna `session.getAttribute(key)` o 404 si no existe.
  - **Respuesta**: Valor del atributo o error.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt http://localhost:8080/session/attribute/miDato
    ```

- **POST `/session/timeout`**:
  - **Descripción**: Configura el timeout de la sesión en segundos.
  - **Lógica**: Llama a `session.setMaxInactiveInterval(segundos)`.
  - **Parámetros**: `segundos` como query param.
  - **Respuesta**: Mensaje de confirmación.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt -X POST "http://localhost:8080/session/timeout?segundos=600"
    ```

- **POST `/session/carrito/agregar`**:
  - **Descripción**: Agrega un producto al carrito de la sesión.
  - **Lógica**: Crea `ItemCarrito` y lo añade a la lista en sesión.
  - **Parámetros**: `producto` y `cantidad`.
  - **Respuesta**: Mensaje de éxito.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt -X POST "http://localhost:8080/session/carrito/agregar?producto=Laptop&cantidad=1"
    ```

- **GET `/session/carrito/ver`**:
  - **Descripción**: Muestra el contenido del carrito.
  - **Lógica**: Retorna la lista de items en sesión.
  - **Respuesta**: JSON con items y total.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt http://localhost:8080/session/carrito/ver
    ```

- **DELETE `/session/carrito/vaciar`**:
  - **Descripción**: Vacía el carrito.
  - **Lógica**: Remueve el atributo "carrito" de la sesión.
  - **Respuesta**: Mensaje de confirmación.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt -X DELETE http://localhost:8080/session/carrito/vaciar
    ```

### 4. Endpoints de Ejemplos (`/ejemplos/*`)

| Método | Endpoint                      | Descripción | Autenticación |
|--------|-------------------------------|-------------|---------------|
| POST   | `/ejemplos/carrito/agregar`   | Agrega producto | Sí |
| GET    | `/ejemplos/carrito/ver`       | Ver carrito | Sí |
| DELETE | `/ejemplos/carrito/vaciar`    | Vacía carrito | Sí |

#### Explicaciones Detalladas:

- **POST `/ejemplos/carrito/agregar`**:
  - **Descripción**: Agrega un producto al carrito (similar a `/session/carrito/agregar`).
  - **Lógica**: Maneja lista en sesión, añade item.
  - **Parámetros**: `producto`, `cantidad`.
  - **Respuesta**: Mensaje de éxito.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Mouse&cantidad=2"
    ```

- **GET `/ejemplos/carrito/ver`**:
  - **Descripción**: Ver el carrito.
  - **Lógica**: Retorna lista de items.
  - **Respuesta**: JSON con items.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt http://localhost:8080/ejemplos/carrito/ver
    ```

- **DELETE `/ejemplos/carrito/vaciar`**:
  - **Descripción**: Vacía el carrito.
  - **Lógica**: Remueve atributo de sesión.
  - **Respuesta**: Mensaje.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt -X DELETE http://localhost:8080/ejemplos/carrito/vaciar
    ```

### 5. Endpoints de Depuración (`/debug/*`)

| Método | Endpoint              | Descripción | Autenticación |
|--------|-----------------------|-------------|---------------|
| GET    | `/debug/users`        | Lista usuarios | Sí |
| POST   | `/debug/admin/reset`  | Resetea password admin | Sí |

#### Explicaciones Detalladas:

- **GET `/debug/users`**:
  - **Descripción**: Lista todos los usuarios de la BD.
  - **Lógica**: Consulta `UserRepository.findAll()` y retorna lista con id, username, password, role.
  - **Respuesta**: JSON array de usuarios.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt http://localhost:8080/debug/users
    ```

- **POST `/debug/admin/reset`**:
  - **Descripción**: Cambia la contraseña del usuario admin.
  - **Lógica**: Busca usuario "admin", encripta nueva password y guarda.
  - **Parámetros**: `password` como query param.
  - **Respuesta**: Mensaje y password encriptada.
  - **Ejemplo**:
    ```bash
    curl -b cookies.txt -X POST "http://localhost:8080/debug/admin/reset?password=nueva123"
    ```


---

## Configuración de Seguridad

La seguridad se configura en `SecurityConfig.java`:

- **CSRF Deshabilitado**: Para APIs REST.
- **Reglas de Autorización**:
  - `/test/public`, `/h2-console/**`: Permitidos sin login.
  - `/api/auth/**`: Permitidos para login/logout.
  - `/ejemplos/**`: Requieren autenticación (para carrito).
  - Resto: Autenticación obligatoria.
- **Autenticación**: Soporta HTTP Basic y formularios.
- **Logout**: Invalida sesión y limpia contexto.

Para cambiar reglas, edita `authorizeHttpRequests` en `SecurityConfig.java`.

### Explicación Detallada de SecurityConfig.java

**Ubicación:** `src/main/java/app/ejemplo02/config/SecurityConfig.java`

**Propósito:** Esta clase configura toda la seguridad de la aplicación Spring Boot usando Spring Security. Define reglas de autorización, autenticación, manejo de sesiones y logout. Es el núcleo de la configuración de seguridad.

**Métodos y Lógica Principales:**

1. **`securityFilterChain(HttpSecurity http)`**:
   - **Propósito**: Configura el filtro de seguridad principal que procesa cada petición HTTP.
   - **Lógica Detallada**:
     - `.csrf(csrf -> csrf.disable())`: Deshabilita la protección CSRF (Cross-Site Request Forgery). Esto es común en APIs REST donde no se usan formularios HTML tradicionales, ya que CSRF requiere tokens en formularios.
     - `.authorizeHttpRequests(auth -> auth ...)`: Define las reglas de autorización basadas en rutas:
       - `.requestMatchers("/test/public", "/h2-console/**").permitAll()`: Permite acceso público a endpoints de prueba y la consola H2 (base de datos).
       - `.requestMatchers("/api/auth/**").permitAll()`: Permite acceso público a endpoints de autenticación (login, logout, etc.) para que los usuarios puedan autenticarse.
       - `.requestMatchers("/test/authenticated", "/test/me").authenticated()`: Requiere autenticación para endpoints de prueba protegidos.
       - `.requestMatchers("/session/**").authenticated()`: Requiere autenticación para todos los endpoints de gestión de sesiones.
       - `.requestMatchers("/ejemplos/**").permitAll()`: Permite acceso público a ejemplos (como el carrito), para probar sin login.
       - `.anyRequest().authenticated()`: Cualquier otra petición requiere autenticación obligatoria.
     - `.headers(headers -> headers.frameOptions(frame -> frame.disable()))`: Deshabilita las restricciones de frames para permitir que la consola H2 se muestre en iframes (necesario para la interfaz web de H2).
     - `.httpBasic(httpBasic -> {})`: Habilita autenticación HTTP Basic (envío de usuario:password en headers). Útil para APIs.
     - `.formLogin(formLogin -> {})`: Habilita login con formularios HTML (por defecto en `/login`). Aunque la app usa APIs REST, esto permite compatibilidad.
     - `.logout(logout -> logout ...)`: Configura el logout:
       - `.logoutUrl("/session/logout")`: Define la URL para cerrar sesión (POST a esta ruta).
       - `.logoutSuccessHandler((request, response, authentication) -> { ... })`: Handler personalizado que responde con status 200 y mensaje "Sesión cerrada exitosamente" en lugar de redirigir.
       - `.invalidateHttpSession(true)`: Invalida la sesión HTTP al hacer logout.
       - `.clearAuthentication(true)`: Limpia el contexto de autenticación de Spring Security.
   - **Resultado**: Devuelve un `SecurityFilterChain` que Spring aplica a todas las peticiones.

2. **`authenticationManager(AuthenticationConfiguration config)`**:
   - **Propósito**: Proporciona el gestor de autenticación usado por los controladores (como `AuthController`).
   - **Lógica**: Obtiene el `AuthenticationManager` configurado automáticamente por Spring Security.

3. **`passwordEncoder()`**:
   - **Propósito**: Define cómo se encriptan las contraseñas.
   - **Lógica**: Usa `PasswordEncoderFactories.createDelegatingPasswordEncoder()`, que soporta múltiples algoritmos (BCrypt por defecto, pero puede manejar otros como SHA-256). Esto permite migrar algoritmos sin romper contraseñas existentes.

**Cómo Encaja en el Proyecto:**
- **Integración con Controladores**: Los controladores como `AuthController` usan el `AuthenticationManager` inyectado. Las reglas de autorización afectan directamente qué endpoints requieren login.
- **Sesiones**: La configuración de logout invalida sesiones, lo que afecta a `SessionController`.
- **Flujo General**: Al iniciar la app, Spring aplica esta configuración. Cada petición pasa por los filtros de seguridad antes de llegar a los controladores.

**Ejemplo de Uso:**
- Sin esta configuración, Spring Security bloquearía todas las peticiones por defecto.
- Para cambiar permisos, edita las reglas en `authorizeHttpRequests` (ej. cambiar `/ejemplos/**` a `authenticated()` si quieres requerir login para el carrito).

Esta configuración asegura que la app sea segura mientras permite acceso público a endpoints necesarios.

---

## Base de Datos y Usuarios

- **BD**: H2 en memoria (`jdbc:h2:mem:testdb`).
- **Usuarios Iniciales** (desde `data.sql`):
  - `user` / `password` (ROLE_USER)
  - `admin` / `admin123` (ROLE_ADMIN)

Accede a H2 Console en `http://localhost:8080/h2-console` para ver datos.

---

## Ejemplos de Uso

### 1. Login y Acceso a Endpoint Protegido

```bash
# Login
curl -c cookies.txt -X POST -H "Content-Type: application/json" -d '{"username":"user","password":"password"}' http://localhost:8080/api/auth/login

# Acceder a endpoint privado
curl -b cookies.txt http://localhost:8080/test/private
```

### 2. Usar el Carrito

```bash
# Agregar producto
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Laptop&cantidad=1"

# Ver carrito
curl -b cookies.txt http://localhost:8080/ejemplos/carrito/ver
```

### 3. Gestionar Sesión

```bash
# Ver info de sesión
curl -b cookies.txt http://localhost:8080/session/info

# Guardar atributo
curl -b cookies.txt -X POST "http://localhost:8080/session/attribute?key=miDato&value=hola"
```

---

## Conceptos Clave

### Sesiones HTTP
- **Qué es**: Almacenamiento de datos por usuario entre peticiones.
- **Cómo funciona**: Spring crea una sesión por usuario, identificada por cookie `JSESSIONID`.
- **Uso**: Carrito, preferencias, etc.

### Autenticación vs Autorización
- **Autenticación**: Verificar identidad (login).
- **Autorización**: Controlar acceso (roles, permisos).

### Spring Security
- **Filtros**: Procesan cada petición.
- **Context**: Almacena usuario autenticado.
- **Roles**: Definidos en BD, usados en `@PreAuthorize`.

---

## Solución de Problemas

- **Error al iniciar**: Verifica JDK 21 y dependencias (`./gradlew build`).
- **Login falla**: Revisa usuarios en H2 Console.
- **Sesión expira**: Configura `server.servlet.session.timeout` en `application.properties`.
- **CORS**: Si usas frontend, agrega configuración en `SecurityConfig.java`.

Para más ayuda, revisa logs en consola o archivos de configuración.

---

## Gestión de Cookies y Sesiones

### ¿Dónde se guardan las cookies al autenticar?

Cuando realizas login exitoso, el servidor Spring Security crea una sesión HTTP y envía una cookie llamada `JSESSIONID` en la respuesta. Esta cookie identifica tu sesión en futuras peticiones.

#### En curl (línea de comandos):
- **Guardar cookies**: Usa `-c archivo.txt` en el login para guardar las cookies en un archivo.
- **Enviar cookies**: Usa `-b archivo.txt` en peticiones posteriores para enviar la cookie guardada.

Ejemplo completo:
```bash
# Login y guardar cookies
curl -c cookies.txt -X POST -H "Content-Type: application/json" -d '{"username":"user","password":"password"}' http://localhost:8080/api/auth/login

# Usar cookies en otras peticiones
curl -b cookies.txt http://localhost:8080/api/auth/me
```

#### En Postman:
- Postman guarda automáticamente las cookies después del login.
- Ve a la pestaña "Cookies" (abajo a la derecha) para ver/verificar las cookies guardadas.
- Las cookies se envían automáticamente en peticiones posteriores al mismo dominio.

#### En el navegador:
- Las cookies se guardan automáticamente en el almacenamiento del navegador.
- Inspecciona en DevTools → Application → Cookies para ver `JSESSIONID`.

#### En el código de la aplicación:
- Las cookies se manejan automáticamente por Spring Security.
- No necesitas código adicional; el framework se encarga de validar la cookie `JSESSIONID` en cada petición.
- Si la sesión expira o es inválida, recibirás un error 401.

**Nota**: Las cookies son específicas por dominio y se envían automáticamente por el cliente HTTP (curl, Postman, navegador). No necesitas manejarlas manualmente en el código del servidor.

---

## Explicación Detallada de UserService.java

**Ubicación:** `src/main/java/app/ejemplo02/service/UserService.java`

**Propósito:** Servicio de negocio que maneja la lógica relacionada con usuarios. Abstrae operaciones de BD y encriptación de contraseñas, usado por controladores y otros servicios.

**Métodos y Lógica Principales:**

1. **`crearUsuario(String username, String passwordPlano, String role)`**:
   - **Propósito**: Crea un nuevo usuario en la BD con contraseña encriptada.
   - **Lógica Detallada**:
     - Crea una instancia de `UserEntity`.
     - Encripta la contraseña plana usando `passwordEncoder.encode(passwordPlano)` (BCrypt por defecto).
     - Asigna username, password encriptada y role.
     - Guarda en BD con `userRepository.save(user)`.
     - Retorna el usuario creado.
   - **Resultado**: Usuario persistido en BD con contraseña segura.

2. **`buscarPorUsername(String username)`**:
   - **Propósito**: Busca un usuario por su nombre de usuario.
   - **Lógica Detallada**:
     - Usa `userRepository.findByUsername(username)` (método derivado de Spring Data JPA).
     - Retorna el usuario si existe, o `null` si no.
   - **Resultado**: Usuario encontrado o null.

3. **`listarTodos()`**:
   - **Propósito**: Lista todos los usuarios de la BD.
   - **Lógica Detallada**:
     - Llama a `userRepository.findAll()` para obtener todos los usuarios.
     - Retorna la lista completa.
   - **Resultado**: Lista de todos los usuarios.

**Cómo Encaja en el Proyecto:**
- **Integración con Controladores**: `DebugController` usa `listarTodos()` para mostrar usuarios. `AuthController` podría usarlo indirectamente via `DbUserDetailsService`.
- **Encriptación**: Asegura que contraseñas se guarden encriptadas, compatible con Spring Security.
- **Abstracción**: Separa lógica de negocio de la capa de datos (repositorio).

**Ejemplo de Uso:**
- Crear usuario: `userService.crearUsuario("nuevo", "pass123", "ROLE_USER");`
- Buscar: `UserEntity user = userService.buscarPorUsername("user");`
- Listar: `List<UserEntity> users = userService.listarTodos();`

Este servicio es esencial para gestionar usuarios de forma segura y centralizada.

---

## Explicación Detallada de SecurityUtils.java

**Ubicación:** `src/main/java/app/ejemplo02/util/SecurityUtils.java`

**Propósito:** Utilidad estática que centraliza operaciones comunes de seguridad relacionadas con sesiones y contexto de autenticación. Proporciona métodos helper para manejar el estado de autenticación de forma segura y consistente.

**Métodos y Lógica Principales:**

1. **`setAuthenticationInSession(HttpServletRequest request, Authentication authentication)`**:
   - **Propósito**: Establece la autenticación en el contexto de seguridad y la guarda en la sesión HTTP.
   - **Lógica Detallada**:
     - Crea un nuevo `SecurityContext` vacío usando `SecurityContextHolder.createEmptyContext()`.
     - Asigna la autenticación al contexto con `securityContext.setAuthentication(authentication)`.
     - Establece el contexto en el `SecurityContextHolder` actual con `SecurityContextHolder.setContext(securityContext)` (esto afecta al hilo actual).
     - Obtiene o crea una sesión HTTP con `request.getSession(true)`.
     - Guarda el `SecurityContext` en la sesión usando la clave estándar `HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY` (que es "SPRING_SECURITY_CONTEXT").
   - **Resultado**: La autenticación queda disponible en el contexto de seguridad y persistida en la sesión para futuras peticiones.

2. **`clearAuthentication(HttpServletRequest request)`**:
   - **Propósito**: Limpia completamente la autenticación, invalidando la sesión y limpiando el contexto.
   - **Lógica Detallada**:
     - Obtiene la sesión actual sin crear una nueva (`request.getSession(false)`).
     - Si existe una sesión, la invalida completamente con `session.invalidate()` (elimina todos los atributos y la marca como expirada).
     - Limpia el contexto de seguridad del hilo actual con `SecurityContextHolder.clearContext()`.
   - **Resultado**: El usuario queda completamente desautenticado, y la sesión se destruye.

**Cómo Encaja en el Proyecto:**
- **Integración con AuthController**: En el método `login()`, después de autenticar exitosamente, se llama `SecurityUtils.setAuthenticationInSession(request, authentication)` para guardar el estado. En `logout()`, se usa `SecurityUtils.clearAuthentication(request)` para cerrar sesión.
- **Centralización**: Evita duplicar código de manejo de sesiones en múltiples lugares. Proporciona una forma consistente de manejar autenticación.
- **Seguridad**: Asegura que el `SecurityContext` se guarde correctamente en la sesión, lo que permite que Spring Security recupere la autenticación en peticiones posteriores.

**Ejemplo de Uso:**
- Después de login exitoso: `SecurityUtils.setAuthenticationInSession(request, authentication);`
- Al hacer logout: `SecurityUtils.clearAuthentication(request);`

Esta utilidad es crucial para el manejo correcto de sesiones en aplicaciones web con Spring Security, asegurando que la autenticación persista entre peticiones y se limpie adecuadamente al cerrar sesión.

---

## Reto Práctico: Miniproyecto  de Sesiones y Cookies

### Descripción del Reto

Para consolidar el aprendizaje sobre sesiones HTTP y cookies en Spring Security, crea un **nuevo proyecto Spring Boot independiente** llamado "TodoApp" que implemente un **sistema de "Lista de Tareas Pendientes" (To-Do List)** almacenado en la sesión del usuario. Este miniproyecto es completamente separado del proyecto actual y te permitirá practicar:

- **Almacenamiento de datos en sesiones**: Guardar listas personalizadas por usuario.
- **Manejo de cookies**: Verificar cómo se mantienen las sesiones entre peticiones.
- **Autenticación y autorización**: Asegurar que cada usuario vea solo su lista.
- **Operaciones CRUD básicas**: Crear, leer, actualizar y eliminar tareas.

**Nota**: Este es un proyecto completamente independiente. Crea un nuevo directorio y proyecto Spring Boot desde cero. **No copies código del proyecto original; implementa todo tú mismo.**

### Objetivos de Aprendizaje

- Comprender cómo Spring Security maneja las sesiones HTTP.
- Aprender a usar `HttpSession` para almacenar datos personalizados.
- Practicar el envío y recepción de cookies en clientes HTTP (Postman, curl).
- Implementar lógica de negocio en controladores REST.

### Requisitos Funcionales

1. **Crear una nueva tarea**: Endpoint `POST /todo/agregar` para añadir una tarea (título, descripción).
2. **Listar tareas**: Endpoint `GET /todo/listar` para ver todas las tareas del usuario.
3. **Marcar como completada**: Endpoint `POST /todo/completar/{id}` para cambiar el estado de una tarea.
4. **Eliminar tarea**: Endpoint `DELETE /todo/eliminar/{id}` para borrar una tarea específica.
5. **Limpiar lista**: Endpoint `DELETE /todo/limpiar` para vaciar toda la lista de tareas.
6. **Login/Logout**: Endpoints para autenticación (`/api/auth/login`, `/api/auth/logout`).
7. **Endpoint público**: `GET /api/auth/public` accesible sin login.

### Instrucciones de Implementación

#### Paso 1: Crear un Nuevo Proyecto Spring Boot

1. Crea un nuevo directorio: `C:\Users\madrid\ws\Labs_DWES_2526\T2\TodoApp`
2. Inicializa con Gradle: `gradle init --type basic --dsl groovy`
3. Configura `build.gradle` con dependencias de Spring Boot (web, security, data-jpa, h2).

#### Paso 2: Configurar la Aplicación

- Crea `TodoAppApplication.java` como clase principal:
  ```java
  @SpringBootApplication
  public class TodoAppApplication {
      public static void main(String[] args) {
          SpringApplication.run(TodoAppApplication.class, args);
      }
  }
  ```

- Configura `application.properties`:
  ```properties
  spring.application.name=TodoApp
  spring.datasource.url=jdbc:h2:mem:testdb
  spring.datasource.driverClassName=org.h2.Driver
  spring.datasource.username=sa
  spring.datasource.password=
  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
  spring.jpa.hibernate.ddl-auto=create-drop
  server.servlet.session.timeout=30m
  spring.h2.console.enabled=true
  spring.h2.console.path=/h2-console
  spring.sql.init.mode=always
  ```

- Crea `data.sql`:
  ```sql
  INSERT INTO users (username, password, role) VALUES ('user', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER');
  INSERT INTO users (username, password, role) VALUES ('admin', '{bcrypt}$2a$12$eTIoaBs2LeiMndO3SQFykuVBMkESD3m43NYBldeHTe1WLxNcXa/SC', 'ROLE_ADMIN');
  ```

#### Paso 3: Implementar Seguridad

- Crea `SecurityConfig.java`:
  ```java
  @Configuration
  @EnableMethodSecurity
  public class SecurityConfig {
      @Bean
      public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          http.csrf(csrf -> csrf.disable())
              .authorizeHttpRequests(auth -> auth
                  .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                  .anyRequest().authenticated()
              )
              .headers(headers -> headers.frameOptions(frame -> frame.disable()))
              .httpBasic(httpBasic -> {})
              .formLogin(formLogin -> {})
              .logout(logout -> logout
                  .logoutUrl("/api/auth/logout")
                  .logoutSuccessHandler((request, response, authentication) -> {
                      response.setStatus(200);
                      response.getWriter().write("Sesión cerrada exitosamente");
                  })
                  .invalidateHttpSession(true)
                  .clearAuthentication(true)
              );
          return http.build();
      }

      @Bean
      public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
          return config.getAuthenticationManager();
      }

      @Bean
      public PasswordEncoder passwordEncoder() {
          return PasswordEncoderFactories.createDelegatingPasswordEncoder();
      }
  }
  ```

- Implementa `UserEntity.java`:
  ```java
  @Entity
  @Table(name = "users")
  public class UserEntity {
      @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      private String username;
      private String password;
      private String role;
      // Getters y setters
  }
  ```

- `UserRepository.java`:
  ```java
  public interface UserRepository extends JpaRepository<UserEntity, Long> {
      Optional<UserEntity> findByUsername(String username);
  }
  ```

- `UserService.java`:
  ```java
  @Service
  public class UserService {
      private final UserRepository userRepository;
      private final PasswordEncoder passwordEncoder;

      public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
          this.userRepository = userRepository;
          this.passwordEncoder = passwordEncoder;
      }

      public UserEntity buscarPorUsername(String username) {
          return userRepository.findByUsername(username).orElse(null);
      }
  }
  ```

- `DbUserDetailsService.java`:
  ```java
  @Service
  public class DbUserDetailsService implements UserDetailsService {
      private final UserRepository userRepository;

      public DbUserDetailsService(UserRepository userRepository) {
          this.userRepository = userRepository;
      }

      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
          UserEntity user = userRepository.findByUsername(username)
              .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
          return new org.springframework.security.core.userdetails.User(
              user.getUsername(),
              user.getPassword(),
              List.of(new SimpleGrantedAuthority(user.getRole()))
          );
      }
  }
  ```

- `SecurityUtils.java`:
  ```java
  public class SecurityUtils {
      public static void setAuthenticationInSession(HttpServletRequest request, Authentication authentication) {
          SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
          securityContext.setAuthentication(authentication);
          SecurityContextHolder.setContext(securityContext);
          HttpSession session = request.getSession(true);
          session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
      }

      public static void clearAuthentication(HttpServletRequest request) {
          HttpSession session = request.getSession(false);
          if (session != null) {
              session.invalidate();
          }
          SecurityContextHolder.clearContext();
      }
  }
  ```

#### Paso 4: Crear DTO y Controladores

- Crea `TareaDTO.java`:
  ```java
  public record TareaDTO(String titulo, String descripcion, boolean completada, long id) {
      public static TareaDTO of(String titulo, String descripcion) {
          return new TareaDTO(titulo, descripcion, false, System.currentTimeMillis());
      }
  }
  ```

- Implementa `AuthController.java`:
  ```java
  @RestController
  @RequestMapping("/api/auth")
  public class AuthController {
      private final AuthenticationManager authenticationManager;

      public AuthController(AuthenticationManager authenticationManager) {
          this.authenticationManager = authenticationManager;
      }

      @PostMapping("/login")
      public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
          try {
              Authentication authentication = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
              );
              SecurityUtils.setAuthenticationInSession(request, authentication);
              return ResponseEntity.ok(Map.of("mensaje", "✅ Login exitoso", "usuario", authentication.getName()));
          } catch (AuthenticationException e) {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "❌ Credenciales inválidas"));
          }
      }

      @PostMapping("/logout")
      public ResponseEntity<?> logout(HttpServletRequest request) {
          SecurityUtils.clearAuthentication(request);
          return ResponseEntity.ok(Map.of("mensaje", "✅ Sesión cerrada exitosamente"));
      }

      @GetMapping("/public")
      public ResponseEntity<String> publico() {
          return ResponseEntity.ok("Este es un endpoint público - accesible sin autenticación");
      }

      public record LoginRequest(String username, String password) {}
  }
  ```

- Implementa `TodoController.java`:
  ```java
  @RestController
  @RequestMapping("/todo")
  public class TodoController {

      @SuppressWarnings("unchecked")
      @PostMapping("/agregar")
      public ResponseEntity<String> agregarTarea(HttpSession session, @RequestParam String titulo, @RequestParam String descripcion) {
          List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");
          if (tareas == null) {
              tareas = new ArrayList<>();
          }
          tareas.add(TareaDTO.of(titulo, descripcion));
          session.setAttribute("tareas", tareas);
          return ResponseEntity.ok("✅ Tarea agregada. Total tareas: " + tareas.size());
      }

      @SuppressWarnings("unchecked")
      @GetMapping("/listar")
      public ResponseEntity<?> verTareas(HttpSession session) {
          List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");
          if (tareas == null || tareas.isEmpty()) {
              return ResponseEntity.ok(Map.of("mensaje", "No hay tareas pendientes", "tareas", List.of()));
          }
          return ResponseEntity.ok(Map.of("tareas", tareas, "total", tareas.size()));
      }

      @SuppressWarnings("unchecked")
      @PostMapping("/completar/{id}")
      public ResponseEntity<String> completarTarea(HttpSession session, @PathVariable long id) {
          List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");
          if (tareas == null) {
              return ResponseEntity.badRequest().body("❌ No hay tareas");
          }
          for (TareaDTO tarea : tareas) {
              if (tarea.id() == id) {
                  TareaDTO completada = new TareaDTO(tarea.titulo(), tarea.descripcion(), true, tarea.id());
                  tareas.set(tareas.indexOf(tarea), completada);
                  session.setAttribute("tareas", tareas);
                  return ResponseEntity.ok("✅ Tarea completada");
              }
          }
          return ResponseEntity.notFound().build();
      }

      @SuppressWarnings("unchecked")
      @DeleteMapping("/eliminar/{id}")
      public ResponseEntity<String> eliminarTarea(HttpSession session, @PathVariable long id) {
          List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");
          if (tareas == null) {
              return ResponseEntity.badRequest().body("❌ No hay tareas");
          }
          tareas.removeIf(t -> t.id() == id);
          session.setAttribute("tareas", tareas);
          return ResponseEntity.ok("🗑️ Tarea eliminada");
      }

      @DeleteMapping("/limpiar")
      public ResponseEntity<String> limpiarTareas(HttpSession session) {
          session.removeAttribute("tareas");
          return ResponseEntity.ok("🗑️ Lista de tareas limpiada");
      }
  }
  ````
