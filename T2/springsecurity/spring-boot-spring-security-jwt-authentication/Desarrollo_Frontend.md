### Descripción breve
Se crea un frontend Angular \`v21\` **standalone** (sin NgModules), usando **signals**, **Signal Forms** (forms tipados), **HttpClient** con \`fetch\`, **interceptor funcional** para adjuntar el JWT, **guard** por rol y 3 pantallas: **registro**, **login** y **/user** (protegida por rol \`ROLE\_USER\`/\`USER\`, según el backend).

A continuación va el scaffold mínimo de archivos.

---

### 1) Dependencias y config base

```json
// package.json (fragmento)
{
  "dependencies": {
    "@angular/animations": "^21.0.0",
    "@angular/common": "^21.0.0",
    "@angular/compiler": "^21.0.0",
    "@angular/core": "^21.0.0",
    "@angular/forms": "^21.0.0",
    "@angular/platform-browser": "^21.0.0",
    "@angular/platform-browser-dynamic": "^21.0.0",
    "@angular/router": "^21.0.0",
    "rxjs": "^7.8.0"
  }
}
```

```typescript
// src/main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

bootstrapApplication(AppComponent, {
  providers: [...appConfig.providers, provideAnimations()],
}).catch(console.error);
```

```typescript
// src/app/app.config.ts
import { ApplicationConfig } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './core/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
  ],
};
```

```typescript
// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginPageComponent } from './features/auth/login-page.component';
import { RegisterPageComponent } from './features/auth/register-page.component';
import { UserPageComponent } from './features/user/user-page.component';
import { roleGuard } from './core/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  { path: 'register', component: RegisterPageComponent },
  { path: 'login', component: LoginPageComponent },

  {
    path: 'user',
    component: UserPageComponent,
    canActivate: [roleGuard],
    data: { roles: ['USER', 'ROLE_USER'] },
  },

  { path: '**', redirectTo: 'login' },
];
```

```typescript
// src/app/app.component.ts
import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthStore } from './core/auth.store';

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

### 2) Config de entorno (URL del backend Spring)

```typescript
// src/environments/environment.ts
export const environment = {
  apiBaseUrl: 'http://localhost:8080',
};
```

---

### 3) Modelos (request/response típicos del backend bezkoder)

```typescript
// src/app/core/auth.models.ts
export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  role?: string[]; // ejemplo: ["user"]
}

export interface JwtResponse {
  id: number;
  username: string;
  email: string;
  roles: string[]; // ejemplo: ["ROLE_USER"]
  tokenType: string; // "Bearer"
  accessToken: string;
}
```

---

### 4) Servicio Auth + Store con signals

```typescript
// src/app/core/storage.ts
export const storageKeys = {
  token: 'accessToken',
  roles: 'roles',
  username: 'username',
} as const;

export function loadToken(): string | null {
  return localStorage.getItem(storageKeys.token);
}

export function saveSession(token: string, roles: string[], username: string): void {
  localStorage.setItem(storageKeys.token, token);
  localStorage.setItem(storageKeys.roles, JSON.stringify(roles));
  localStorage.setItem(storageKeys.username, username);
}

export function clearSession(): void {
  localStorage.removeItem(storageKeys.token);
  localStorage.removeItem(storageKeys.roles);
  localStorage.removeItem(storageKeys.username);
}

export function loadRoles(): string[] {
  const raw = localStorage.getItem(storageKeys.roles);
  if (!raw) return [];
  try {
    return JSON.parse(raw) as string[];
  } catch {
    return [];
  }
}
```

