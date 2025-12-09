#  Spring Security con H2 - Autenticación Básica

##  Índice
1. [Introducción](#introducción)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Conceptos Clave](#conceptos-clave)
4. [Explicación del Código](#explicación-del-código)
5. [Cómo Probar la Aplicación](#cómo-probar-la-aplicación)
6. [🎯 RETO: Tu Turno](#-reto-tu-turno)

---

## Introducción

Este proyecto demuestra cómo implementar **autenticación y autorización** en Spring Boot usando:

- **Spring Security** - Framework de seguridad
- **H2 Database** - Base de datos en memoria
- **JPA/Hibernate** - Persistencia de datos
- **BCrypt** - Encriptación de contraseñas

### ¿Qué aprenderás?

✅ Configurar Spring Security desde cero  
✅ Proteger endpoints con roles (USER, ADMIN)  
✅ Almacenar usuarios en base de datos  
✅ Encriptar contraseñas de forma segura  
✅ Usar la consola H2 para ver los datos  

---

## Estructura del Proyecto

```
src/main/java/com/example/demo/
├── DemoApplication.java          # Clase principal
├── config/
│   ├── SecurityConfig.java       # Configuración de seguridad
│   └── DataInitializer.java      # Crea usuarios de prueba
├── controller/
│   └── DemoController.java       # Endpoints REST
└── user/
    ├── UserEntity.java           # Entidad JPA (tabla users)
    ├── UserRepository.java       # Repositorio JPA
    └── DbUserDetailsService.java # Conecta Security con la BD
```

---

## Conceptos Clave

###  Autenticación vs Autorización

| Concepto | Pregunta que responde | Ejemplo |
|----------|----------------------|---------|
| **Autenticación** | ¿Quién eres? | Login con usuario/contraseña |
| **Autorización** | ¿Qué puedes hacer? | Solo ADMIN puede borrar usuarios |

### 🛡️ Flujo de Spring Security

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   Usuario       │────▶│  SecurityFilter  │────▶│   Controlador   │
│ (user:password) │     │  Chain           │     │   (endpoint)    │
└─────────────────┘     └──────────────────┘     └─────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │ UserDetailsService│
                    │ (busca en BD)     │
                    └──────────────────┘
```

1. Usuario envía credenciales
2. Spring Security intercepta la petición
3. Llama a `UserDetailsService.loadUserByUsername()`
4. Compara contraseña enviada con la almacenada (BCrypt)
5. Si es correcta → permite acceso según el rol

###  BCrypt: Encriptación de Contraseñas

```
Contraseña: "password"
    ↓ BCrypt
Hash: "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"
```

**Características de BCrypt:**
- ❌ No se puede "desencriptar" (es un hash, no cifrado)
- ✅ Cada vez genera un hash diferente (usa "salt")
- ✅ Es lento a propósito (dificulta ataques de fuerza bruta)

---

## Explicación del Código

### SecurityConfig.java

```java
@Configuration          // Esta clase contiene configuración de Spring
@EnableWebSecurity      // Activa Spring Security
@EnableMethodSecurity   // Permite usar @PreAuthorize en controladores
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())  // Desactiva CSRF (solo desarrollo)
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/publico/**").permitAll()    // Sin login
                .requestMatchers("/h2-console/**").permitAll() // Consola H2
                .anyRequest().authenticated()                   // Resto: login
            )
            
            .httpBasic(Customizer.withDefaults()); // Auth tipo Basic
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encriptador de contraseñas
    }
}
```

**Patrones de URL:**
| Patrón | Significado | Ejemplo |
|--------|-------------|---------|
| `/publico/*` | Un nivel | `/publico/saludo` ✅, `/publico/a/b` ❌ |
| `/publico/**` | Cualquier nivel | `/publico/saludo` ✅, `/publico/a/b` ✅ |

### UserEntity.java [no implementado por ahora]

Esta Entidad no la vamos a implementar en este ejemplo, pero es importante entender su estructura.

```java
@Entity                    // Es una entidad JPA (se guarda en BD)
@Table(name = "users")     // Nombre de la tabla
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;       // Clave primaria autoincremental

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // ¡Siempre encriptada!

    @Column(nullable = false)
    private String role;     // ROLE_USER o ROLE_ADMIN
}
```

### 3 DbUserDetailsService.java [no implementado por ahora]

- Este servicio no lo vamos a usar por ahora, pero lo usaremos después pues es el 
encargado de verificar si existe el usuario en la BD.

```java
@Service
public class DbUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        // 1. Buscar usuario en la BD
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("No encontrado"));

        // 2. Convertir a UserDetails (lo que Spring Security entiende)
        return new User(
            user.getUsername(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
```

### 4️⃣ DemoController.java

```java
@RestController
public class DemoController {

    @GetMapping("/publico/saludo")
    public String publico() {
        return "Endpoint público";  // Sin autenticación
    }

    @GetMapping("/api/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String soloUser() {
        return "Solo usuarios autenticados";
    }

    @GetMapping("/api/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String soloAdmin() {
        return "Solo administradores";
    }
}
```

**Importante:** `hasRole('ADMIN')` busca `ROLE_ADMIN` en la BD (añade el prefijo automáticamente).

---

## Cómo Probar la Aplicación

### 1. Arrancar la aplicación
Ejecuta `DemoApplication.java` desde IntelliJ (botón ▶️)

### 2. Probar endpoints

**Endpoint público (sin credenciales):**
```bash
curl http://localhost:8080/publico/saludo
```

**Endpoint con autenticación:**
```bash
# Usuario normal
curl -u user:password http://localhost:8080/api/user

# Administrador
curl -u admin:admin123 http://localhost:8080/api/admin

# Esto falla (user no es admin):
curl -u user:password http://localhost:8080/api/admin  # → 403 Forbidden
```

### 3. Ver la base de datos H2

1. Abre: `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:testdb`
3. User: `sa` / Password: (vacío)
4. Ejecuta: `SELECT * FROM USERS;`

### Usuarios de prueba

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| user | password | ROLE_USER |
| admin | admin123 | ROLE_ADMIN |

---

## 🎯 RETO: Tu Turno

### Objetivo
Añadir un nuevo rol **ROLE_MODERATOR** a la aplicación y crear 2 endpoints nuevos.

### Requisitos

#### 1. Nuevos Roles
- **ROLE_USER** - Solo lectura
- **ROLE_MODERATOR** - Lectura y edición (rol nuevo)
- **ROLE_ADMIN** - Todo

#### 2. Nuevos Endpoints a crear

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| GET | `/api/contenido` | USER, MODERATOR, ADMIN | Ver contenido |
| PUT | `/api/contenido/editar` | MODERATOR, ADMIN | Editar contenido |

**Ejemplo:**
```java
@GetMapping("/api/contenido")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
public String verContenido() {
    return "Contenido visible para todos los autenticados";
}

@PutMapping("/api/contenido/editar")
@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
public String editarContenido() {
    return "Solo moderadores y admins pueden editar";
}
```

#### 3. Nuevo usuario de prueba

Añade a `data.sql`:
```sql
INSERT INTO USERS (USERNAME, PASSWORD, ENABLED) VALUES
    ('moderador', '{bcrypt}$2a$10$...[encriptado]...', true);

INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES
    ('moderador', 'ROLE_MODERATOR');
```

#### 4. Pruebas esperadas

| Usuario | Endpoint | Resultado |
|---------|----------|-----------|
| user | `/api/contenido` | ✅ 200 OK |
| user | `/api/contenido/editar` | ❌ 403 Forbidden |
| moderador | `/api/contenido` | ✅ 200 OK |
| moderador | `/api/contenido/editar` | ✅ 200 OK |
| admin | `/api/contenido/editar` | ✅ 200 OK |

### Pistas

1. Modifica `DemoController.java` y añade los 2 nuevos endpoints.

2. Genera un hash BCrypt para "moderador123" (usa un generador online o código Java):
   ```java
   new BCryptPasswordEncoder().encode("moderador123")
   ```

3. Prueba con curl:
   ```bash
   # Debería fallar
   curl -u user:password http://localhost:8080/api/contenido/editar
   
   # Debería funcionar
   curl -u moderador:moderador123 http://localhost:8080/api/contenido/editar
   ```



---

## 📖 Recursos Adicionales

- [Documentación oficial Spring Security](https://docs.spring.io/spring-security/reference/)
- [Guía de BCrypt](https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt)
- [H2 Database](https://www.h2database.com/html/main.html)

---
