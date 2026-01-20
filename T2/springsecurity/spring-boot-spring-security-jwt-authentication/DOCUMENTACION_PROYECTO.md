# Documentación Completa: Spring Boot JWT Authentication

## 📋 Índice

1. [Visión General del Proyecto](#visión-general-del-proyecto)
2. [Arquitectura y Flujo de Autenticación](#arquitectura-y-flujo-de-autenticación)
3. [Models](#models)
4. [Payloads](#payloads)
5. [Repository](#repository)
6. [Security](#security)
7. [Controllers](#controllers)
8. [Componentes Auxiliares](#componentes-auxiliares)

---

## 🎯 Visión General del Proyecto

Este proyecto implementa un sistema de autenticación y autorización basado en **JWT (JSON Web Tokens)** utilizando **Spring Boot** y **Spring Security**. El sistema permite:

- ✅ Registro de nuevos usuarios
- ✅ Autenticación mediante login
- ✅ Generación de tokens JWT
- ✅ Validación de tokens en cada solicitud
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Revocación de tokens

**Tecnologías principales:**

- Spring Boot 3.x
- Spring Security 6.x
- JWT (JJWT 0.12.x)
- JPA/Hibernate
- Base de datos relacional (H2/MySQL)
- Jakarta Persistence API

---

## 🔄 Arquitectura y Flujo de Autenticación

### Flujo de Registro (Signup)

```
Cliente → POST /api/auth/signup
         ↓
    Validar datos de entrada
         ↓
    Verificar username/email únicos
         ↓
    Hashear contraseña (BCrypt)
         ↓
    Crear usuario en BD
         ↓
    Asignar roles
         ↓
    Guardar en base de datos
         ↓
    Respuesta: Mensaje de éxito/error
```

### Flujo de Autenticación (Login)

```
Cliente → POST /api/auth/signin (username, password)
         ↓
    Validar credenciales
         ↓
    Generar JWT Token
         ↓
    Respuesta: JwtResponse (token + datos usuario)
```

### Flujo de Validación de Token

```
Cliente → GET /api/test/** (con Authorization: Bearer {token})
         ↓
    AuthTokenFilter intercepta solicitud
         ↓
    Extrae token del header
         ↓
    Valida firma y fecha de expiración
         ↓
    Verifica si token está revocado
         ↓
    Carga detalles del usuario
         ↓
    Establece autenticación en SecurityContext
         ↓
    Procesa solicitud o rechaza
```

---

## 📊 Models

### 1. **User.java** - Entidad de Usuario

**Propósito:** Representa la tabla `users` en la base de datos. Almacena información de los usuarios registrados.

**Anotaciones principales:**

- `@Entity`: Define que es una entidad JPA
- `@Table`: Especifica el nombre de la tabla y restricciones únicas
- `@UniqueConstraint`: Garantiza que `username` y `email` sean únicos

**Atributos:**

| Atributo   | Tipo      | Descripción                 | Validaciones                           |
| ---------- | --------- | --------------------------- | -------------------------------------- |
| `id`       | Long      | ID único del usuario        | `@GeneratedValue(IDENTITY)`            |
| `username` | String    | Nombre de usuario           | `@NotBlank`, `@Size(max=20)`           |
| `email`    | String    | Email del usuario           | `@NotBlank`, `@Email`, `@Size(max=50)` |
| `password` | String    | Contraseña hasheada         | `@NotBlank`, `@Size(max=120)`          |
| `roles`    | Set<Role> | Conjunto de roles asignados | `@ManyToMany`, `LAZY`                  |

**Relaciones:**

- **ManyToMany** con `Role`: Un usuario puede tener múltiples roles, un rol puede asignarse a múltiples usuarios
- Tabla de unión: `user_roles`

**Ejemplo de uso:**

```java
User user = new User("john", "john@example.com", "hashedPassword123");
user.getRoles().add(roleUser);
userRepository.save(user);
```

---

### 2. **Role.java** - Entidad de Rol

**Propósito:** Representa los roles disponibles en el sistema (`ROLE_USER`, `ROLE_MODERATOR`, `ROLE_ADMIN`).

**Anotaciones principales:**

- `@Entity`: Define que es una entidad JPA
- `@Enumerated(EnumType.STRING)`: Guarda el enum como String en BD

**Atributos:**

| Atributo | Tipo    | Descripción           |
| -------- | ------- | --------------------- |
| `id`     | Integer | ID único del rol      |
| `name`   | ERole   | Nombre del rol (enum) |

**Relaciones:**

- Inversa de la relación ManyToMany en `User`

**Ejemplo de uso:**

```java
Role roleUser = new Role(ERole.ROLE_USER);
roleRepository.save(roleUser);
```

---

### 3. **ERole.java** - Enumeración de Roles

**Propósito:** Define los tres roles disponibles en el sistema de forma segura.

**Valores:**

```java
ROLE_USER       // Usuario básico
ROLE_MODERATOR  // Moderador con permisos adicionales
ROLE_ADMIN      // Administrador con acceso total
```

**Uso en seguridad:**

- Utilizado en anotaciones `@PreAuthorize`
- Comparado durante la validación de autorización
- Almacenado en los claims del JWT

---

## 📦 Payloads

Los payloads son objetos DTO (Data Transfer Object) que definen la estructura de datos intercambiados entre cliente y servidor.

### Request Payloads

#### 1. **LoginRequest.java** - Solicitud de Autenticación

**Propósito:** Captura las credenciales del usuario para autenticarse.

**Atributos:**

| Atributo   | Tipo   | Validación  | Descripción               |
| ---------- | ------ | ----------- | ------------------------- |
| `username` | String | `@NotBlank` | Nombre de usuario         |
| `password` | String | `@NotBlank` | Contraseña en texto plano |

**Ejemplo JSON:**

```json
{
  "username": "john",
  "password": "password123"
}
```

**Flujo de procesamiento:**

1. Cliente envía POST a `/api/auth/signin`
2. Spring deserializa JSON a `LoginRequest`
3. `AuthController` utiliza estos datos para autenticar
4. `AuthenticationManager` verifica credenciales contra BD

---

#### 2. **SignupRequest.java** - Solicitud de Registro

**Propósito:** Captura los datos del nuevo usuario para registrarse.

**Atributos:**

| Atributo   | Tipo        | Validación                             | Descripción                             |
| ---------- | ----------- | -------------------------------------- | --------------------------------------- |
| `username` | String      | `@NotBlank`, `@Size(min=3, max=20)`    | Nombre único (3-20 caracteres)          |
| `email`    | String      | `@NotBlank`, `@Email`, `@Size(max=50)` | Email válido y único                    |
| `password` | String      | `@NotBlank`, `@Size(min=6, max=40)`    | Contraseña fuerte (6-40 caracteres)     |
| `role`     | Set<String> | Opcional                               | Roles solicitados (ej: ["user", "mod"]) |

**Ejemplo JSON:**

```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123",
  "role": ["user"]
}
```

**Validación en el controlador:**

1. Verificar que username no existe
2. Verificar que email no existe
3. Hashear contraseña con BCrypt
4. Crear usuario en BD
5. Asignar roles (por defecto "user" si no se especifica)

---

### Response Payloads

#### 1. **JwtResponse.java** - Respuesta de Autenticación Exitosa

**Propósito:** Retorna el token JWT y datos del usuario autenticado.

**Atributos:**

| Atributo   | Tipo         | Descripción                          |
| ---------- | ------------ | ------------------------------------ |
| `token`    | String       | Token JWT generado                   |
| `type`     | String       | Tipo de token (por defecto "Bearer") |
| `id`       | Long         | ID del usuario autenticado           |
| `username` | String       | Nombre de usuario                    |
| `email`    | String       | Email del usuario                    |
| `roles`    | List<String> | Roles asignados al usuario           |

**Ejemplo JSON:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

**Uso del cliente:**

```javascript
// El cliente guarda el token
const token = response.token;

// Luego lo usa en solicitudes posteriores
fetch("/api/test/user", {
  headers: {
    Authorization: "Bearer " + token,
  },
});
```

---

#### 2. **MessageResponse.java** - Respuesta de Mensaje Genérico

**Propósito:** Comunica mensajes de éxito o error al cliente.

**Atributos:**

| Atributo  | Tipo   | Descripción          |
| --------- | ------ | -------------------- |
| `message` | String | Mensaje de respuesta |

**Ejemplos de uso:**

```json
// Error: Username duplicado
{
  "message": "Error: Username is already taken!"
}

// Error: Email duplicado
{
  "message": "Error: Email is already in use!"
}

// Éxito: Usuario registrado
{
  "message": "User registered successfully!"
}
```

---

## 🗄️ Repository

Los repositorios son interfaces que extienden `JpaRepository` y proporcionan métodos para acceder a la base de datos.

### 1. **UserRepository.java**

**Propósito:** Define operaciones CRUD y búsquedas personalizadas para la entidad `User`.

**Métodos:**

#### `findByUsername(String username): Optional<User>`

- **Descripción:** Busca un usuario por su nombre
- **Retorno:** `Optional<User>` (presente si existe)
- **Uso en seguridad:** Cargar usuario durante autenticación
- **Implementación Spring Data:** Generada automáticamente

```java
// Uso
userRepository.findByUsername("john")
  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
```

#### `existsByUsername(String username): Boolean`

- **Descripción:** Verifica si un username ya está registrado
- **Retorno:** `true` si existe, `false` si no
- **Uso en seguridad:** Validación de registro de nuevos usuarios
- **Query implícita:** `SELECT COUNT(*) > 0 FROM users WHERE username = ?`

```java
// Uso
if (userRepository.existsByUsername(signUpRequest.getUsername())) {
  return error("Username already taken");
}
```

#### `existsByEmail(String email): Boolean`

- **Descripción:** Verifica si un email ya está registrado
- **Retorno:** `true` si existe, `false` si no
- **Uso en seguridad:** Validación de emails únicos
- **Query implícita:** `SELECT COUNT(*) > 0 FROM users WHERE email = ?`

```java
// Uso
if (userRepository.existsByEmail(signUpRequest.getEmail())) {
  return error("Email already in use");
}
```

**Herencia de JpaRepository:**

```java
// Métodos automáticos heredados:
save(User user)              // Guardar/actualizar
findById(Long id)            // Buscar por ID
findAll()                    // Obtener todos
delete(User user)            // Eliminar
```

---

### 2. **RoleRepository.java**

**Propósito:** Define operaciones CRUD y búsquedas para la entidad `Role`.

**Métodos:**

#### `findByName(ERole name): Optional<Role>`

- **Descripción:** Busca un rol por su nombre (enum)
- **Retorno:** `Optional<Role>`
- **Uso en seguridad:** Cargar roles durante registro
- **Query implícita:** `SELECT * FROM roles WHERE name = ?`

```java
// Uso en asignación de roles
Role userRole = roleRepository.findByName(ERole.ROLE_USER)
  .orElseThrow(() -> new RuntimeException("Role not found"));
user.getRoles().add(userRole);
```

**Herencia de JpaRepository:**

```java
// Métodos automáticos:
save(Role role)              // Guardar rol
findAll()                    // Obtener todos los roles
count()                      // Contar total de roles
```

---

## 🔐 Security

La seguridad del proyecto se gestiona a través de varias clases que trabajan en conjunto.

### 1. **WebSecurityConfig.java** - Configuración General de Seguridad

**Propósito:** Clase de configuración que define todas las reglas de seguridad de Spring Security.

**Anotaciones principales:**

- `@Configuration`: Define que es una clase de configuración Bean
- `@EnableMethodSecurity`: Habilita validaciones `@PreAuthorize` en métodos

**Componentes configurados:**

#### PasswordEncoder - `passwordEncoder()`

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

- **Propósito:** Define el algoritmo de encriptación de contraseñas
- **BCrypt:** Algoritmo iterativo que ralentiza ataques de fuerza bruta
- **Factor de trabajo:** 10 iteraciones por defecto
- **Salto:** Generado automáticamente por BCrypt

#### DaoAuthenticationProvider - `authenticationProvider()`

```java
@Bean
public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}
```

- **Propósito:** Proveedor de autenticación que valida credenciales
- **Flujo:**
  1. Carga detalles del usuario desde BD (UserDetailsServiceImpl)
  2. Valida que la contraseña proporcionada coincida (BCrypt)
  3. Retorna objeto Authentication si es válido

#### AuthenticationManager - `authenticationManager()`

```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
    throws Exception {
    return authConfig.getAuthenticationManager();
}
```

- **Propósito:** Orquesta el proceso de autenticación
- **Uso:** En `AuthController.signin()` para validar credenciales

#### AuthTokenFilter - `authenticationJwtTokenFilter()`

```java
@Bean
public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
}
```

- **Propósito:** Filtro que intercepta todas las solicitudes HTTP
- **Responsabilidades:**
  1. Extrae JWT del header Authorization
  2. Valida la firma y fecha de expiración
  3. Verifica si el token está revocado
  4. Carga usuario asociado y establece autenticación

#### SecurityFilterChain - `filterChain()`

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .exceptionHandling(exception ->
            exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth ->
          auth.requestMatchers("/api/auth/**").permitAll()
              .requestMatchers("/api/test/**").permitAll()
              .anyRequest().authenticated()
        );

    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(authenticationJwtTokenFilter(),
                         UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

**Configuraciones clave:**

| Configuración   | Valor              | Propósito                                 |
| --------------- | ------------------ | ----------------------------------------- |
| CSRF            | Deshabilitado      | APIs REST stateless no necesitan CSRF     |
| Session Policy  | STATELESS          | No mantiene sesiones servidor, usa tokens |
| `/api/auth/**`  | Permitido sin auth | Permite registro y login                  |
| `/api/test/**`  | Permitido sin auth | Permite acceso público a pruebas          |
| Otros endpoints | Requieren auth     | Todas demás rutas necesitan token válido  |

---

### 2. **JwtUtils.java** - Utilidades JWT

**Propósito:** Clase que maneja toda la lógica de generación, validación y extracción de información de tokens JWT.

**Configuraciones (application.properties):**

```properties
dam2.app.jwtSecret=myJwtSecretKeyThatIsAtLeast32CharactersLong
dam2.app.jwtExpirationMs=86400000  # 24 horas en milisegundos
```

**Métodos principales:**

#### `generateJwtToken(Authentication authentication): String`

- **Propósito:** Crea un nuevo token JWT válido
- **Entrada:** Objeto Authentication (contiene usuario autenticado)
- **Salida:** String con el token JWT
- **Algoritmo:** HS256 (HMAC con SHA-256)
- **Contenido del token:**
  - `sub` (subject): Username del usuario
  - `iat` (issued at): Timestamp de creación
  - `exp` (expiration): Timestamp de expiración (iat + jwtExpirationMs)
  - Firma con clave secreta

```java
// Estructura del JWT
// Header: {"alg":"HS256","typ":"JWT"}
// Payload: {"sub":"john","iat":1641234567,"exp":1641320967}
// Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

#### `validateJwtToken(String authToken): boolean`

- **Propósito:** Valida que el token sea correcto y no esté expirado
- **Validaciones:**
  1. Firma válida (usando la clave secreta)
  2. No expirado
  3. No malformado
- **Retorno:** `true` si es válido, `false` si no
- **Excepciones capturadas:**
  - `MalformedJwtException`: Token mal formado
  - `ExpiredJwtException`: Token expirado
  - `UnsupportedJwtException`: Algoritmo no soportado
  - `IllegalArgumentException`: Token vacío

```java
// Uso
if (jwtUtils.validateJwtToken(jwt)) {
    // Token válido, procesar
} else {
    // Token inválido, rechazar
}
```

#### `getUserNameFromJwtToken(String token): String`

- **Propósito:** Extrae el username del token sin validar (solo parsea)
- **Entrada:** String con token JWT
- **Salida:** Username (subject del token)
- **Uso:** En AuthTokenFilter para cargar usuario tras validar

```java
String username = jwtUtils.getUserNameFromJwtToken(jwt);
// Resultado: "john"
```

#### `key(): Key`

- **Propósito:** Genera la clave criptográfica a partir del secreto
- **Proceso:**
  1. Decodifica el secreto desde Base64
  2. Genera clave HMAC SHA-256
- **Uso interno:** Usado por todos los métodos anteriores

---

### 3. **AuthTokenFilter.java** - Filtro de Validación JWT

**Propósito:** Filtro que intercepta TODAS las solicitudes HTTP y valida el token JWT.

**Herencia:**

- Extiende `OncePerRequestFilter`: Garantiza ejecución una sola vez por solicitud

**Método principal: `doFilterInternal()`**

**Flujo paso a paso:**

```
1. Intercepta solicitud HTTP
   ↓
2. Extrae JWT del header Authorization
   - Formato esperado: "Bearer <token>"
   ↓
3. Si JWT existe:
   a) Valida la firma y fecha de expiración
   b) Verifica si el token está revocado
   c) Extrae username del token
   d) Carga detalles del usuario desde BD
   e) Crea objeto UsernamePasswordAuthenticationToken
   f) Lo establece en SecurityContextHolder
   ↓
4. Si JWT no es válido:
   - Registra error en logs
   - Continúa sin autenticación
   ↓
5. Pasa solicitud al siguiente filtro o controlador
```

**Extracción del JWT:**

```java
private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) &&
        headerAuth.startsWith("Bearer ")) {
        return headerAuth.substring(7);  // Extrae token sin "Bearer "
    }
    return null;
}

