# Desarrollo del Frontend (Angular moderno, 2026)

Este documento explica **cómo crear un frontend Angular moderno** (estilo 2026) para este backend Spring Boot + Spring Security + JWT.

Objetivo (versión 1):
- Registro (`/register`)
- Login (`/login`)
- Acceso a una zona protegida de usuario (`/user`) **solo si tiene rol de usuario**

> Nota: La seguridad real siempre está en el backend. El frontend solo ayuda a la UX (redirigir, ocultar enlaces, etc.).

---

## 0) Qué endpoints del backend vamos a consumir

Basado en este proyecto (controladores en `src/main/java/.../controllers`):

- `POST /api/auth/signup`
  - Crea usuario.
  - Body típico:
    - `username`: string
    - `email`: string
    - `password`: string
    - `role`: opcional. En muchos ejemplos se envía `role: ["user"]` para ROLE_USER.

- `POST /api/auth/signin`
  - Autentica.
  - Body:
    - `username`: string
    - `password`: string
  - Respuesta típica `JwtResponse`:
    - `accessToken`: string
    - `tokenType`: "Bearer"
    - `roles`: array de roles (normalmente `ROLE_USER`, `ROLE_ADMIN`...)

- `GET /api/test/user`
  - Endpoint protegido.
  - Requiere header:
    - `Authorization: Bearer <accessToken>`

---

## 1) Requisitos previos

- Node.js LTS instalado (recomendable 20+)
- npm
- Java/Maven ya los tienes para el backend

---

## 2) CORS: imprescindible para que Angular pueda llamar al backend

### ¿Por qué?
En desarrollo, Angular corre normalmente en `http://localhost:4200` y tu backend en `http://localhost:8080`.
Eso son **orígenes distintos** → el navegador aplica CORS.

### Estado actual
En este repo ya se ha añadido CORS en `WebSecurityConfig.java` para permitir `http://localhost:4200` y el header `Authorization`.

### Si cambias el puerto del frontend
Si ejecutas Angular en otro puerto (ej. `4201`) o con otra URL (`127.0.0.1`), tendrás que añadirlo al CORS del backend.

---

## 3) Cómo verificar el JWT (para no perder tiempo con "Invalid Signature")

### Importante
Cuando verifiques un token en jwt.io/token.dev debes usar:
- El **mismo algoritmo** (HS256 en este proyecto)
- La **misma key** que usa el backend
- Y marcar/desmarcar "base64 secret" según el formato que uses.

### Recomendación práctica
Para una práctica con Angular, lo más sencillo es:
- Usar una clave HS256 válida
- Y si la guardas en Base64 en `application.properties`, entonces en jwt.io:
  - O bien pegas la clave decodificada (texto) y desmarcas base64
  - O bien pegas la base64 y marcas base64

---

## 4) Crear el frontend Angular (standalone + componentes)

> Vamos a crear el frontend dentro de este repo, en una carpeta `frontend/`.

### 4.1 Comando (PowerShell)
Ejecuta esto en la raíz del proyecto (misma carpeta que `pom.xml`):

```powershell
cd "D:\ws\curso2526\Labs_DWES_2526\T2\springsecurity\spring-boot-spring-security-jwt-authentication"
npx -y @angular/cli@latest new frontend --routing --style=scss --skip-git --package-manager=npm
```

Esto crea un Angular moderno (por defecto con **standalone components** en Angular actual).

### 4.2 Arrancar el frontend

```powershell
cd .\frontend
npm start
```

Abrirá `http://localhost:4200`.

---

## 5) Estructura recomendada del frontend

Dentro de `frontend/src/app/`:

- `core/`
  - `auth/` → estado de auth (token, roles) + helpers
  - `http/` → interceptor JWT
  - `guards/` → guard por rol
- `features/`
  - `auth/` → páginas de login y registro
  - `user/` → página `/user`

---

## 6) Implementación: flujo completo (registro → login → /user)

### 6.1 Registro
1. Usuario rellena formulario.
2. Frontend llama a `POST /api/auth/signup`.
3. Si todo va bien:
   - mostrar mensaje "Registro OK"
   - redirigir a `/login`.

**Roles**:
- Si tu backend asigna ROLE_USER por defecto, puedes no enviar `role`.
- Si tu backend exige rol explícito, envía `role: ["user"]`.

### 6.2 Login
1. Usuario rellena login.
2. Frontend llama a `POST /api/auth/signin`.
3. Backend devuelve `accessToken` + `roles`.
4. Frontend guarda:
   - `accessToken`
   - `roles`
   - `username`
   en `localStorage` (simple) y también en un store con signals.
5. Redirige a `/user`.

### 6.3 Acceso a `/user`
1. Route guard comprueba:
   - hay token
   - y tiene rol permitido
