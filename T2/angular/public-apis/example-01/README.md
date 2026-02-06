# Sistema de Autenticación JWT con Angular 21

## Índice

- [Descripción General](#descripción-general)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Flujo de Autenticación](#flujo-de-autenticación)
- [Componentes Principales](#componentes-principales)
- [Servicios](#servicios)
- [Guardas y Seguridad](#guardas-y-seguridad)
- [Interceptor HTTP](#interceptor-http)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [API Endpoints](#api-endpoints)

---

## Descripción General

Este proyecto es una aplicación de autenticación completa desarrollada con **Angular 21** que implementa:

- ✅ Registro de usuarios
- ✅ Inicio de sesión (Login)
- ✅ Autenticación JWT (JSON Web Tokens)
- ✅ Gestión de tokens (Access Token y Refresh Token)
- ✅ Protección de rutas
- ✅ Interceptor HTTP automático
- ✅ Gestión de estado con Signals
- ✅ Verificación de disponibilidad de email en tiempo real

**API Backend**: [Platzi Fake Store API](https://fakeapi.platzi.com/)

---

- Usuario de prueba: test123@example123.ac/Password123

## Arquitectura del Proyecto

```
src/app/
├── components/
│   ├── login/              # Componente de inicio de sesión
│   ├── register/           # Componente de registro
│   ├── home/              # Panel de usuario (zona protegida)
│   └── navbar/            # Barra de navegación
├── services/
│   └── auth.service.ts    # Servicio de autenticación
├── guards/
│   └── auth.guard.ts      # Guard para proteger rutas
├── interceptors/
│   └── auth.interceptor.ts # Interceptor HTTP para tokens
├── models/
│   └── user.model.ts      # Interfaces TypeScript
├── app.config.ts          # Configuración de la app
└── app.routes.ts          # Definición de rutas
```

---

## Flujo de Autenticación

### 1️ **Registro de Usuario**

```
Usuario → Formulario de Registro → AuthService.register()
    ↓
POST /api/v1/users/
    ↓
Usuario creado → Auto-login → Redirección a /home
```

**Archivo**: `register.component.ts`

- Valida los datos del formulario
- Verifica disponibilidad del email en tiempo real
- Crea el usuario en la API
- Hace login automáticamente después del registro exitoso

### 2️ **Inicio de Sesión (Login)**

```
Usuario → Formulario de Login → AuthService.login()
    ↓
POST /api/v1/auth/login
    ↓
Recibe: { access_token, refresh_token }
    ↓
🔐 1️⃣ TOKEN RECIBIDO (consola)
    ↓
💾 2️⃣ GUARDADO en localStorage
    ↓
👤 3️⃣ CARGA PERFIL usando el token
    ↓
GET /api/v1/auth/profile (con token en header)
    ↓
✅ Usuario autenticado → Redirección a /home
```

**Archivo**: `auth.service.ts` - método `login()`

**Consola del navegador muestra:**

```javascript
 ========== AUTENTICACIÓN JWT ==========
✅ 1️⃣ TOKEN RECIBIDO desde la API
📍 Endpoint: https://api.escuelajs.co/api/v1/auth/login
📦 Respuesta completa: {access_token: "...", refresh_token: "..."}
🎟️ Access Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
🔄 Refresh Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3️⃣ **Acceso a Rutas Protegidas**

```
Usuario intenta acceder a /home
    ↓
AuthGuard verifica si está autenticado
    ↓
¿Hay token? → SÍ → Permite acceso
            → NO → Redirecciona a /login
```

**Archivo**: `auth.guard.ts`

### 4️⃣ **Uso del Token en Peticiones HTTP**

```
Cualquier petición HTTP
    ↓
AuthInterceptor intercepta la petición
    ↓
🔒 4️⃣ Obtiene el token de localStorage
    ↓
Añade header: Authorization: Bearer <token>
    ↓
Envía petición con autenticación
```

**Archivo**: `auth.interceptor.ts`

**Consola del navegador muestra:**

```javascript
🔒 INTERCEPTOR HTTP: Añadiendo token JWT a la petición
📍 URL: https://api.escuelajs.co/api/v1/auth/profile
🎟️ Token: eyJhbGciOiJIUzI1NiIsInR5cCI6...
📤 Header Authorization: Bearer eyJhbGciOiJI...
```

---

## Componentes Principales

### LoginComponent (`login.component.ts`)

**Ubicación**: `src/app/components/login/`

**Responsabilidades**:

- Mostrar formulario de inicio de sesión
- Validar email y contraseña
- Llamar al servicio de autenticación
- Mostrar credenciales de prueba
- Redirigir al home después de login exitoso

**Características**:

- Formulario reactivo con validaciones
- Badge "🔐 Autenticación JWT" visible
- Manejo de errores visual
- Link a registro

**Credenciales de prueba**:

- Email: `john@mail.com`
- Password: `changeme`

### RegisterComponent (`register.component.ts`)

**Ubicación**: `src/app/components/register/`

**Responsabilidades**:

- Formulario de registro con validaciones
- Verificación de disponibilidad de email en tiempo real
- Validación de coincidencia de contraseñas
- Registro de nuevo usuario
- Auto-login después del registro

**Validaciones**:

- Nombre: mínimo 3 caracteres
- Email: formato válido y disponible
- Contraseña: mínimo 6 caracteres
- Confirmación de contraseña debe coincidir

### HomeComponent (`home.component.ts`)

**Ubicación**: `src/app/components/home/`

**Responsabilidades**:

- Mostrar información del usuario autenticado
- Mostrar detalles del token JWT
- Indicar que es una zona protegida
- Mostrar datos obtenidos del endpoint protegido

**Secciones visuales**:

1. **Badge de zona protegida**: "🔒 Zona Protegida - Acceso Autorizado"
2. **Tarjeta de usuario**: Avatar, nombre, email, rol, ID
3. **Información de autenticación JWT**:
   - Estado de sesión
   - Tipo de token (Bearer)
   - Preview del Access Token
   - Endpoint protegido usado
4. **Datos del perfil**: Información obtenida con el token

### NavbarComponent (`navbar.component.ts`)

**Ubicación**: `src/app/components/navbar/`

**Responsabilidades**:

- Navegación principal de la aplicación
- Mostrar opciones según estado de autenticación
- Botón de logout

**Cuando NO estás autenticado**:

- Logo y nombre de la app
- Botón "Iniciar Sesión"
- Botón "Registrarse"

**Cuando SÍ estás autenticado**:

- Logo y nombre de la app
- Link "Inicio"
- Nombre del usuario
- Botón "Cerrar Sesión"

---

## 🔧 Servicios

### AuthService (`auth.service.ts`)

**Ubicación**: `src/app/services/`

Es el **servicio central** de autenticación. Gestiona todo el flujo de autenticación JWT.

#### **Signals (Estado Reactivo)**

```typescript
private currentUserSignal = signal<User | null>(null);
private loadingSignal = signal<boolean>(false);

readonly currentUser = this.currentUserSignal.asReadonly();
readonly isAuthenticated = computed(() => this.currentUserSignal() !== null);
readonly isLoading = this.loadingSignal.asReadonly();
```

#### **Métodos Principales**

| Método                     | Descripción                            | Endpoint                   |
| -------------------------- | -------------------------------------- | -------------------------- |
| `register()`               | Registra un nuevo usuario              | POST `/users/`             |
| `login()`                  | Inicia sesión y obtiene tokens         | POST `/auth/login`         |
| `logout()`                 | Cierra sesión y limpia tokens          | -                          |
| `getProfile()`             | Obtiene perfil del usuario autenticado | GET `/auth/profile`        |
| `refreshToken()`           | Renueva el access token                | POST `/auth/refresh-token` |
| `checkEmailAvailability()` | Verifica si un email está disponible   | POST `/users/is-available` |
| `getAccessToken()`         | Obtiene el token del localStorage      | -                          |
| `getRefreshToken()`        | Obtiene el refresh token               | -                          |

#### **Almacenamiento de Tokens**

```typescript
private storeTokens(response: AuthResponse): void {
  localStorage.setItem('access_token', response.access_token);
  localStorage.setItem('refresh_token', response.refresh_token);
}
```

Las llaves usadas en localStorage:

- `access_token`: Token de acceso JWT
- `refresh_token`: Token para renovar el access token

#### **Constructor**

```typescript
constructor() {
  this.loadUserFromToken(); // Carga el usuario si hay un token guardado
}
```

Al iniciar la app, si existe un token en localStorage, intenta cargar automáticamente el perfil del usuario.

---

## 🛡️ Guardas y Seguridad

### AuthGuard (`auth.guard.ts`)

**Ubicación**: `src/app/guards/`

**Tipo**: `CanActivateFn` (Functional Guard de Angular)

**Función**: Protege rutas que requieren autenticación

```typescript
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true; // ✅ Permite acceso
  }

  // ❌ Redirecciona a login con URL de retorno
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url },
  });
};
```

**Uso en rutas**:

```typescript
{
  path: 'home',
  loadComponent: () => import('./components/home/home.component'),
  canActivate: [authGuard] // 🛡️ Ruta protegida
}
```

---

## 🔌 Interceptor HTTP

### AuthInterceptor (`auth.interceptor.ts`)

**Ubicación**: `src/app/interceptors/`

**Tipo**: `HttpInterceptorFn` (Functional Interceptor)

**Función**: Añade automáticamente el token JWT a todas las peticiones HTTP

```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken();

  // 🔒 Si hay token, clona la petición y añade el header
  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`, // 📤 Header con token
        },
      })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // ❌ Token inválido o expirado
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    }),
  );
};
```

**Configuración en `app.config.ts`**:

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor]), // 🔌 Registra el interceptor
    ),
  ],
};
```

**Funcionamiento**:

1. **Intercepta** cada petición HTTP
2. **Verifica** si hay token en localStorage
3. **Añade** el header `Authorization: Bearer <token>`
4. **Envía** la petición modificada
5. **Captura** errores 401 (no autorizado) y cierra sesión

---

## 🗺️ Rutas

**Archivo**: `app.routes.ts`

```typescript
export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    loadComponent: () => import('./components/home/home.component'),
    canActivate: [authGuard], // 🛡️ PROTEGIDA
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component'),
  },
  {
    path: 'register',
    loadComponent: () => import('./components/register/register.component'),
  },
  {
    path: '**',
    redirectTo: 'home',
  },
];
```

**Características**:

- ✅ Lazy loading de componentes (mejor rendimiento)
- ✅ Ruta protegida con `authGuard`
- ✅ Redirección por defecto a `/home`
- ✅ Wildcard para rutas no encontradas

---

## 🎨 Modelos e Interfaces

**Archivo**: `models/user.model.ts`

```typescript
export interface User {
  id: number;
  email: string;
  name: string;
  role: 'customer' | 'admin';
  avatar: string;
  password?: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  name: string;
  email: string;
  password: string;
  avatar?: string;
}

