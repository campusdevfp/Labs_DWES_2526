### 1) Proyecto en **Gradle** (Spring Boot + Spring Security + JWT) con **autenticación y autorización**

A continuación te dejo un “esqueleto” funcional (Java 17 + Spring Boot 3.x) usando **Gradle (Groovy)**, con:

- **Autenticación**: `POST /api/auth/register` y `POST /api/auth/login`
- **Autorización**:
    - `GET /api/user` → requiere `ROLE_USER` o `ROLE_ADMIN`
    - `GET /api/admin` → requiere `ROLE_ADMIN`
- **JWT stateless** (sin sesión de servidor)
- Persistencia simple con **JPA + H2**
- Roles en base de datos y en el token

#### `build.gradle`
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.2'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.example'
version = '1.0.0'
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'

    // JWT (JJWT)
    implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'

    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

#### `src/main/resources/application.yml`
> Importante: el `jwt.secret` debe ser **Base64** (para HS256 conviene mínimo 256 bits). Aquí te dejo uno de ejemplo.

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console

jwt:
  secret: "2v8y1yGQ7pD7j1fQvZqk1kV2yQm1zQx0m2Qv7y0p9nA="
  accessTokenExpirationMs: 3600000
```

#### Entidades

`Role.java`
```java
package com.example.demo.user;

public enum Role {
    USER, ADMIN
}
```

`AppUser.java`
```java
package com.example.demo.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
```

`UserRepository.java`
```java
package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

#### DTOs

`RegisterRequest.java`
```java
package com.example.demo.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 30) String username,
        @NotBlank @Size(min = 6, max = 72) String password
) {}
```

`LoginRequest.java`
```java
package com.example.demo.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}
```

`AuthResponse.java`
```java
package com.example.demo.auth;

public record AuthResponse(
        String tokenType,
        String accessToken,
        String username,
        String role
) {}
```

#### JWT Service

`JwtService.java`
```java
package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {

    private final SecretKey key;
    private final long accessTokenExpirationMs;

    public JwtService(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.accessTokenExpirationMs}") long accessTokenExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public String generateAccessToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key) // HS256 por defecto con SecretKey
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = parseClaims(token);
        boolean usernameOk = claims.getSubject().equals(userDetails.getUsername());
        boolean notExpired = claims.getExpiration().after(new Date());
        return usernameOk && notExpired;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
```

#### UserDetailsService + Filter

`CustomUserDetailsService.java`
```java
package com.example.demo.security;

import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
```

`JwtAuthFilter.java`
```java
package com.example.demo.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails ud = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, ud)) {
                var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

#### Config de Spring Security (autenticación + autorización)

`SecurityConfig.java`
```java
package com.example.demo.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // Eliminamos la línea .authenticationProvider(...)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // Spring Security detectará automáticamente este Bean y el de UserDetailsService
    // para configurar el DaoAuthenticationProvider interno.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

#### AuthService + Controller

`AuthService.java`
```java
package com.example.demo.auth;

import com.example.demo.security.JwtService;
import com.example.demo.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        if (repo.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        var user = AppUser.builder()
                .username(req.username())
                .passwordHash(encoder.encode(req.password()))
                .role(Role.USER)
                .build();

        repo.save(user);

        var ud = userDetailsService.loadUserByUsername(user.getUsername());
        var token = jwtService.generateAccessToken(ud);

        return new AuthResponse("Bearer", token, user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        var user = repo.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var ud = userDetailsService.loadUserByUsername(user.getUsername());
        var token = jwtService.generateAccessToken(ud);

        return new AuthResponse("Bearer", token, user.getUsername(), user.getRole().name());
    }
}
```

`AuthController.java`
```java
package com.example.demo.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(service.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(service.login(req));
    }
}
```

`DemoController.java`
```java
package com.example.demo.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> user(Principal principal) {
        return ResponseEntity.ok("Hello " + principal.getName() + " (USER content)");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> admin(Principal principal) {
        return ResponseEntity.ok("Hello " + principal.getName() + " (ADMIN content)");
    }
}
```

#### (Opcional) Crear un ADMIN al arrancar
`DataSeeder.java`
```java
package com.example.demo.user;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (!repo.existsByUsername("admin")) {
            repo.save(AppUser.builder()
                    .username("admin")
                    .passwordHash(encoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build());
        }
    }
}
```

#### Probar con `curl`
1) Register:
```bash
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"u1","password":"password123"}'
```