2. Si no:
   - redirige a `/login`
3. Si sí:
   - muestra la página y llama a `GET /api/test/user`.

---

## 7) Paso a paso técnico (qué hay que programar)

### Paso A — Configurar la URL del backend en el frontend
Crea `frontend/src/environments/environment.ts` (o ajusta el existente) con:

- `apiBaseUrl = "http://localhost:8080"`

### Paso B — Crear `AuthService`
Funciones:
- `register()` → POST `/api/auth/signup`
- `login()` → POST `/api/auth/signin`
- `getUserContent()` → GET `/api/test/user` (text)

### Paso C — Crear `AuthStore` (signals)
- Señales:
  - token
  - roles
  - username
- Computed:
  - `isAuthenticated`
- Métodos:
  - `setSession(jwtResponse)`
  - `logout()`

Persistencia:
- Guardar sesión en `localStorage`.

### Paso D — Interceptor JWT
- Si hay token → añadir `Authorization: Bearer <token>`

### Paso E — Guard por rol
- Protege `/user`
- Permitidos: `ROLE_USER` (y opcionalmente `USER` si normalizas)

### Paso F — Componentes/páginas
- `LoginPageComponent`:
  - formulario (reactive forms)
  - submit → login → guardar session → navegar a `/user`

- `RegisterPageComponent`:
  - formulario
  - submit → register → navegar a `/login`

- `UserPageComponent`:
  - botón o carga inicial → GET `/api/test/user`

---

## 8) Checklist de depuración (si algo falla)

### 8.1 CORS
- ¿Angular está en `http://localhost:4200`?
- ¿Backend permite ese origin?
- ¿Backend permite `Authorization` header?

### 8.2 401 Unauthorized
- ¿Estás enviando `Authorization: Bearer <token>`?
- ¿El token está guardado y no está vacío?
- ¿El token ha expirado?

### 8.3 Forbidden (403)
- El token es válido, pero el usuario no tiene el rol requerido.
- Comprueba `roles` en la respuesta del login.

### 8.4 “Invalid Signature” en jwt.io
- Estás verificando con una clave o algoritmo diferente.
- Asegúrate de seleccionar HS256.
- Si la clave del backend está en Base64, usa la opción correcta de la herramienta.

---

## 9) Próximas mejoras (cuando registro/login/user estén OK)

- Añadir logout en navbar.
- Página `403`/`unauthorized`.
- Implementar refresh token con cookie HttpOnly (requiere cambios backend + `allowCredentials(true)` en CORS).
- Tests (unit) para store/guard.

---

## 10) Mini guía rápida (lo mínimo para que funcione)

1. Arranca el backend.
2. Crea el frontend con el comando de la sección 4.
3. Implementa `AuthService`, `AuthStore`, interceptor, guard y páginas.
4. Registra usuario en `/register`.
5. Haz login en `/login`.
6. Entra a `/user` y comprueba que carga el contenido protegido.

---

## 11) Plantilla “copiar/pegar” (código mínimo recomendado)

Esta sección te deja un **esqueleto funcional** (Angular moderno con componentes standalone) para que al crear el proyecto `frontend/` puedas copiar estos archivos tal cual.

> Asunción: backend en `http://localhost:8080` y frontend en `http://localhost:4200`.

### 11.1 Estructura de archivos
Crea (o ajusta) estas rutas dentro de `frontend/src/app/`:

- `app.routes.ts`
- `app.config.ts`
- `app.component.ts`
- `core/auth/auth.models.ts`
- `core/auth/auth.store.ts`
- `core/auth/auth.service.ts`
- `core/http/jwt.interceptor.ts`
- `core/guards/role.guard.ts`
- `features/auth/login.page.ts`
- `features/auth/register.page.ts`
- `features/user/user.page.ts`

Y en `frontend/src/environments/`:
- `environment.ts`

> Si tu Angular crea `environment.development.ts`, puedes duplicar ahí la misma configuración.

---

### 11.2 `src/environments/environment.ts`
```ts
export const environment = {
  apiBaseUrl: 'http://localhost:8080',
};
```

---

### 11.3 `src/app/app.config.ts`
```ts
import { ApplicationConfig } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { jwtInterceptor } from './core/http/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withFetch(), withInterceptors([jwtInterceptor])),
  ],
};
```

---

### 11.4 `src/app/app.routes.ts`
```ts
import { Routes } from '@angular/router';
import { LoginPage } from './features/auth/login.page';
import { RegisterPage } from './features/auth/register.page';
import { UserPage } from './features/user/user.page';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },

  {
    path: 'user',
    component: UserPage,
    canActivate: [roleGuard],
    data: { roles: ['ROLE_USER', 'USER'] },
  },

  { path: '**', redirectTo: 'login' },
];
```

