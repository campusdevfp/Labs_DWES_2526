# 🔐 Spring Security con Sesiones HTTP - Guía Completa

### Flujo de autenticación con sesión

## 🚦 Endpoints Disponibles
  |                              |                          |
| Método | Endpoint                              | Descripción                        | Autenticación |
|--------|---------------------------------------|------------------------------------|---------------|
| POST   | `/api/auth/login`                     | Login con usuario y contraseña     | No            |
| GET    | `/api/auth/me`                        | Info del usuario autenticado       | Sí            |
| POST   | `/ejemplos/carrito/agregar`           | Añadir producto al carrito         | Sí            |
| GET    | `/ejemplos/carrito/ver`               | Ver el carrito actual              | Sí            |
| DELETE | `/ejemplos/carrito/vaciar`            | Vaciar el carrito                  | Sí            |
| GET    | `/test/public`                        | Endpoint público                   | No            |
| GET    | `/test/private`                       | Endpoint privado                   | Sí            |
```
---

## 🛒 Gestión de Carrito y Sesiones
- **Authentication**: Objeto con tu información de usuario
- El carrito de compras se almacena en la sesión HTTP del usuario.
- Cada usuario autenticado tiene su propio carrito.
- Si la sesión expira o se cierra, el carrito se pierde.
### ¿Se puede configurar la expiración de la cookie de sesión?
Sí, en Spring Boot puedes configurar el tiempo de expiración de la sesión en `application.properties`:

## 💻 Ejemplos Prácticos (curl, Postman, Angular)
server.servlet.session.timeout=30m
### Usando curl (simulación de navegador con cookies)

### Depuración de Sesiones
# 1. Login y guarda la cookie de sesión
curl -c cookies.txt -X POST -H "Content-Type: application/json" -d '{"username":"user","password":"password"}' http://localhost:8080/api/auth/login
- Chrome: DevTools → Application → Cookies
# 2. Añadir producto al carrito (usando la cookie de sesión)
curl -b cookies.txt -X POST "http://localhost:8080/ejemplos/carrito/agregar?producto=Laptop&cantidad=1"
#### Ver Sesión en Servidor
# 3. Ver el carrito
@GetMapping("/debug/session")
public Map<String, Object> debugSession(HttpSession session) {
# 4. Vaciar el carrito
    info.put("id", session.getId());
    info.put("creationTime", new Date(session.getCreationTime()));
    info.put("lastAccessedTime", new Date(session.getLastAccessedTime()));
### Usando Postman
    while (attrs.hasMoreElements()) {
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
### Endpoints de Prueba (`/test/*`)
### Usando Angular (ejemplo básico de integración)
| Método | Endpoint | Acceso | Descripción |
```typescript
// auth.service.ts
login(username: string, password: string) {
  return this.http.post('/api/auth/login', { username, password }, { withCredentials: true });
}
| GET | `/test/me` | Autenticado | Información del usuario actual |
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
---

## 📝 Notas para el alumno

- Las sesiones y cookies son fundamentales para mantener el estado del usuario en aplicaciones web.
- El backend puede ser consumido por cualquier frontend (Angular, React, Postman, curl, etc.) siempre que gestione correctamente las cookies de sesión.
- Si tienes problemas de autenticación, revisa que las cookies se estén enviando correctamente en cada petición.
- Puedes modificar la duración de la sesión en `application.properties`.

---
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