export interface AuthResponse {
  access_token: string;
  refresh_token: string;
}

export interface EmailAvailability {
  isAvailable: boolean;
}
```

---

## 🚀 Instalación y Ejecución

### Requisitos Previos

- Node.js (versión 18 o superior)
- npm o yarn
- Angular CLI 21

### Instalación

```bash
# Clonar el repositorio (si aplica)
cd example-01

# Instalar dependencias
npm install
```

### Ejecución

```bash
# Modo desarrollo
npm start

# La aplicación estará disponible en http://localhost:4200
```

### Build de Producción

```bash
npm run build
```

---

## 🌐 API Endpoints

**Base URL**: `https://api.escuelajs.co/api/v1`

### 1. Autenticación

#### Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@mail.com",
  "password": "changeme"
}
```

**Respuesta**:

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Obtener Perfil (Protegido 🔒)

```http
GET /auth/profile
Authorization: Bearer {access_token}
```

**Respuesta**:

```json
{
  "id": 1,
  "email": "john@mail.com",
  "name": "Jhon",
  "role": "customer",
  "avatar": "https://i.imgur.com/LDOO4Qs.jpg"
}
```

#### Refresh Token

```http
POST /auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "{refresh_token}"
}
```

### 2. Usuarios

#### Crear Usuario (Registro)

```http
POST /users/
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "avatar": "https://i.imgur.com/yhW6Yw1.jpg"
}
```

#### Verificar Disponibilidad de Email

```http
POST /users/is-available
Content-Type: application/json