// Ejemplo:
// Header: "Authorization: Bearer eyJhbGc..."
// Resultado: "eyJhbGc..."
```

**Validación de revocación:**

```java
// Verificar si el token ha sido revocado
if (authController.isTokenRevoked(jwt)) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write("Error: Token has been revoked");
    return;
}
```

**Establecimiento de autenticación:**

```java
UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()  // Roles del usuario
    );

authentication.setDetails(
    new WebAuthenticationDetailsSource()
        .buildDetails(request)
);

SecurityContextHolder.getContext()
    .setAuthentication(authentication);
```

---

### 4. **AuthEntryPointJwt.java** - Manejador de Errores de Autenticación

**Propósito:** Implementa `AuthenticationEntryPoint` para personalizar respuestas de errores de autenticación.

**Interfaz implementada:**

```java
public interface AuthenticationEntryPoint {
    void commence(HttpServletRequest request,
                  HttpServletResponse response,
                  AuthenticationException authException)
        throws IOException, ServletException;
}
```

**Método: `commence()`**

Se invoca cuando acceso a recurso protegido sin autenticación válida.

**Respuesta JSON personalizada:**

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Token no válido o ausente",
  "path": "/api/test/user"
}
```

**Configuración de respuesta:**

```java
response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // JSON
response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 401

final Map<String, Object> body = new HashMap<>();
body.put("status", 401);
body.put("error", "Unauthorized");
body.put("message", authException.getMessage());
body.put("path", request.getServletPath());

final ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(response.getOutputStream(), body);
```