---

### 11.5 `src/app/app.component.ts`
```ts
import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthStore } from './core/auth/auth.store';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, NgIf],
  template: `
    <nav style="display:flex; gap:12px; padding:12px; border-bottom:1px solid #ddd;">
      <a routerLink="/login">Login</a>
      <a routerLink="/register">Registro</a>
      <a routerLink="/user">User</a>

      <button *ngIf="auth.isAuthenticated()" (click)="logout()">Logout</button>
    </nav>

    <main style="padding:12px;">
      <router-outlet />
    </main>
  `,
})
export class AppComponent {
  readonly auth = inject(AuthStore);

  logout(): void {
    this.auth.logout();
  }
}
```

---

### 11.6 `src/app/core/auth/auth.models.ts`
```ts
export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  role?: string[];
}

export interface JwtResponse {
  id: number;
  username: string;
  email: string;
  roles: string[];
  tokenType: string;
  accessToken: string;
}
```

---

### 11.7 `src/app/core/auth/auth.store.ts`
```ts
import { Injectable, computed, signal } from '@angular/core';

const LS_TOKEN = 'accessToken';
const LS_ROLES = 'roles';
const LS_USERNAME = 'username';

function loadRoles(): string[] {
  const raw = localStorage.getItem(LS_ROLES);
  if (!raw) return [];
  try {
    return JSON.parse(raw) as string[];
  } catch {
    return [];
  }
}

@Injectable({ providedIn: 'root' })
export class AuthStore {
  private readonly tokenSig = signal<string | null>(localStorage.getItem(LS_TOKEN));
  private readonly rolesSig = signal<string[]>(loadRoles());
  private readonly usernameSig = signal<string | null>(localStorage.getItem(LS_USERNAME));

  readonly token = computed(() => this.tokenSig());
  readonly roles = computed(() => this.rolesSig());
  readonly username = computed(() => this.usernameSig());

  readonly isAuthenticated = computed(() => !!this.tokenSig());

  setSession(token: string, username: string, roles: string[]): void {
    localStorage.setItem(LS_TOKEN, token);
    localStorage.setItem(LS_USERNAME, username);
    localStorage.setItem(LS_ROLES, JSON.stringify(roles ?? []));

    this.tokenSig.set(token);
    this.usernameSig.set(username);
    this.rolesSig.set(roles ?? []);
  }

  logout(): void {
    localStorage.removeItem(LS_TOKEN);
    localStorage.removeItem(LS_USERNAME);
    localStorage.removeItem(LS_ROLES);

    this.tokenSig.set(null);
    this.usernameSig.set(null);
    this.rolesSig.set([]);
  }

  hasAnyRole(allowed: string[]): boolean {
    const mine = this.rolesSig();
    return allowed.some(r => mine.includes(r));
  }
}
```

---

### 11.8 `src/app/core/auth/auth.service.ts`
```ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { JwtResponse, LoginRequest, SignupRequest } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  register(body: SignupRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/api/auth/signup`, body);
  }

  login(body: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.baseUrl}/api/auth/signin`, body);
  }

  getUserContent(): Observable<string> {
    return this.http.get(`${this.baseUrl}/api/test/user`, { responseType: 'text' });
  }
}
```

---

### 11.9 `src/app/core/http/jwt.interceptor.ts`
```ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStore } from '../auth/auth.store';
import { environment } from '../../../environments/environment';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthStore);
  const token = auth.token();

  // Evita añadir token a URLs que no sean tu backend
  if (!token || !req.url.startsWith(environment.apiBaseUrl)) {
    return next(req);
  }

  return next(
    req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    })
  );
};
```

---

### 11.10 `src/app/core/guards/role.guard.ts`
```ts
import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthStore } from '../auth/auth.store';

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthStore);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    return router.parseUrl('/login');
  }

  const allowed = (route.data?.['roles'] as string[] | undefined) ?? [];
  if (allowed.length === 0) return true;

  if (!auth.hasAnyRole(allowed)) {
    return router.parseUrl('/login');
  }

  return true;
};
```

---

### 11.11 `src/app/features/auth/register.page.ts`
```ts
import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <h2>Registro</h2>

    <form [formGroup]="form" (ngSubmit)="submit()">
      <div>
        <label>Username</label><br />
        <input formControlName="username" />
      </div>

      <div>
        <label>Email</label><br />
        <input formControlName="email" />
      </div>

      <div>
        <label>Password</label><br />
        <input type="password" formControlName="password" />
      </div>

      <div style="margin-top:8px;">
        <button type="submit" [disabled]="busy() || form.invalid">Crear cuenta</button>
        <a style="margin-left:12px;" routerLink="/login">Ir a login</a>
      </div>

      <p *ngIf="error()" style="color:#b00020;">{{ error() }}</p>
      <p *ngIf="done()" style="color:#0a7a0a;">Registro OK. Ya puedes hacer login.</p>
    </form>
  `,
})
export class RegisterPage {
  private readonly api = inject(AuthService);
  private readonly router = inject(Router);

