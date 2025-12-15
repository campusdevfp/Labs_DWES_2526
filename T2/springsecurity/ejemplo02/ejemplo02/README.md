

# 🔐 Spring Security con Sesiones HTTP - Guía Completa


## 📋 Índice

1. [Configuración del Proyecto](#-configuración-del-proyecto)
2. [Usuarios de Prueba](#-usuarios-de-prueba)
3. [Cómo Funciona la Autenticación](#-cómo-funciona-la-autenticación)
4. [Endpoints Disponibles](#-endpoints-disponibles)
5. [Gestión de Sesiones](#-gestión-de-sesiones)
6. [Ejemplos Prácticos](#-ejemplos-prácticos)
7. [Configuración de Seguridad](#-configuración-de-seguridad)

---

## ⚙️ Configuración del Proyecto

### Tecnologías utilizadas
- **Spring Boot 4.0** con Spring Security
- **H2 Database** (base de datos en memoria)
- **JPA/Hibernate** para persistencia
- **Form Login** para autenticación (con cookies de sesión)

### Estructura de archivos principales

```
src/main/java/app/ejemplo02/
├── config/
│   └── SecurityConfig.java      # Configuración de seguridad
├── controller/
│   ├── TestController.java      # Endpoints de prueba (público/autenticado)
│   ├── SessionController.java   # Gestión de sesiones
│   └── EjemplosSessionController.java  # Ejemplos prácticos
├── models/
│   └── UserEntity.java          # Entidad de usuario
├── repository/
│   └── UserRepository.java      # Repositorio JPA
├── service/
│   └── UserService.java         # Servicio de usuarios
└── user/
    └── DbUserDetailsService.java # Carga usuarios desde BD
```

### Ejecutar el proyecto

```bash
./gradlew bootRun
```

La aplicación estará en: `http://localhost:8080`

---

## 👤 Usuarios de Prueba

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `user` | `password` | ROLE_USER |
| `admin` | `admin123` | ROLE_ADMIN |

---

## 🔄 Cómo Funciona la Autenticación

### Flujo de autenticación con sesión

```
Cliente                    Spring Security              Servidor
  |                              |                          |
  |---(1) Login con user:password--->                       |
  |                              |                          |
  |                    (2) Valida credenciales en BD        |
  |                              |                          |
  |                    (3) Crea objeto Authentication       |
  |                              |                          |
  |                    (4) Guarda en SecurityContext        |
  |                              |                          |
  |                    (5) Almacena en HttpSession -------->|
  |                              |                          |
  |<--(6) Responde con Cookie: JSESSIONID=abc123-----------|
  |                              |                          |
  |---(7) Siguientes peticiones envían JSESSIONID--------->|
  |                              |                          |
  |                    (8) Recupera SecurityContext <-------|
  |                              |                          |
  |<--(9) Usuario ya autenticado (sin pedir credenciales)--|
```

### ¿Qué guarda Spring Security en la sesión?

Cuando te autenticas, Spring Security guarda un objeto `SecurityContext` en la sesión bajo la clave `SPRING_SECURITY_CONTEXT`. Este contiene:

- **Authentication**: Objeto con tu información de usuario
  - `name`: Tu username
  - `authorities`: Tus roles (ROLE_USER, ROLE_ADMIN)
  - `authenticated`: Si estás autenticado (true/false)
  - `credentials`: Se elimina después del login por seguridad

---

## 🍪 Sesiones y Cookies: Conceptos Fundamentales

### ¿Qué es una Sesión HTTP?

Una **sesión HTTP** es un mecanismo que permite al servidor recordar información sobre un usuario a través de múltiples peticiones HTTP. HTTP es un protocolo **stateless** (sin estado), lo que significa que cada petición es independiente y el servidor no recuerda automáticamente al usuario entre peticiones.

**Problema sin sesiones:**
- Cada petición requiere autenticación (ej. enviar usuario/contraseña)
- No se puede mantener estado (carrito de compras, preferencias, etc.)
- Experiencia de usuario pobre

**Solución con sesiones:**
- El servidor asigna un ID único a cada usuario
- Este ID se envía en cada petición (vía cookie)
- El servidor recupera el estado del usuario usando ese ID

### ¿Qué es una Cookie?

Una **cookie** es un pequeño archivo de texto que el servidor envía al navegador del cliente. Contiene pares clave-valor y se almacena en el navegador.

**Estructura típica de una cookie:**
```
Nombre: JSESSIONID
Valor: ABC123DEF456GHI789
Dominio: localhost
Path: /
Expires/Max-Age: Session (o fecha específica)
Secure: false (true en HTTPS)
HttpOnly: true (no accesible desde JavaScript)
```

### Cómo Funcionan las Sesiones con Cookies

```
1. Usuario hace login
   Cliente → Servidor: POST /login (username/password)

2. Servidor valida credenciales
   - Crea HttpSession
   - Guarda datos en sesión
   - Envía cookie JSESSIONID

3. Cliente recibe cookie
   - Navegador la almacena automáticamente

4. Peticiones subsiguientes
   Cliente → Servidor: GET /api/data + Cookie: JSESSIONID=ABC123...

5. Servidor recupera sesión
   - Usa JSESSIONID para encontrar HttpSession
   - Accede a datos guardados
```

### Tipos de Cookies

| Tipo | Descripción | Ejemplo |
|------|-------------|---------|
| **Session Cookies** | Se eliminan al cerrar navegador | JSESSIONID |
| **Persistent Cookies** | Tienen fecha de expiración | "Recordarme" |
| **Secure Cookies** | Solo se envían por HTTPS | Para producción |
| **HttpOnly Cookies** | No accesibles desde JavaScript | JSESSIONID |
| **SameSite Cookies** | Controlan envío cross-site | Lax, Strict, None |

### Utilidades de las Sesiones y Cookies

#### 1. **Autenticación y Autorización**
- Mantener usuario logueado entre peticiones
- Recordar roles y permisos
- Evitar re-autenticación constante

#### 2. **Estado de la Aplicación**
- Carrito de compras
- Preferencias de usuario (idioma, tema)
- Formularios multi-paso (wizard)
- Historial de navegación

#### 3. **Personalización**
- Contenido personalizado
- Recomendaciones basadas en comportamiento
- Configuraciones guardadas

#### 4. **Seguridad**
- CSRF tokens
- Rate limiting por sesión
- Tracking de actividad sospechosa

#### 5. **Analytics y Tracking**
- Seguimiento de usuario
- Métricas de uso
- A/B testing

### Ventajas de las Sesiones

✅ **Fácil de implementar**: Spring Boot lo maneja automáticamente  
✅ **Estado compartido**: Datos accesibles en toda la aplicación  
✅ **Seguro**: Datos en servidor, no en cliente  
✅ **Escalable**: Para aplicaciones pequeñas/medianas  

### Desventajas de las Sesiones

❌ **Consumo de memoria**: Cada sesión ocupa RAM en servidor  
❌ **Escalabilidad**: Problemas con múltiples servidores (sticky sessions o Redis)  
❌ **Timeout**: Sesiones expiran, usuarios pierden estado  
❌ **No stateless**: Dificulta APIs para móviles/SPAs  

### Alternativas a las Sesiones

#### JWT (JSON Web Tokens)
- **Stateless**: Todo el estado en el token
- **Escalable**: No ocupa memoria en servidor
- **Cross-platform**: Funciona en móviles/web
- **Desventaja**: No se puede invalidar fácilmente

#### OAuth 2.0 / OpenID Connect
- **Estándar**: Para autenticación externa
- **Seguro**: Tokens de corta duración
- **Complejo**: Requiere proveedor de identidad

### Configuración de Sesiones en Spring Boot

#### Timeout de Sesión
```properties
# application.properties
server.servlet.session.timeout=30m  # 30 minutos
```

#### Cookies Seguras
```java
// SecurityConfig.java
.sessionManagement(session -> session
    .sessionFixation().migrateSession()
    .cookie().secure(true)  // Solo HTTPS
    .httpOnly(true)         // No JavaScript
    .sameSite("Lax")        // Control cross-site
)
```

#### Persistencia de Sesión (Redis)
```properties
# Para producción con múltiples servidores
spring.session.store-type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

### Buenas Prácticas

#### Para Cookies
- **Usa HttpOnly**: Previene ataques XSS
- **Secure en producción**: Solo HTTPS
- **SameSite apropiado**: Evita CSRF
- **Tamaño limitado**: < 4KB por cookie

#### Para Sesiones
- **Timeout razonable**: 30min-2h según caso
- **Limpieza**: Invalida sesiones en logout
- **Monitoreo**: Revisa uso de memoria
- **Backup**: Persistencia para recuperación

#### Para Seguridad
- **Regenera ID**: Después de login (session fixation)
- **Valida origen**: CORS configurado
- **Auditoría**: Log de accesos importantes

### Ejemplos Prácticos de Uso

#### Carrito de Compras
```java
@PostMapping("/carrito/agregar")
public String agregarProducto(HttpSession session, String producto) {
    List<String> carrito = (List<String>) session.getAttribute("carrito");
    if (carrito == null) carrito = new ArrayList<>();
    carrito.add(producto);
    session.setAttribute("carrito", carrito);
    return "Producto agregado";
}
```

#### Preferencias de Usuario
```java
@PostMapping("/preferencias/idioma")
public String cambiarIdioma(HttpSession session, String idioma) {
    session.setAttribute("idioma", idioma);
    return "Idioma guardado";
}
```

#### Wizard Multi-Paso
```java
@PostMapping("/registro/paso1")
public String paso1(HttpSession session, String nombre, String email) {
    session.setAttribute("registro.nombre", nombre);
    session.setAttribute("registro.email", email);
    return "Paso 1 completado";
}
```

### Depuración de Sesiones

#### Ver Cookies en Navegador
- Chrome: DevTools → Application → Cookies
- Firefox: DevTools → Storage → Cookies

#### Ver Sesión en Servidor
```java
@GetMapping("/debug/session")
public Map<String, Object> debugSession(HttpSession session) {
    Map<String, Object> info = new HashMap<>();
    info.put("id", session.getId());
    info.put("creationTime", new Date(session.getCreationTime()));
    info.put("lastAccessedTime", new Date(session.getLastAccessedTime()));
    info.put("maxInactiveInterval", session.getMaxInactiveInterval());
    
    // Atributos de la sesión
    Enumeration<String> attrs = session.getAttributeNames();
    Map<String, Object> attributes = new HashMap<>();
    while (attrs.hasMoreElements()) {
        String key = attrs.nextElement();
        attributes.put(key, session.getAttribute(key));
    }
    info.put("attributes", attributes);
## 📡 Endpoints Disponibles

### Endpoints de Prueba (`/test/*`)

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| GET | `/test/public` | Público | No requiere autenticación |
| GET | `/test/authenticated` | Autenticado | Requiere login |
| GET | `/test/me` | Autenticado | Información del usuario actual |

### Endpoints de Sesión (`/session/*`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/session/info` | Información de la sesión actual |
| GET | `/session/attributes` | Lista todos los atributos de la sesión |
| POST | `/session/attribute?key=x&value=y` | Guarda un atributo en la sesión |
| GET | `/session/attribute/{key}` | Obtiene un atributo específico |
| GET | `/session/timeout` | Tiempo de vida de la sesión |
| POST | `/session/logout` | Cierra la sesión (logout) |

### Endpoints de Ejemplos (`/ejemplos/*`) - Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/ejemplos/carrito/agregar?producto=x&cantidad=n` | Agregar al carrito |
| GET | `/ejemplos/carrito/ver` | Ver carrito |
| DELETE | `/ejemplos/carrito/vaciar` | Vaciar carrito |
| POST | `/ejemplos/preferencias/idioma?idioma=es` | Cambiar idioma |
| POST | `/ejemplos/preferencias/tema?tema=oscuro` | Cambiar tema |
| GET | `/ejemplos/preferencias` | Ver preferencias |
| POST | `/ejemplos/historial/agregar?pagina=/url` | Agregar al historial |
| GET | `/ejemplos/historial` | Ver historial |
| POST | `/ejemplos/registro/paso1?nombre=x&email=y` | Wizard paso 1 |
| POST | `/ejemplos/registro/paso2?telefono=x&ciudad=y` | Wizard paso 2 |
| POST | `/ejemplos/registro/paso3` | Wizard confirmar |
| GET | `/ejemplos/registro/estado` | Estado del wizard |

---

## 🧪 Gestión de Sesiones

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

## 🔑 Conceptos Clave

| Concepto | Descripción |
|----------|-------------|
| **JSESSIONID** | Cookie que identifica tu sesión en el servidor |
| **HttpSession** | Objeto Java donde se guardan datos de la sesión |
| **SecurityContext** | Contiene el objeto Authentication (usuario autenticado) |
| **Authentication** | Información del usuario: username, roles, si está autenticado |
| **session.invalidate()** | Destruye la sesión (logout) |
| **Timeout** | Tiempo de inactividad antes de que expire (por defecto 30 min) |

---

## ⚠️ Consideraciones

1. **Las sesiones ocupan memoria**: En producción con muchos usuarios, considera usar Redis
2. **HTTPS en producción**: Las cookies deben ser seguras (Secure flag)
3. **Timeout apropiado**: Ajusta según tu caso de uso
4. **No guardes datos sensibles**: Las contraseñas nunca se guardan en sesión
5. **Sesiones vs JWT**: Para APIs móviles/SPAs, considera tokens JWT (stateless)

---

## 🤝 Integración con Angular

Sí, este backend se puede usar perfectamente con un frontend de Angular. A continuación, ejemplos claros de cómo integrarlo.

### 1. Configuración en Spring Security (CORS)

Primero, habilita CORS en `SecurityConfig.java` para permitir peticiones desde Angular (que corre en `http://localhost:4200`):

```java
.cors(cors -> cors.configurationSource(request -> {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:4200")); // Puerto de Angular
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true); // Crucial para cookies de sesión
    return config;
}))
```

### 2. Servicio Angular para Autenticación

Crea un servicio `auth.service.ts` en Angular:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  // Login con Form Login (envía username/password como form data)
  login(username: string, password: string): Observable<any> {
    const body = new URLSearchParams();
    body.set('username', username);
    body.set('password', password);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    return this.http.post(`${this.apiUrl}/login`, body.toString(), {
      headers,
      withCredentials: true  // Envía/recibe cookies
    });
  }

  // Verificar si está autenticado
  getUserInfo(): Observable<any> {
    return this.http.get(`${this.apiUrl}/test/me`, {
      withCredentials: true
    });
  }

  // Logout
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/session/logout`, {}, {
      withCredentials: true
    });
  }
}
```

### 3. Servicio para el Carrito de Compras

```typescript
@Injectable({
  providedIn: 'root'
})
export class CarritoService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  agregarProducto(producto: string, cantidad: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/ejemplos/carrito/agregar`, null, {
      params: { producto, cantidad: cantidad.toString() },
      withCredentials: true
    });
  }

  verCarrito(): Observable<any> {
    return this.http.get(`${this.apiUrl}/ejemplos/carrito/ver`, {
      withCredentials: true
    });
  }

  vaciarCarrito(): Observable<any> {
    return this.http.delete(`${this.apiUrl}/ejemplos/carrito/vaciar`, {
      withCredentials: true
    });
  }
}
```

### 4. Componente de Login

```typescript
import { Component } from '@angular/core';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-login',
  template: `
    <div>
      <h2>Login</h2>
      <input [(ngModel)]="username" placeholder="Usuario" />
      <input [(ngModel)]="password" type="password" placeholder="Contraseña" />
      <button (click)="login()">Login</button>
      <p *ngIf="error">{{ error }}</p>
    </div>
  `
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  constructor(private authService: AuthService) { }

  login() {
    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        console.log('Login exitoso', response);
        // Redirigir o actualizar estado
      },
      error: (err) => {
        this.error = 'Credenciales incorrectas';
      }
    });
  }
}
```

### 5. Componente del Carrito

```typescript
import { Component, OnInit } from '@angular/core';
import { CarritoService } from './carrito.service';

@Component({
  selector: 'app-carrito',
  template: `
    <div>
      <h2>Carrito de Compras</h2>
      <input [(ngModel)]="producto" placeholder="Producto" />
      <input [(ngModel)]="cantidad" type="number" placeholder="Cantidad" />
      <button (click)="agregar()">Agregar</button>
      
      <h3>Carrito Actual</h3>
      <pre>{{ carrito | json }}</pre>
      <button (click)="vaciar()">Vaciar Carrito</button>
    </div>
  `
})
export class CarritoComponent implements OnInit {
  producto = '';
  cantidad = 1;
  carrito: any = {};

  constructor(private carritoService: CarritoService) { }

  ngOnInit() {
    this.verCarrito();
  }

  agregar() {
    this.carritoService.agregarProducto(this.producto, this.cantidad).subscribe({
      next: (response) => {
        console.log('Producto agregado', response);
        this.verCarrito(); // Actualizar vista
      }
    });
  }

  verCarrito() {
    this.carritoService.verCarrito().subscribe({
      next: (data) => this.carrito = data
    });
  }

  vaciar() {
    this.carritoService.vaciarCarrito().subscribe({
      next: () => this.verCarrito()
    });
  }
}
```

### Notas Importantes

- **`withCredentials: true`**: Es obligatorio para que Angular envíe las cookies de sesión.
- **Orden de ejecución**: Primero haz login, luego las demás peticiones usarán la sesión automáticamente.
- **Manejo de errores**: Implementa interceptores para redirigir a login si la sesión expira (401).
- **Producción**: Cambia las URLs y configura HTTPS.

Con estos ejemplos, puedes integrar completamente Angular con este backend Spring Security.