---

### 5. **UserDetailsImpl.java** - Implementación de UserDetails

**Propósito:** Implementa la interfaz `UserDetails` de Spring Security, representando un usuario autenticado.

**Interfaz implementada:**

```java
public interface UserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();
    String getPassword();
    String getUsername();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();
}
```

**Atributos:**
| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `id` | Long | ID del usuario |
| `username` | String | Nombre de usuario |
| `email` | String | Email |
| `password` | String | Contraseña hasheada (anotada con `@JsonIgnore`) |
| `authorities` | Collection | Roles/permisos del usuario |

**Método estático: `build(User user)`**

Crea una instancia de `UserDetailsImpl` a partir de una entidad `User`:

```java
public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());

    return new UserDetailsImpl(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getPassword(),
        authorities);
}

// Ejemplo:
// Usuario: john con roles [ROLE_USER, ROLE_MODERATOR]
// Resultado: UserDetailsImpl con authorities =
//   [SimpleGrantedAuthority("ROLE_USER"),
//    SimpleGrantedAuthority("ROLE_MODERATOR")]
```

**Métodos de verificación:**

```java
@Override
public boolean isAccountNonExpired() { return true; }

@Override
public boolean isAccountNonLocked() { return true; }

@Override
public boolean isCredentialsNonExpired() { return true; }

@Override
public boolean isEnabled() { return true; }
```