{
  "email": "john@example.com"
}
```

**Respuesta**:

```json
{
  "isAvailable": false
}
```

---

## 🔍 Debugging y Depuración del Token JWT

El sistema incluye múltiples formas de depurar y visualizar el token JWT durante el desarrollo:

### Método 1: Consola del Navegador (Automático ✅)

El sistema ya incluye logs detallados. Simplemente abre las **DevTools** (`F12`) → pestaña **Console**:

#### Al hacer Login:

```javascript
🔐 ========== AUTENTICACIÓN JWT ==========
✅ 1️⃣ TOKEN RECIBIDO desde la API
📍 Endpoint: https://api.escuelajs.co/api/v1/auth/login
📦 Respuesta completa: {access_token: "...", refresh_token: "..."}
🎟️ Access Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
🔄 Refresh Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
========================================

💾 2️⃣ GUARDANDO TOKENS en localStorage
🔑 Key para Access Token: access_token
🔑 Key para Refresh Token: refresh_token
✅ Tokens guardados exitosamente en localStorage

👤 3️⃣ CARGANDO PERFIL DE USUARIO con el token
📍 Endpoint protegido: https://api.escuelajs.co/api/v1/auth/profile
🎟️ Usando Access Token para autenticación...
✅ Perfil cargado exitosamente: {id: 1, name: "...", email: "..."}
========================================
```

#### En cada petición HTTP protegida:

```javascript
🔒 INTERCEPTOR HTTP: Añadiendo token JWT a la petición
📍 URL: https://api.escuelajs.co/api/v1/auth/profile
🎟️ Token: eyJhbGciOiJIUzI1NiIsInR5cCI6...
📤 Header Authorization: Bearer eyJhbGciOIJI...
```

### Método 2: localStorage en DevTools

**Pasos:**

1. Abre **DevTools** (`F12`)
2. Ve a la pestaña **Application** (o **Almacenamiento**)
3. En el panel izquierdo: **Storage** → **Local Storage** → `http://localhost:4200`
4. Verás las claves con los tokens:

```
Key                    Value
────────────────────────────────────────────────────────────────
access_token          eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOj...
refresh_token         eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOj...
```

**Acceso rápido desde la consola:**

```javascript
// Ver el access token
localStorage.getItem('access_token');

// Ver el refresh token
localStorage.getItem('refresh_token');

// Ver ambos tokens
console.log({
  access: localStorage.getItem('access_token'),
  refresh: localStorage.getItem('refresh_token'),
});
```

### Método 3: Network Tab (Peticiones HTTP)

**Para ver el token en la RESPUESTA del login:**

1. Abre **DevTools** (`F12`)
2. Ve a **Network** (Red)
3. Haz login en la aplicación
4. Busca la petición `auth/login`
5. Click en ella → pestaña **Response**
6. Verás el JSON con los tokens:

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Para ver el token en las PETICIONES protegidas:**

1. En la pestaña **Network**, busca la petición `auth/profile`
2. Click en ella → pestaña **Headers**
3. Busca **Request Headers**
4. Verás: `Authorization: Bearer eyJhbGciOiJIUzI1N...`

### Método 4: En la UI del Home (Ya implementado ✅)

Después de hacer login, ve a la ruta `/home`. La interfaz muestra:

- 🔒 **Badge**: "Zona Protegida - Acceso Autorizado"
- 🎟️ **Token Preview**: Primeros y últimos caracteres del token
- 📍 **Endpoint usado**: URL del endpoint protegido
- 👤 **Datos del perfil**: Obtenidos usando el token

### Método 5: Decodificar el Token con JWT.io

Para ver **qué información contiene el token** (payload decodificado):

**Pasos:**