  readonly busy = signal(false);
  readonly done = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(6)] }),
  });

  submit(): void {
    if (this.form.invalid || this.busy()) return;

    this.busy.set(true);
    this.error.set(null);
    this.done.set(false);

    const { username, email, password } = this.form.getRawValue();

    // Si tu backend asigna ROLE_USER por defecto, puedes quitar role.
    this.api.register({ username, email, password, role: ['user'] }).subscribe({
      next: () => {
        this.done.set(true);
        this.busy.set(false);
        this.router.navigateByUrl('/login');
      },
      error: (e) => {
        this.error.set(String(e?.error?.message ?? 'Error registrando usuario'));
        this.busy.set(false);
      },
    });
  }
}
```

---

### 11.12 `src/app/features/auth/login.page.ts`
```ts
import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { AuthStore } from '../../core/auth/auth.store';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <h2>Login</h2>

    <form [formGroup]="form" (ngSubmit)="submit()">
      <div>
        <label>Username</label><br />
        <input formControlName="username" />
      </div>

      <div>
        <label>Password</label><br />
        <input type="password" formControlName="password" />
      </div>

      <div style="margin-top:8px;">
        <button type="submit" [disabled]="busy() || form.invalid">Entrar</button>
        <a style="margin-left:12px;" routerLink="/register">Crear cuenta</a>
      </div>

      <p *ngIf="error()" style="color:#b00020;">{{ error() }}</p>
    </form>
  `,
})
export class LoginPage {
  private readonly api = inject(AuthService);
  private readonly auth = inject(AuthStore);
  private readonly router = inject(Router);

  readonly busy = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  submit(): void {
    if (this.form.invalid || this.busy()) return;

    this.busy.set(true);
    this.error.set(null);

    const { username, password } = this.form.getRawValue();

    this.api.login({ username, password }).subscribe({
      next: (jwt) => {
        this.auth.setSession(jwt.accessToken, jwt.username, jwt.roles ?? []);
        this.busy.set(false);
        this.router.navigateByUrl('/user');
      },
      error: (e) => {
        this.error.set(String(e?.error?.message ?? 'Credenciales inválidas'));
        this.busy.set(false);
      },
    });
  }
}
```

---

### 11.13 `src/app/features/user/user.page.ts`
```ts
import { Component, inject, signal } from '@angular/core';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/auth/auth.service';
import { AuthStore } from '../../core/auth/auth.store';

@Component({
  standalone: true,
  imports: [NgIf],
  template: `
    <h2>Zona USER</h2>

    <p>Usuario: <strong>{{ auth.username() }}</strong></p>
    <p>Roles: <code>{{ auth.roles().join(', ') }}</code></p>

    <button (click)="load()" [disabled]="busy()">Cargar contenido protegido</button>

    <p *ngIf="busy()">Cargando...</p>
    <pre *ngIf="content()">{{ content() }}</pre>
    <p *ngIf="error()" style="color:#b00020;">{{ error() }}</p>
  `,
})
export class UserPage {
  private readonly api = inject(AuthService);
  readonly auth = inject(AuthStore);

  readonly busy = signal(false);
  readonly content = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  load(): void {
    this.busy.set(true);
    this.error.set(null);
    this.content.set(null);

    this.api.getUserContent().subscribe({
      next: (txt) => {
        this.content.set(txt);
        this.busy.set(false);
      },
      error: (e) => {
        this.error.set(String(e?.error ?? 'No autorizado / error de servidor'));
        this.busy.set(false);
      },
    });
  }
}
```

---

## 12) Prueba manual (paso a paso)

1. **Backend**: arranca Spring Boot (`mvn spring-boot:run`).
2. **Frontend**: crea el proyecto `frontend/` (sección 4) y pega los ficheros de la sección 11.
3. Ejecuta el frontend (`npm start`) y abre `http://localhost:4200`.
4. Ve a `/register` y crea un usuario.
5. Ve a `/login`, inicia sesión.
6. Entra a `/user`:
   - debe dejarte entrar por guard si tus roles incluyen `ROLE_USER`.
   - pulsa “Cargar contenido protegido” y debe llamar a `GET /api/test/user`.

---

## 13) Si te falla el rol (muy común)

Si tras login el backend devuelve roles como `ROLE_USER`, el guard funcionará.
Si devuelve solo `USER`, también.

Si no te deja entrar:
- Mira qué te llega en `jwt.roles` cuando haces login.
- Ajusta `data: { roles: [...] }` en la ruta `/user`.