---

### 6. **UserDetailsServiceImpl.java** - Servicio de Detalles de Usuario

**Propósito:** Implementa `UserDetailsService` para cargar detalles del usuario desde BD.

**Interfaz implementada:**

```java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException;
}
```

**Método: `loadUserByUsername(String username)`**

```java
@Override
@Transactional
public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {

    User user = userRepository.findByUsername(username)
        .orElseThrow(() ->
            new UsernameNotFoundException(
                "User Not Found with username: " + username
            )
        );

    return UserDetailsImpl.build(user);
}
```

**Flujo:**

1. Busca usuario en BD por username
2. Si no existe: lanza `UsernameNotFoundException`
3. Si existe: construye y retorna `UserDetailsImpl`

**Anotación `@Transactional`:**

- Abre transacción BD para la búsqueda
- Lazy-load de roles asociados
- Cierra transacción al terminar

**Invocación:**

```
DaoAuthenticationProvider → UserDetailsServiceImpl.loadUserByUsername()
                         ↓
                    UserRepository.findByUsername()
                         ↓
                    Retorna User
                         ↓
                    UserDetailsImpl.build(User)
                         ↓
                    Retorna UserDetails
```

---

## 🎮 Controllers

### 1. **AuthController.java** - Autenticación y Registro