1. Copia el token usando cualquiera de los métodos anteriores
2. Ve a [https://jwt.io/](https://jwt.io/)
3. Pega el token en el campo **Encoded**
4. Verás automáticamente el **Header**, **Payload** y **Signature** decodificados

**Ejemplo de payload decodificado:**

```json
{
  "sub": 1, // ID del usuario
  "iat": 1675688641, // Issued at (timestamp de creación)
  "exp": 1677424641, // Expiration (timestamp de expiración)
  "role": "customer" // Rol del usuario
}
```

**Campos importantes:**

- `sub`: Subject, identificador único del usuario
- `iat`: Issued At, cuándo se emitió el token (Unix timestamp)
- `exp`: Expiration, cuándo expira el token (Unix timestamp)
- `role`: Rol del usuario en el sistema

### Método 6: Angular DevTools

Si tienes instalada la extensión **Angular DevTools**:

1. Abre **DevTools** → pestaña **Angular**
2. Ve a **Component Explorer**
3. Selecciona el componente `HomeComponent`
4. En el panel derecho verás las propiedades del componente
5. Busca `authService` → `currentUser` (señal con datos del usuario)

### Método 7: Breakpoints (Puntos de Ruptura)

Los **breakpoints** permiten **pausar la ejecución del código** para inspeccionar variables en tiempo real.

#### Opción A: Breakpoints en Chrome DevTools

**Pasos:**

1. Abre **DevTools** (`F12`) → pestaña **Sources**
2. En el panel izquierdo, navega a tu archivo TypeScript:
   ```
   webpack://
     → src
       → app
         → services
           → auth.service.ts
   ```
3. Click en el **número de línea** donde deseas pausar la ejecución:

**Ubicaciones recomendadas:**

```typescript
// 📍 Breakpoint 1: Cuando se recibe el token de la API
login(credentials: LoginCredentials): Observable<AuthResponse> {
  return this.http.post<AuthResponse>(`${this.API_URL}/auth/login`, credentials)
    .pipe(
      tap((response) => {
        debugger; // ← O pon breakpoint aquí (línea ~72)
        console.log('🔐 Token recibido:', response);
        this.storeTokens(response);
        this.loadUserProfile().subscribe();
      })
    );
}

// 📍 Breakpoint 2: Cuando se guarda el token
private storeTokens(response: AuthResponse): void {
  debugger; // ← O pon breakpoint aquí (línea ~159)
  localStorage.setItem('access_token', response.access_token);
  localStorage.setItem('refresh_token', response.refresh_token);
}

// 📍 Breakpoint 3: En el interceptor cuando se usa el token
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken();

  debugger; // ← O pon breakpoint aquí (línea ~16 del interceptor)
  console.log('Token a usar:', token);

  const authReq = token ? req.clone({...}) : req;
  return next(authReq);
};
```

4. Cuando la ejecución llegue al breakpoint, se pausará
5. En el panel derecho de **Sources**, verás:
   - **Scope**: Variables locales y su valor
   - **Call Stack**: Pila de llamadas
   - **Watch**: Variables que quieras observar

**Controles del debugger:**

- ▶️ **Resume** (`F8`): Continuar ejecución
- ⤵️ **Step Over** (`F10`): Saltar a la siguiente línea
- ⬇️ **Step Into** (`F11`): Entrar a la función
- ⬆️ **Step Out** (`Shift+F11`): Salir de la función

**Inspeccionar el token durante el breakpoint:**

```javascript
// Cuando el código esté pausado, escribe en la consola:
response.access_token; // Ver el token completo
token; // Ver la variable token
localStorage.getItem('access_token'); // Ver lo que está en storage
```

#### 🟪 Opción B: Breakpoints en VS Code

**Prerequisitos:**

- Instalar extensión: [JavaScript Debugger](https://marketplace.visualstudio.com/items?itemName=ms-vscode.js-debug)
- ✅ Ya viene incluida en VS Code moderno

**Configuración (sólo una vez):**

1. En VS Code, presiona `F5` o ve a **Run and Debug** (icono ▶️)
2. Click en **"create a launch.json file"**
3. Selecciona **"Web App (Chrome)"**
4. Reemplaza el contenido con:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "launch",
      "name": "Angular App",
      "url": "http://localhost:4200",
      "webRoot": "${workspaceFolder}",
      "sourceMapPathOverrides": {
        "webpack:/*": "${webRoot}/*",
        "/./*": "${webRoot}/*",
        "/src/*": "${webRoot}/*",
        "/*": "*",
        "/./~/*": "${webRoot}/node_modules/*"
      }
    }
  ]
}
```

**Uso:**

1. **Inicia tu app** con `npm start` (en terminal aparte)
2. En VS Code, abre `auth.service.ts`
3. Click en el margen izquierdo (junto al número de línea) para poner un **punto rojo** (breakpoint)
4. Presiona `F5` para iniciar el debugger
5. Se abrirá Chrome automáticamente
6. Usa la app normalmente (haz login)
7. Cuando llegue al breakpoint, VS Code se enfocará y verás:
   - **Variables** en el panel izquierdo
   - **Call Stack** de llamadas
   - **Watch**: Para añadir expresiones a observar

**Variables a observar (Add to Watch):**

```typescript
response.access_token;
response.refresh_token;
this.currentUserSignal();
localStorage.getItem('access_token');
```

#### 🟨 Opción C: debugger; Statement (Rápido)

La forma **más rápida** sin configuración:

1. Abre `auth.service.ts`
2. Añade `debugger;` en el código:

```typescript
login(credentials: LoginCredentials): Observable<AuthResponse> {
  return this.http.post<AuthResponse>(`${this.API_URL}/auth/login`, credentials)
    .pipe(
      tap((response) => {
        debugger; // ⛔ La ejecución se pausará AQUÍ
        console.log('Token:', response.access_token);
        this.storeTokens(response);
      })
    );
}
```

3. Abre DevTools (`F12`)
4. Haz login en la app
5. **Automáticamente** se pausará en el `debugger;`
6. Inspecciona `response.access_token` en la consola

⚠️ **Recuerda eliminar los `debugger;` antes de producción**

#### 🎯 Mejores Lugares para Breakpoints en Flujo JWT

```
📍 auth.service.ts línea ~72
   ↓ Función: login() → tap()
   🔍 Ver: response.access_token, response.refresh_token

📍 auth.service.ts línea ~159
   ↓ Función: storeTokens()
   🔍 Ver: response objeto completo antes de guardar

📍 auth.service.ts línea ~174
   ↓ Función: loadUserProfile()
   🔍 Ver: token siendo usado, respuesta del profile

📍 auth.interceptor.ts línea ~16
   ↓ Función: authInterceptor
   🔍 Ver: token, req.headers, authReq modificado