2) Login:
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"u1","password":"password123"}' | jq -r .accessToken)
```

3) Endpoint protegido:
```bash
curl -s http://localhost:8080/api/user -H "Authorization: Bearer $TOKEN"
```

---

### 2) Qué significa “autenticación” y “autorización” en este proyecto

- **Autenticación (AuthN)**: comprobar *quién eres*.
    - En `/api/auth/login`, Spring Security valida credenciales con `AuthenticationManager` + `DaoAuthenticationProvider` + `UserDetailsService`.
    - Si es correcto, generamos un **JWT** y lo devolvemos.

- **Autorización (AuthZ)**: comprobar *qué puedes hacer*.
    - En `SecurityFilterChain` restringimos rutas: `/api/admin/**` requiere `ADMIN`.
    - En `@PreAuthorize(...)` reforzamos por método (muy recomendado).
    - El rol viaja como autoridad `ROLE_*` y Spring la compara con lo requerido.

---

### 3) Explicación detallada: JWT, tokens, OAuth2 y Spring Security

#### 3.1 Tokens (concepto general)
Un **token** es una “credencial” que un cliente presenta para acceder a recursos.

Hay dos familias típicas:

1) **Opaque token (opaco)**
    - Es un identificador sin significado para el cliente.
    - El servidor debe **consultar** (en BD/cache/Authorization Server) para validar y obtener permisos.
    - Ventaja: revocación fácil (borras el token del store).
    - Desventaja: cada request suele implicar lookup (aunque se cachea).

2) **JWT (auto-contenido)**
    - El token **contiene** información (claims) y una **firma** que permite verificar integridad.
    - Ventaja: validación local, muy escalable.
    - Desventaja: revocación “instantánea” es difícil (si ya fue emitido y aún no expira).

En APIs modernas, el patrón común es:
- **Access Token** (corto, minutos/1h) para llamadas a API
- **Refresh Token** (más largo) para obtener nuevos access tokens (idealmente con rotación y revocación)

#### 3.2 JWT (JSON Web Token)
Un JWT es un string con 3 partes Base64URL separadas por puntos:

- `header.payload.signature`

**Header**: tipo (`JWT`) y algoritmo (ej. `HS256`, `RS256`).  
**Payload**: claims (datos).  
**Signature**: firma del header+payload con una clave.

Ejemplo de claims típicos:
- `sub`: subject (usuario)
- `iat`: issued at
- `exp`: expiration
- `iss` / `aud`: emisor / audiencia (muy útiles en entornos reales)
- claims custom: `roles`, `tenantId`, etc.

**Firma ≠ cifrado**: JWT normalmente está **firmado**, no cifrado. Cualquiera que lo tenga puede leer el payload (aunque no modificarlo sin invalidar la firma). Si necesitas confidencialidad, hay que usar cifrado (JWE) o no meter datos sensibles.

**Algoritmos**:
- **HS256 (HMAC)**: simétrico, misma clave para firmar/verificar. Fácil, pero debes proteger muy bien el secreto.
- **RS256/ES256 (asimétrico)**: clave privada firma, pública verifica. Ideal cuando hay múltiples servicios verificando tokens sin compartir el secreto.

**Caducidad (`exp`)**: fundamental para limitar daño si se filtra un token.

**Revocación**:
- JWT no se “invalida” solo; estrategias:
    - Access token muy corto + refresh token revocable
    - Lista negra (denylist) en cache (pierdes parte del “stateless”)
    - “Token version” en BD y claim `ver` en JWT (si cambia, invalidas los viejos)

#### 3.3 OAuth2 (y OpenID Connect)
**OAuth2** es un **framework de autorización**: define cómo un cliente obtiene un *access token* para acceder a un recurso en nombre de un usuario o de sí mismo.

Roles en OAuth2:
- **Resource Owner**: el usuario
- **Client**: la app (web/mobile/backend)
- **Authorization Server**: emite tokens (ej. Keycloak, Auth0, Okta, etc.)
- **Resource Server**: la API que valida tokens

Flujos comunes hoy:
- **Authorization Code + PKCE**: recomendado para SPAs y móviles.
- **Client Credentials**: máquina a máquina (sin usuario).

**OpenID Connect (OIDC)** es una capa sobre OAuth2 para **autenticación** (login). Agrega `id_token` y endpoints/userinfo.  
En resumen:
- OAuth2 → “¿esta app puede acceder a X?”
- OIDC → “¿quién es el usuario?” (identidad)

Relación con JWT:
- Un access token **puede** ser JWT o opaco (depende del Authorization Server).
- Un `id_token` de OIDC suele ser JWT.

#### 3.4 Spring Security (cómo encaja todo)
Spring Security es un framework basado en **filtros** (Filter Chain). Conceptos clave:

- **SecurityFilterChain**: define reglas de seguridad (qué rutas requieren qué).
- **Authentication**: representa al usuario autenticado (principal + authorities).
- **SecurityContext**: almacena el `Authentication` actual (por request).
- **AuthenticationManager / AuthenticationProvider**: autenticación (validar credenciales).
- **Authorization**: decidir si un `Authentication` tiene permisos para una acción.

En JWT stateless:
1) El cliente manda `Authorization: Bearer <token>`
2) Un filtro (`OncePerRequestFilter`) extrae el token
3) Se valida firma y expiración
4) Se crea un `Authentication` con `authorities`
5) Spring usa esas authorities para autorizar rutas/métodos

---

### Siguiente paso (para ajustarlo a lo que necesitas)
Para dejarlo exactamente como lo quieres, dime 3 cosas:

1) ¿Gradle **Groovy** (como arriba) o **Kotlin DSL** (`build.gradle.kts`)?
2) ¿Quieres **refresh token** (recomendado en producción) y logout con revocación?
3) ¿JWT “propio” (como este ejemplo) o quieres hacerlo con **OAuth2/OIDC** (Keycloak/Auth0) y que tu API sea **Resource Server**?