**Propósito:** Maneja endpoints de autenticación y registro de usuarios.

**Anotaciones:**

- `@RestController`: Define clase como controlador REST
- `@CrossOrigin(origins = "*", maxAge = 3600)`: Permite solicitudes CORS desde cualquier origen, caché de 1 hora
- `@RequestMapping("/api/auth")`: Ruta base

**Atributos inyectados:**

```java
@Autowired AuthenticationManager authenticationManager;
@Autowired UserRepository userRepository;
@Autowired RoleRepository roleRepository;
@Autowired PasswordEncoder encoder;
@Autowired JwtUtils jwtUtils;

// Para revocación de tokens
private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();
```

#### Endpoint: `POST /api/auth/signin` - Login

```java
@PostMapping("/signin")
public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
```

**Flujo detallado:**

```
1. Cliente envía JSON:
   {"username": "john", "password": "password123"}

2. Spring valida con @Valid

3. Crear token de autenticación:
   UsernamePasswordAuthenticationToken(username, password)

4. AuthenticationManager valida:
   a) DaoAuthenticationProvider busca usuario en BD
   b) UserDetailsServiceImpl.loadUserByUsername()
   c) Compara contraseña hasheada con BCrypt
   d) Si válido: retorna Authentication con usuario + roles
   e) Si inválido: lanza BadCredentialsException

5. Generar JWT:
   jwtUtils.generateJwtToken(authentication)

6. Extraer datos de usuario:
   userDetails.getId()
   userDetails.getUsername()
   userDetails.getEmail()
   userDetails.getAuthorities() → List<String> roles

7. Retornar JwtResponse con:
   - Token JWT
   - ID usuario
   - Username
   - Email
   - Lista de roles
```

**Respuesta exitosa (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "roles": ["ROLE_USER", "ROLE_MODERATOR"]
}
```

**Respuesta con error (401 Unauthorized):**

```
BadCredentialsException → Controlador global captura →
Respuesta de error personalizada
```

---

#### Endpoint: `POST /api/auth/signup` - Registro

```java
@PostMapping("/signup")
public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest)
```

**Flujo detallado:**

```
1. Cliente envía JSON:
   {
     "username": "john",
     "email": "john@example.com",
     "password": "password123",
     "role": ["user"]
   }

2. Validaciones automáticas (@Valid):
   - username: 3-20 caracteres
   - email: formato válido
   - password: 6-40 caracteres

3. Verificar username único:
   if (userRepository.existsByUsername(username))
       ↓ Existe
       Retornar error 400: "Username already taken"

4. Verificar email único:
   if (userRepository.existsByEmail(email))
       ↓ Existe
       Retornar error 400: "Email already in use"

5. Hashear contraseña:
   encoder.encode(password)  # Usa BCrypt

6. Crear instancia de User:
   new User(username, email, hashedPassword)

7. Procesar roles:
   - Si SignupRequest.role está vacío:
     → Asignar ROLE_USER por defecto
   - Si SignupRequest.role contiene valores:
     → Buscar cada rol en BD (RoleRepository)
     → Si no existe rol: ignorar (o lanzar error según config)
     → Agregar rol al usuario

8. Guardar usuario en BD:
   userRepository.save(user)

9. Retornar respuesta de éxito
```

**Ejemplo completo de registro:**

```json
// Request
POST /api/auth/signup
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "securePass123",
  "role": ["user", "mod"]
}

// Response 200 OK
{
  "message": "User registered successfully!"
}