📍 login.component.ts línea ~45
   ↓ Función: onSubmit()
   🔍 Ver: credentials antes de enviar
```

### 🎯 Comparación de Métodos

| Método               | Mejor para                       | Velocidad | Información                   | Nivel        |
| -------------------- | -------------------------------- | --------- | ----------------------------- | ------------ |
| **Console**          | Ver flujo completo               | ⚡⚡⚡    | Token + logs del proceso      | Principiante |
| **localStorage**     | Acceso directo al token          | ⚡⚡⚡    | Token completo                | Principiante |
| **Network**          | Ver HTTP headers                 | ⚡⚡      | Token + peticiones/respuestas | Intermedio   |
| **UI Home**          | UX y demostración                | ⚡⚡⚡    | Token + datos de usuario      | Principiante |
| **JWT.io**           | Decodificar payload              | ⚡        | Contenido interno del token   | Principiante |
| **Angular DevTools** | Estado de la app                 | ⚡⚡      | Estado completo del servicio  | Intermedio   |
| **Breakpoints** 🔴   | Debugging profundo línea a línea | ⚡        | Variables en tiempo real      | Avanzado     |

### 🚀 Recomendación para Development

**Debugging rápido (sin breakpoints):**

```javascript
// Pega esto en la consola del navegador (F12)
console.log('🎟️ Access Token:', localStorage.getItem('access_token'));
console.log('🔄 Refresh Token:', localStorage.getItem('refresh_token'));
```

**Debugging avanzado con breakpoint (recomendado):**

```typescript
// En auth.service.ts, método login(), añade:
tap((response) => {
  debugger; // ⛔ Pausa aquí y explora response en la consola
  console.log('Token recibido:', response.access_token);
  this.storeTokens(response);
});
```

Luego:

1. Abre DevTools (`F12`)
2. Haz login
3. Cuando pause, escribe en la consola:
   ```javascript
   response; // Ver objeto completo
   response.access_token; // Ver token
   ```

**Ver el payload decodificado sin salir de la consola:**

```javascript
// Función helper para decodificar JWT en la consola
function decodeJWT(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split('')
      .map((c) => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      })
      .join(''),
  );
  return JSON.parse(jsonPayload);
}

// Usar la función
const token = localStorage.getItem('access_token');
console.log('📦 Payload decodificado:', decodeJWT(token));
```

### 🔐 Notas de Seguridad

⚠️ **IMPORTANTE**:

- Los tokens en `localStorage` son accesibles desde JavaScript
- **NUNCA** compartas capturas de pantalla con tokens reales
- Los tokens mostrados en la UI están truncados por seguridad
- En producción, considera usar `httpOnly` cookies si es posible

---

## 📊 Características de Angular 21 Utilizadas

- ✅ **Standalone Components**: Sin NgModules
- ✅ **Signals**: Gestión de estado reactivo
- ✅ **Control Flow nativo**: `@if`, `@for` (en lugar de `*ngIf`, `*ngFor`)
- ✅ **Functional Guards**: `CanActivateFn`
- ✅ **Functional Interceptors**: `HttpInterceptorFn`
- ✅ **Función `inject()`**: Inyección de dependencias sin constructor
- ✅ **`input()` y `output()`**: Para inputs y outputs de componentes
- ✅ **Reactive Forms**: FormBuilder con validaciones
- ✅ **Lazy Loading**: Carga diferida de componentes

---

## 🔐 Mejores Prácticas Implementadas

1. **Separación de responsabilidades**: Componentes, servicios, guards separados
2. **Inmutabilidad**: Uso de signals para estado reactivo
3. **Tipado fuerte**: TypeScript con interfaces bien definidas
4. **Manejo de errores**: Try-catch y operadores RxJS
5. **Seguridad**: Tokens en localStorage, interceptor automático
6. **UX**: Loading states, mensajes de error claros
7. **Accesibilidad**: Labels, ARIA attributes
8. **Responsive**: Mobile-first design

---

## �️ Roadmap y Plan de Desarrollo Futuro

El sistema actual implementa **autenticación JWT completa**. A continuación se detalla el plan para convertir esto en un **frontend e-commerce completo**.

### 📊 Estado Actual vs Planificado

| Módulo                    | Estado         | Descripción                                    |
| ------------------------- | -------------- | ---------------------------------------------- |
| **Autenticación**         | ✅ Completo    | Login, registro, JWT, guards, interceptor      |
| **Catálogo de Productos** | 📋 Planificado | Listado, filtros, búsqueda, paginación         |
| **Detalle de Producto**   | 📋 Planificado | Vista detalle, galería, productos relacionados |
| **Categorías**            | 📋 Planificado | Listado, filtros por categoría                 |
| **Panel Admin**           | 📋 Planificado | CRUD completo productos y categorías           |
| **Perfil de Usuario**     | 📋 Planificado | Edición de datos, avatar                       |
| **Carrito**               | 💡 Opcional    | LocalStorage, gestión de items                 |

---

## 🏗️ Arquitectura del Frontend Completo (Planificado)

### **Estructura de Carpetas Completa**

```
src/app/
├── core/
│   ├── guards/
│   │   ├── auth.guard.ts          ✅ Implementado
│   │   └── admin.guard.ts         📋 Planificado
│   ├── interceptors/
│   │   └── auth.interceptor.ts    ✅ Implementado
│   └── services/
│       ├── auth.service.ts        ✅ Implementado
│       ├── product.service.ts     📋 Planificado
│       └── category.service.ts    📋 Planificado
│
├── shared/
│   ├── components/
│   │   ├── navbar/                ✅ Implementado
│   │   ├── product-card/          📋 Planificado
│   │   ├── loading-spinner/       📋 Planificado
│   │   ├── pagination/            📋 Planificado
│   │   └── confirm-dialog/        📋 Planificado
│   └── pipes/
│       ├── currency.pipe.ts       📋 Planificado
│       └── truncate.pipe.ts       📋 Planificado
│
├── features/
│   ├── auth/                      ✅ Implementado
│   │   ├── login/
│   │   ├── register/
│   │   └── profile/               📋 Planificado
│   │
│   ├── shop/                      📋 Planificado
│   │   ├── product-list/
│   │   ├── product-detail/
│   │   ├── product-filters/
│   │   ├── category-list/
│   │   └── category-detail/
│   │
│   └── admin/                     📋 Planificado
│       ├── dashboard/
│       ├── products/
│       │   ├── product-list/
│       │   └── product-form/
│       └── categories/
│           └── category-manager/
│
└── models/
    ├── user.model.ts              ✅ Implementado
    ├── product.model.ts           📋 Planificado
    └── category.model.ts          📋 Planificado