```typescript
// src/app/core/auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { JwtResponse, LoginRequest, SignupRequest } from './auth.models';
import { Observable } from 'rxjs';

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

```typescript
// src/app/core/auth.store.ts
import { Injectable, computed, signal } from '@angular/core';
import { clearSession, loadRoles, loadToken, saveSession } from './storage';
import { JwtResponse } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthStore {
  private readonly tokenSig = signal<string | null>(loadToken());
  private readonly rolesSig = signal<string[]>(loadRoles());
  private readonly usernameSig = signal<string | null>(localStorage.getItem('username'));

  readonly token = computed(() => this.tokenSig());
  readonly roles = computed(() => this.rolesSig());
  readonly username = computed(() => this.usernameSig());

  readonly isAuthenticated = computed(() => !!this.tokenSig());

  setSession(jwt: JwtResponse): void {
    saveSession(jwt.accessToken, jwt.roles ?? [], jwt.username ?? '');
    this.tokenSig.set(jwt.accessToken);
    this.rolesSig.set(jwt.roles ?? []);
    this.usernameSig.set(jwt.username ?? null);
  }

  logout(): void {
    clearSession();
    this.tokenSig.set(null);
    this.rolesSig.set([]);
    this.usernameSig.set(null);
  }

  hasAnyRole(allowed: string[]): boolean {
    const userRoles = this.rolesSig();
    return allowed.some((r) => userRoles.includes(r));
  }
}
```

---

### 5) Interceptor funcional (Bearer token)

```typescript
// src/app/core/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStore } from './auth.store';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthStore);
  const token = auth.token();

  if (!token) return next(req);

  const cloned = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });

  return next(cloned);
};
```

---

### 6) Guard por rol (para \`/user\`)

```typescript
// src/app/core/role.guard.ts
import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthStore } from './auth.store';

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthStore);
  const router = inject(Router);

  const allowed = (route.data?.['roles'] as string[] | undefined) ?? [];

  if (!auth.isAuthenticated()) {
    return router.parseUrl('/login');
  }

  if (allowed.length === 0) {
    return true;
  }

  if (!auth.hasAnyRole(allowed)) {
    return router.parseUrl('/login');
  }

  return true;
};
```

---

### 7) Registro (standalone + Signal Forms)

```typescript
// src/app/features/auth/register-page.component.ts
import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

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
export class RegisterPageComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly busy = signal(false);
  readonly error = signal<string | null>(null);
  readonly done = signal(false);

  readonly form = new FormGroup({
    username: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    email: new FormControl<string>('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    password: new FormControl<string>('', { nonNullable: true, validators: [Validators.required, Validators.minLength(6)] }),
  });

  submit(): void {
    if (this.form.invalid || this.busy()) return;

    this.error.set(null);
    this.done.set(false);
    this.busy.set(true);

    const { username, email, password } = this.form.getRawValue();

    // Para bezkoder suele ser role:["user"] opcional; si tu backend lo requiere, se envía aquí.
    this.authService.register({ username, email, password, role: ['user'] }).subscribe({
      next: () => {
        this.done.set(true);
        this.busy.set(false);
        // opcional: redirigir a login
        this.router.navigateByUrl('/login');
      },
      error: (e) => {
        const msg = e?.error?.message ?? 'Error registrando usuario';
        this.error.set(String(msg));
        this.busy.set(false);
      },
    });
  }
}
```

---

### 8) Login (guardar JWT + redirigir a \`/user\`)

```typescript
// src/app/features/auth/login-page.component.ts
import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { AuthStore } from '../../core/auth.store';

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
export class LoginPageComponent {
  private readonly authService = inject(AuthService);
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);

  readonly busy = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = new FormGroup({
    username: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
  });

  submit(): void {
    if (this.form.invalid || this.busy()) return;

    this.error.set(null);
    this.busy.set(true);

    const { username, password } = this.form.getRawValue();

    this.authService.login({ username, password }).subscribe({
      next: (jwt) => {
        this.authStore.setSession(jwt);
        this.busy.set(false);
        this.router.navigateByUrl('/user');
      },
      error: (e) => {
        const msg = e?.error?.message ?? 'Credenciales inválidas';
        this.error.set(String(msg));
        this.busy.set(false);
      },
    });
  }
}
```

---

### 9) Pantalla \`/user\` (llama al backend \`/api/test/user\`)

```typescript
// src/app/features/user/user-page.component.ts
import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { NgIf } from '@angular/common';
import { AuthStore } from '../../core/auth.store';

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
export class UserPageComponent {
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
        const msg = e?.error?.message ?? 'No autorizado o error de servidor';
        this.error.set(String(msg));
        this.busy.set(false);
      },
    });
  }
}
```

---

### 10) Notas de rol (compatibilidad)
El backend bezkoder normalmente devuelve roles como \`["ROLE_USER"]\`. El guard acepta \`USER\` y \`ROLE\_USER\` a la vez para evitar desajustes. Si tu backend devuelve solo \`USER\`, también funciona.

---