// En BD se crea:
// users: id=2, username="alice", email="alice@example.com",
//        password="$2a$10$...(hasheada)"
// user_roles: user_id=2, role_id=1 (ROLE_USER)
//             user_id=2, role_id=2 (ROLE_MODERATOR)
```

**Respuesta error - Username duplicado (400 Bad Request):**

```json
{
  "message": "Error: Username is already taken!"
}
```

---

#### Método auxiliar: `isTokenRevoked(String token): boolean`

```java
public boolean isTokenRevoked(String token) {
    return revokedTokens.contains(token);
}
```

**Propósito:** Verifica si un token ha sido revocado (logout)

**Estructura de datos:**

```java
private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();
```

- `ConcurrentHashMap`: Thread-safe, no requiere sincronización manual
- `newKeySet()`: Retorna Set backed por la estructura

**Uso en AuthTokenFilter:**

```java
if (authController.isTokenRevoked(jwt)) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
}
```

---

### 2. **TestController.java** - Endpoints de Prueba

**Propósito:** Proporciona endpoints protegidos por roles para pruebas de autorización.

**Anotaciones:**

- `@CrossOrigin(origins = "*", maxAge = 3600)`: CORS permitido
- `@RestController`: Controlador REST
- `@RequestMapping("/api/test")`: Ruta base

#### Endpoint público: `GET /api/test/all`

```java
@GetMapping("/all")
public String allAccess() {
    return "Public Content.";
}
```

**Acceso:** Sin autenticación requerida
**Respuesta:**

```
Public Content.
```

---

#### Endpoint protegido - Roles múltiples: `GET /api/test/user`

```java
@GetMapping("/user")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
public String userAccess() {
    return "User Content.";
}
```

**Anotación `@PreAuthorize`:**

- Se evalúa ANTES de ejecutar el método
- Autoriza si usuario tiene AL MENOS UNO de estos roles: USER, MODERATOR, ADMIN
- Si no autorizado: lanza `AccessDeniedException` → AuthEntryPointJwt maneja

**Acceso:**

- Requerido token JWT válido
- Usuario debe tener rol USER, MODERATOR o ADMIN
- Verificación: `Authentication.getAuthorities()` vs roles requeridos

**Respuesta exitosa (200 OK):**

```
User Content.
```

**Respuesta sin autorización (403 Forbidden):**

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

---

#### Endpoint protegido - Rol específico: `GET /api/test/mod`

```java
@GetMapping("/mod")
@PreAuthorize("hasRole('MODERATOR')")
public String moderatorAccess() {
    return "Moderator Board.";
}
```

**Acceso:** Solo usuarios con rol MODERATOR exactamente

---

#### Endpoint protegido - Rol administrador: `GET /api/test/admin`

```java
@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public String adminAccess() {
    return "Admin Board.";
}
```

**Acceso:** Solo usuarios con rol ADMIN exactamente

---

**Matriz de acceso:**

| Endpoint          | Sin JWT | USER | MODERATOR | ADMIN |
| ----------------- | ------- | ---- | --------- | ----- |
| `/api/test/all`   | ✅      | ✅   | ✅        | ✅    |
| `/api/test/user`  | ❌      | ✅   | ✅        | ✅    |
| `/api/test/mod`   | ❌      | ❌   | ✅        | ❌    |
| `/api/test/admin` | ❌      | ❌   | ❌        | ✅    |

---

## 🛠️ Componentes Auxiliares

### DataInitializer.java - Inicializador de Datos

**Propósito:** Inicializa la base de datos con los roles predeterminados al arrancar la aplicación.

**Implementación:**

```java
@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_USER));
            roleRepository.save(new Role(ERole.ROLE_MODERATOR));
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            System.out.println(
                "Default roles created: ROLE_USER, ROLE_MODERATOR, ROLE_ADMIN"
            );
        }
    }
}
```

**Interfaz `CommandLineRunner`:**

- Se ejecuta automáticamente al arrancar Spring Boot
- Implementa método `run(String... args)`

**Lógica:**

```
1. Al iniciar la aplicación:
   ↓
2. Spring ejecuta DataInitializer.run()
   ↓
3. Consultar: ¿Existen roles en BD?
   - Si SÍ: No hacer nada (evitar duplicados)
   - Si NO: Crear tres roles por defecto
   ↓
4. Crear y guardar:
   new Role(ROLE_USER)
   new Role(ROLE_MODERATOR)
   new Role(ROLE_ADMIN)
   ↓
5. Imprimir confirmación en consola
```

**Resultado en BD después del arranque:**

```sql
SELECT * FROM roles;
-- id | name
-- 1  | ROLE_USER
-- 2  | ROLE_MODERATOR
-- 3  | ROLE_ADMIN
```

---

### SpringBootSecurityJwtApplication.java - Punto de Entrada

**Propósito:** Clase principal que arranca la aplicación Spring Boot.

```java
@SpringBootApplication
public class SpringBootSecurityJwtApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootSecurityJwtApplication.java, args);
    }
}
```

**Anotación `@SpringBootApplication`:**

- Combina `@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan`
- Habilita:
  - Configuración automática de componentes
  - Escaneo de paquetes para detectar beans
  - Propiedades de application.properties

**Flujo de inicio:**

```
1. main() invocado
   ↓
2. SpringApplication.run() inicia contexto Spring
   ↓
3. Auto-configuración:
   - Detecta dependencias (JWT, BD, Spring Security)
   - Configura beans automáticamente
   ↓
4. Componentes inicializados:
   - Repositorios
   - Servicios de seguridad
   - Controladores
   - DataInitializer (crea roles)
   ↓
5. Aplicación lista para recibir solicitudes
```

---

## 🔗 Diagrama de Flujo General

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENTE                                   │
│                  (Navegador/App)                            │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ├─── POST /api/auth/signup ──→
                     │   {username, email, password, role}
                     │
                     │    AuthController.registerUser()
                     │    ├─ Validar datos
                     │    ├─ Verificar username/email únicos
                     │    ├─ Hashear contraseña (BCrypt)
                     │    ├─ Crear User
                     │    ├─ Asignar roles
                     │    └─ Guardar en BD
                     │
              ←──────┴─ {message: "success"}
                     │
                     ├─── POST /api/auth/signin ──→
                     │   {username, password}
                     │
                     │    AuthController.authenticateUser()
                     │    ├─ AuthenticationManager
                     │    ├─ DaoAuthenticationProvider
                     │    ├─ UserDetailsServiceImpl
                     │    │  └─ UserRepository.findByUsername()
                     │    ├─ BCrypt validar contraseña
                     │    ├─ JwtUtils.generateJwtToken()
                     │    └─ Retornar Authentication
                     │
              ←──────┴─ {token, id, username, email, roles}
                     │
                     ├─── GET /api/test/user ──→
                     │   Header: Authorization: Bearer {token}
                     │
                     │    AuthTokenFilter.doFilterInternal()
                     │    ├─ Extrae JWT del header
                     │    ├─ JwtUtils.validateJwtToken()
                     │    ├─ Verifica revocación
                     │    ├─ Extrae username
                     │    ├─ UserDetailsServiceImpl.loadUserByUsername()
                     │    ├─ Establece SecurityContext
                     │    └─ Pasa a siguiente filtro
                     │
                     │    TestController.userAccess()
                     │    ├─ @PreAuthorize valida roles
                     │    └─ Retorna "User Content."
                     │
              ←──────┴─ "User Content."
```