```

---

## 🛣️ Rutas Planificadas

```typescript
const routes: Routes = [
  { path: '', redirectTo: 'shop', pathMatch: 'full' },

  // 🏪 Tienda Pública
  { path: 'shop', component: ProductListComponent },
  { path: 'shop/product/:id', component: ProductDetailComponent },
  { path: 'categories', component: CategoryListComponent },
  { path: 'categories/:slug', component: CategoryDetailComponent },

  // 🔐 Autenticación (Implementado)
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // 👤 Área de Usuario (Protegido)
  {
    path: 'profile',
    component: UserProfileComponent,
    canActivate: [authGuard],
  },

  // ⚙️ Panel Admin (Protegido + Solo Admin)
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'products', component: AdminProductsComponent },
      { path: 'products/new', component: ProductFormComponent },
      { path: 'products/edit/:id', component: ProductFormComponent },
      { path: 'categories', component: AdminCategoriesComponent },
    ],
  },

  { path: '**', redirectTo: 'shop' },
];
```

---

## 📦 Servicios Planificados

### **ProductService**

```typescript
// Endpoints disponibles en la API
GET    /products                     // Lista paginada
GET    /products/:id                 // Detalle por ID
GET    /products/slug/:slug          // Detalle por slug
POST   /products                     // Crear (admin)
PUT    /products/:id                 // Actualizar (admin)
DELETE /products/:id                 // Eliminar (admin)
GET    /products?title=...           // Filtrar por título
GET    /products?categoryId=...      // Filtrar por categoría
GET    /products?price_min=...&price_max=... // Filtrar por precio
GET    /products?offset=0&limit=10   // Paginación
```

### **CategoryService**

```typescript
// Endpoints disponibles en la API
GET    /categories                   // Lista completa
GET    /categories/:id               // Detalle por ID
GET    /categories/slug/:slug        // Detalle por slug
POST   /categories                   // Crear (admin)
PUT    /categories/:id               // Actualizar (admin)
DELETE /categories/:id               // Eliminar (admin)
GET    /categories/:id/products      // Productos por categoría
```

---

## 🎯 Fases de Desarrollo Sugeridas

### **Fase 1: MVP - Catálogo Público** 🟢

**Objetivo**: Permitir a usuarios ver productos y categorías

**Tareas**:

1. Crear modelos: `Product`, `Category`, `ProductFilters`
2. Implementar `ProductService` con métodos GET
3. Implementar `CategoryService` con métodos GET
4. Crear componente `ProductListComponent` con grid de productos
5. Crear componente `ProductFiltersComponent` (título, categoría, precio)
6. Crear componente `ProductDetailComponent` con galería
7. Crear componente `CategoryListComponent`
8. Implementar paginación con offset/limit
9. Crear `ProductCardComponent` reutilizable

**Endpoints usados**:

- `GET /products`
- `GET /products/:id`
- `GET /categories`
- `GET /categories/:id/products`

---

### **Fase 2: Panel de Administración** 🔴

**Objetivo**: Permitir a administradores gestionar productos y categorías

**Tareas**:

1. Crear `AdminGuard` (verificar `role === 'admin'`)
2. Implementar métodos POST/PUT/DELETE en servicios
3. Crear `AdminDashboardComponent` con estadísticas
4. Crear `AdminProductsComponent` con tabla
5. Crear `ProductFormComponent` (crear/editar)
6. Crear `AdminCategoriesComponent` con CRUD
7. Implementar `ConfirmDialogComponent` para deletes
8. Añadir validaciones en formularios reactivos

**Endpoints usados**:

- `POST /products`
- `PUT /products/:id`
- `DELETE /products/:id`
- `POST /categories`
- `PUT /categories/:id`
- `DELETE /categories/:id`

---

### **Fase 3: Funcionalidades Extra** 🌟

**Objetivo**: Mejorar UX y añadir features avanzadas

**Tareas**:

1. Edición de perfil de usuario (`PUT /users/:id`)
2. Sistema de favoritos (LocalStorage)
3. Carrito de compras (frontend-only)
4. Upload de imágenes (`POST /files/upload`)
5. Búsqueda avanzada con autocompletado
6. Infinite scroll en listado de productos
7. Filtros con URL query params (compartir filtros)
8. Dark mode toggle

---

## 📡 Integración con API Externa (OpenAPI/Swagger)

### **Documentación de la API**

La aplicación se integra con **Platzi Fake Store API**, que proporciona:

- 📄 **Swagger UI**: [https://fakeapi.platzi.com/en/rest/swagger/](https://fakeapi.platzi.com/en/rest/swagger/)
- 📖 **Documentación completa**: [https://fakeapi.platzi.com/en/about/introduction/](https://fakeapi.platzi.com/en/about/introduction/)

### **Características de la API**

| Característica    | Estado          | Detalles                           |
| ----------------- | --------------- | ---------------------------------- |
| **REST API**      | ✅ Disponible   | Endpoints RESTful estándar         |
| **GraphQL**       | ✅ Disponible   | Alternativa para queries complejas |
| **JWT Auth**      | ✅ Implementado | Access + Refresh tokens            |
| **CRUD Completo** | ✅ Disponible   | Create, Read, Update, Delete       |
| **Paginación**    | ✅ Disponible   | offset/limit parameters            |
| **Filtros**       | ✅ Disponible   | title, price, category             |
| **File Upload**   | ✅ Disponible   | multipart/form-data                |
| **CORS**          | ✅ Habilitado   | Sin restricciones                  |

### **OpenAPI Schema**

Puedes obtener el schema OpenAPI/Swagger desde:

```bash
# Ver documentación interactiva
https://fakeapi.platzi.com/en/rest/swagger/