---

## 📝 Resumen de Relaciones entre Componentes

```
UserRepository ←─────────┬──────────→ User Entity
                         │
                    ManyToMany
                         │
                         └──────────→ Role Entity ←─ RoleRepository
                                           ▲
                                           │
                                         ERole (enum)

AuthController
├─ Usa: AuthenticationManager
├─ Usa: UserRepository
├─ Usa: RoleRepository
├─ Usa: PasswordEncoder
├─ Usa: JwtUtils
└─ Mantiene: revokedTokens

WebSecurityConfig
├─ Configura: SecurityFilterChain
├─ Crea: AuthenticationManager
├─ Crea: PasswordEncoder (BCrypt)
├─ Crea: DaoAuthenticationProvider
├─ Crea: AuthTokenFilter
└─ Crea: AuthEntryPointJwt

AuthTokenFilter
├─ Usa: JwtUtils (validar)
├─ Usa: UserDetailsServiceImpl (cargar usuario)
└─ Usa: AuthController (verificar revocación)

JwtUtils
└─ Usa: @Value properties (secret, expirationMs)

UserDetailsServiceImpl
├─ Usa: UserRepository
└─ Crea: UserDetailsImpl

AuthEntryPointJwt
└─ Implementa: AuthenticationEntryPoint
```

---

## 🚀 Flujo Completo de Autenticación y Autorización