# Descargar schema JSON
curl https://api.escuelajs.co/api-docs/
```

### **Colecciones Postman/Insomnia**

La API proporciona colecciones pre-configuradas:

- **Postman**: [Descargar colección](https://fakeapi.platzi.com/en/resources/postman/)
- **Insomnia**: [Descargar colección](https://fakeapi.platzi.com/en/resources/insomnia/)

### **Ejemplo de Integración con Angular**

```typescript
// product.service.ts
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly API_URL = 'https://api.escuelajs.co/api/v1';

  constructor(private http: HttpClient) {}

  // Listado con filtros
  getProducts(filters?: ProductFilters): Observable<Product[]> {
    let params = new HttpParams();

    if (filters?.title) params = params.set('title', filters.title);
    if (filters?.categoryId) params = params.set('categoryId', filters.categoryId);
    if (filters?.priceMin) params = params.set('price_min', filters.priceMin);
    if (filters?.priceMax) params = params.set('price_max', filters.priceMax);
    if (filters?.offset !== undefined) params = params.set('offset', filters.offset);
    if (filters?.limit) params = params.set('limit', filters.limit);

    return this.http.get<Product[]>(`${this.API_URL}/products`, { params });
  }

  // Detalle de producto
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.API_URL}/products/${id}`);
  }

  // Crear producto (requiere autenticación)
  createProduct(data: ProductCreate): Observable<Product> {
    return this.http.post<Product>(`${this.API_URL}/products`, data);
  }

  // Actualizar producto (requiere autenticación)
  updateProduct(id: number, data: Partial<ProductUpdate>): Observable<Product> {
    return this.http.put<Product>(`${this.API_URL}/products/${id}`, data);
  }

  // Eliminar producto (requiere autenticación)
  deleteProduct(id: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.API_URL}/products/${id}`);
  }
}
```

### **Testing de Endpoints**

Puedes probar los endpoints directamente:

```bash
# Obtener productos
curl https://api.escuelajs.co/api/v1/products?limit=5

# Obtener categorías
curl https://api.escuelajs.co/api/v1/categories

# Login
curl -X POST https://api.escuelajs.co/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@mail.com","password":"changeme"}'

# Crear producto (requiere token)
curl -X POST https://api.escuelajs.co/api/v1/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "New Product",
    "price": 100,
    "description": "Description",
    "categoryId": 1,
    "images": ["https://placehold.co/600x400"]
  }'
```

---

## 🐳 Docker y Despliegue

### **Archivos Docker Incluidos**

El proyecto incluye configuración completa para Docker:

```
📁 Project Root
├── Dockerfile              # Multi-stage build (Node + Nginx)
├── docker-compose.yml      # Orquestación de servicios
├── nginx.conf             # Configuración Nginx optimizada
├── .dockerignore          # Archivos excluidos del build
└── .env.example           # Variables de entorno template
```

---

### **🔨 Build y Ejecución Local**

#### **Opción 1: Docker Compose (Recomendado)**

```bash
# Build y ejecutar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener
docker-compose down

# Rebuild
docker-compose up -d --build
```

**Acceso**: [http://localhost:8080](http://localhost:8080)

#### **Opción 2: Docker directamente**

```bash
# Build de la imagen
docker build -t angular-jwt-app .

# Ejecutar contenedor
docker run -d -p 8080:80 --name angular-app angular-jwt-app

# Ver logs
docker logs -f angular-app

# Detener y eliminar
docker stop angular-app
docker rm angular-app
```

---

### **🚀 Despliegue en Proveedores Gratuitos**

#### **1. Render.com** (Recomendado - Docker nativo)

**Características**:

- ✅ Plan gratuito con 750 horas/mes
- ✅ Soporte Docker nativo
- ✅ SSL automático (HTTPS)
- ✅ Deploy automático desde Git
- ✅ Health checks

**Pasos**:

1. **Crear cuenta en [Render.com](https://render.com)**

2. **Conectar tu repositorio Git**

3. **Crear nuevo Web Service**:
   - Environment: `Docker`
   - Build Command: (vacío, usa Dockerfile)
   - Start Command: (vacío, usa CMD del Dockerfile)
   - Plan: `Free`

4. **Variables de entorno** (opcional):

   ```
   NODE_ENV=production
   PORT=80
   ```

5. **Deploy**: Render automáticamente:
   - Detecta el `Dockerfile`
   - Ejecuta el build multi-stage
   - Expone el puerto 80
   - Genera una URL: `https://tu-app.onrender.com`

**Configuración en `render.yaml`** (opcional):

```yaml
services:
  - type: web
    name: angular-jwt-app
    env: docker
    plan: free
    healthCheckPath: /health
    envVars:
      - key: NODE_ENV
        value: production
```

---

#### **2. Railway.app** (Deploy rápido)

**Características**:

- ✅ $5 de crédito gratis/mes
- ✅ Deploy con un click
- ✅ SSL automático
- ✅ Soporte Docker