```
REGISTRO:
┌─────────────────────────────────────────────────┐
│ 1. Cliente envía datos: POST /api/auth/signup   │
│    {username, email, password, role}            │
└──────────────────┬──────────────────────────────┘
                   ↓
          [AuthController.registerUser()]
                   ↓
        ┌─────────────────────────┐
        │ 2. Validaciones básicas │
        └────────────┬────────────┘
                     ↓
    ┌────────────────────────────────────────┐
    │ 3. ¿Username existe?                   │
    │    ├─ SÍ: Error 400 "Ya existe"       │
    │    └─ NO: Continuar                   │
    └────────────┬───────────────────────────┘
                 ↓
    ┌────────────────────────────────────────┐
    │ 4. ¿Email existe?                      │
    │    ├─ SÍ: Error 400 "Ya existe"       │
    │    └─ NO: Continuar                   │
    └────────────┬───────────────────────────┘
                 ↓
        ┌───────────────────────┐
        │ 5. Hashear contraseña │
        │    BCrypt con 10      │
        │    rounds            │
        └────────────┬──────────┘
                     ↓
        ┌────────────────────────┐
        │ 6. Crear User entity  │
        │ 7. Asignar roles      │
        │ 8. Guardar en BD      │
        └────────────┬───────────┘
                     ↓
    ┌──────────────────────────────────────────┐
    │ 9. Respuesta: 200 OK                     │
    │    {"message": "User registered..."}     │
    └──────────────────────────────────────────┘

LOGIN:
┌─────────────────────────────────────────────────┐
│ 1. Cliente envía credenciales:                  │
│    POST /api/auth/signin                        │
│    {username, password}                         │
└──────────────────┬──────────────────────────────┘
                   ↓
          [AuthController.authenticateUser()]
                   ↓
    ┌────────────────────────────────────────────┐
    │ 2. Crear UsernamePasswordAuthenticationToken│
    │    (username sin validar aún)               │
    └────────────┬───────────────────────────────┘
                 ↓
    ┌────────────────────────────────────────────┐
    │ 3. AuthenticationManager.authenticate()     │
    │    ├─ DaoAuthenticationProvider ejecuta    │
    │    │  ├─ UserDetailsServiceImpl cargar user │
    │    │  ├─ UserRepository.findByUsername()   │
    │    │  ├─ BCrypt validar contraseña        │
    │    │  └─ Si no coincide: BadCredentials   │
    │    └─ Si OK: Retorna Authentication       │
    └────────────┬───────────────────────────────┘
                 ↓
        ┌──────────────────────────┐
        │ 4. Generar JWT Token     │
        │    JwtUtils.generate...()│
        │    ├─ Payload: username  │
        │    ├─ Expira: 24h        │
        │    └─ Firma: HS256       │
        └────────────┬─────────────┘
                     ↓
        ┌─────────────────────────────┐
        │ 5. Extraer datos del usuario│
        │    └─ roles, id, email     │
        └────────────┬────────────────┘
                     ↓
    ┌──────────────────────────────────────────────┐
    │ 6. Respuesta: 200 OK                         │
    │    {token, type, id, username, email, roles} │
    └──────────────────────────────────────────────┘

VALIDACIÓN (Cada solicitud protegida):
┌─────────────────────────────────────────┐
│ 1. Cliente envía: GET /api/test/user    │
│    Header: Authorization: Bearer <JWT>  │
└──────────────────┬──────────────────────┘
                   ↓
    ┌────────────────────────────────────┐
    │ 2. [AuthTokenFilter intercepta]    │
    │    doFilterInternal() ejecuta       │
    └────────────┬───────────────────────┘
                 ↓
        ┌──────────────────────────┐
        │ 3. Extraer JWT del header│
        │    "Bearer " + token     │
        └────────────┬─────────────┘
                     ↓
    ┌─────────────────────────────────────┐
    │ 4. JwtUtils.validateJwtToken()      │
    │    ├─ Verificar firma (HS256)      │
    │    ├─ Verificar no expirado        │
    │    └─ Si inválido: rechazar        │
    └────────────┬────────────────────────┘
                 ↓
    ┌─────────────────────────────────────┐
    │ 5. authController.isTokenRevoked()  │
    │    ├─ ¿Está en revokedTokens?     │
    │    └─ Si sí: rechazar (401)        │
    └────────────┬────────────────────────┘
                 ↓
        ┌──────────────────────────────┐
        │ 6. Extraer username del JWT  │
        │    JwtUtils.getUserNameFrom..│
        └────────────┬─────────────────┘
                     ↓
    ┌──────────────────────────────────────────┐
    │ 7. UserDetailsServiceImpl.loadUserByUser..│
    │    └─ UserRepository.findByUsername()    │
    └────────────┬───────────────────────────────┘
                 ↓
        ┌─────────────────────────────┐
        │ 8. Crear UsernamePasswordAuth│
        │    Token con authorities    │
        │    (roles del usuario)      │
        └────────────┬────────────────┘
                     ↓
    ┌────────────────────────────────────────┐
    │ 9. Establecer en SecurityContextHolder  │
    │    ├─ Disponible en thread local       │
    │    └─ Accesible desde cualquier lugar  │
    └────────────┬───────────────────────────┘
                 ↓
    ┌────────────────────────────────────────┐
    │ 10. Pasa a siguiente filtro/controlador │
    └────────────┬───────────────────────────┘
                 ↓
    ┌────────────────────────────────────────┐
    │ 11. [TestController.userAccess()]      │
    │    @PreAuthorize valida roles          │
    │    ├─ Obtiene Authentication actual    │
    │    ├─ Extrae authorities (roles)       │
    │    ├─ Verifica: hasRole('USER')?       │
    │    └─ Si SÍ: ejecutar método           │
    └────────────┬───────────────────────────┘
                 ↓
    ┌──────────────────────────────────────────┐
    │ 12. Respuesta: 200 OK                    │
    │     "User Content."                      │
    └──────────────────────────────────────────┘

ERROR: Rol insuficiente
    ┌────────────────────────────────────────┐
    │ @PreAuthorize verifica ANTES           │
    │ ├─ Usuario tiene ROLE_USER             │
    │ ├─ Necesita ROLE_ADMIN                 │
    │ └─ AccessDeniedException lanzada       │
    └────────────┬───────────────────────────┘
                 ↓
    ┌────────────────────────────────────────┐
    │ ExceptionHandler captura               │
    │ └─ Respuesta: 403 Forbidden            │
    └────────────────────────────────────────┘
```

---

## 📊 Estructura de Base de Datos

```sql
-- Tabla de Usuarios
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL
);

-- Tabla de Roles
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) UNIQUE NOT NULL  -- Enum: ROLE_USER, ROLE_MODERATOR, ROLE_ADMIN
);

-- Tabla de Relación User-Roles (ManyToMany)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Ejemplo de datos iniciales
INSERT INTO roles (name) VALUES
('ROLE_USER'),
('ROLE_MODERATOR'),
('ROLE_ADMIN');

-- Ejemplo: Usuario john con roles USER y MODERATOR
INSERT INTO users (username, email, password) VALUES
('john', 'john@example.com', '$2a$10$...(hasheada)');

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),  -- john tiene ROLE_USER
(1, 2);  -- john tiene ROLE_MODERATOR
```

---

## 🔑 Propiedades de Configuración

**Archivo: `application.properties`**

```properties
# JWT Configuration
dam2.app.jwtSecret=myJwtSecretKeyThatIsAtLeast32CharactersLong
dam2.app.jwtExpirationMs=86400000

# Database Configuration (ejemplo H2)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true

# Logging
logging.level.root=INFO
logging.level.com.bezkoder.springjwt=DEBUG
```

---

## 🎓 Conclusión

Este proyecto implementa un sistema de autenticación robusto basado en JWT con:

✅ **Seguridad:**

- Contraseñas hasheadas con BCrypt
- Tokens JWT con firma HMAC-SHA256
- Validación en cada solicitud
- Control de acceso basado en roles (RBAC)

✅ **Arquitectura limpia:**

- Separación de responsabilidades
- DTOs para input/output
- Repositorios para acceso a datos
- Servicios de seguridad desacoplados

✅ **Escalabilidad:**

- Stateless (sin sesiones servidor)
- Fácil de integrar con múltiples servidores
- Soporte para múltiples roles

✅ **Usabilidad:**

- Endpoints claros y bien documentados
- Mensajes de error descriptivos
- CORS habilitado para clientes web