**Pasos**:

```bash
# Instalar Railway CLI
npm i -g @railway/cli

# Login
railway login

# Deploy
railway up

# Obtener URL
railway open
```

O desde la web:

1. Conecta GitHub
2. Selecciona el repositorio
3. Railway detecta el Dockerfile
4. Deploy automático

---

#### **3. Fly.io** (Global CDN)

**Características**:

- ✅ Plan gratuito generoso
- ✅ Deploy global (múltiples regiones)
- ✅ SSL automático
- ✅ CLI potente

**Pasos**:

```bash
# Instalar Fly CLI
curl -L https://fly.io/install.sh | sh

# Login
flyctl auth login

# Lanzar app (crea fly.toml automáticamente)
flyctl launch

# Deploy
flyctl deploy

# Ver app
flyctl open
```

**`fly.toml` generado**:

```toml
app = "angular-jwt-app"

[build]
  dockerfile = "Dockerfile"

[http_service]
  internal_port = 80
  force_https = true

[[services]]
  internal_port = 80
  protocol = "tcp"

  [[services.ports]]
    port = 80
    handlers = ["http"]

  [[services.ports]]
    port = 443
    handlers = ["tls", "http"]
```

---

#### **4. Vercel** (Alternativa sin Docker)

**Nota**: Vercel no soporta Docker directamente, pero puedes desplegar la build estática:

```bash
# Instalar Vercel CLI
npm i -g vercel

# Build local
npm run build

# Deploy
vercel --prod
```

**`vercel.json`**:

```json
{
  "version": 2,
  "builds": [
    {
      "src": "package.json",
      "use": "@vercel/static-build",
      "config": {
        "distDir": "dist/example-01/browser"
      }
    }
  ],
  "routes": [
    {
      "src": "/(.*)",
      "dest": "/index.html"
    }
  ]
}
```

---

### **📊 Comparativa de Proveedores**

| Proveedor   | Docker           | Plan Gratuito | SSL | Auto Deploy | CDN       | Recomendado |
| ----------- | ---------------- | ------------- | --- | ----------- | --------- | ----------- |
| **Render**  | ✅ Nativo        | 750h/mes      | ✅  | ✅          | ❌        | ⭐⭐⭐⭐⭐  |
| **Railway** | ✅ Nativo        | $5 crédito    | ✅  | ✅          | ❌        | ⭐⭐⭐⭐    |
| **Fly.io**  | ✅ Nativo        | 3 VMs gratis  | ✅  | ✅          | ✅ Global | ⭐⭐⭐⭐⭐  |
| **Vercel**  | ❌ Solo estático | Ilimitado     | ✅  | ✅          | ✅ Edge   | ⭐⭐⭐⭐    |

---

### **🔍 Monitoreo y Logs**

#### **Health Check Endpoint**

El `nginx.conf` incluye un endpoint de health:

```bash
# Verificar salud de la app
curl http://localhost:8080/health
# Respuesta: healthy
```

#### **Ver logs en producción**:

```bash
# Render
# Dashboard → Logs tab

# Railway
railway logs

# Fly.io
flyctl logs
```

---

### **⚙️ Variables de Entorno**

Crea un archivo `.env` basado en `.env.example`:

```bash
cp .env.example .env
```

**Contenido**:

```env
NODE_ENV=production
PORT=8080
API_URL=https://api.escuelajs.co/api/v1
```

**Configurar en producción**:

- **Render**: Settings → Environment → Add Variable
- **Railway**: Variables tab → New Variable
- **Fly.io**: `flyctl secrets set API_URL=...`

---

### **🔒 Seguridad en Producción**

El `nginx.conf` incluye:

- ✅ Headers de seguridad (X-Frame-Options, X-XSS-Protection)
- ✅ Compresión Gzip
- ✅ Cache de assets estáticos
- ✅ Rutas SPA (redirect a index.html)
- ✅ Health check endpoint

---

### **📦 CI/CD Automático**

**Render y Railway** detectan cambios en Git automáticamente:

```bash
# 1. Commit y push
git add .
git commit -m "New feature"
git push origin main

# 2. Deploy automático en 2-3 minutos
# 3. Notificación por email cuando termine
```

---

## 📚 Recursos

- [Angular Documentation](https://angular.dev)
- [Platzi Fake Store API](https://fakeapi.platzi.com/)
- [JWT.io](https://jwt.io/) - Para decodificar tokens JWT
- [RxJS](https://rxjs.dev/) - Programación reactiva
- [Docker Docs](https://docs.docker.com/)
- [Render Docs](https://render.com/docs)
- [Fly.io Docs](https://fly.io/docs/)
- [Railway Docs](https://docs.railway.app/)

---

## 🤝 Contribución

Este es un proyecto educativo. Para mejoras:

1. Fork el repositorio
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -m 'Añadir nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

---

## 📝 Licencia

Este proyecto es de uso educativo.

---

## 👨‍💻 Autor

Desarrollado como material educativo para aprender autenticación JWT con Angular 21.

---

## ❓ FAQ

### ¿Dónde se guarda el token?

En `localStorage` del navegador con las claves:

- `access_token`
- `refresh_token`

### ¿Cuánto dura el token?

Según la API de Platzi:

- Access Token: 20 días
- Refresh Token: 10 horas

### ¿Cómo veo el token en la consola?

Abre las DevTools (F12) y verás logs detallados en cada login y petición HTTP.

### ¿Qué pasa si el token expira?

El interceptor detecta errores 401 y cierra sesión automáticamente, redirigiendo al login.

### ¿Puedo usar otra API?

Sí, solo cambia la `API_URL` en `auth.service.ts` y ajusta las interfaces según la respuesta de tu API.
