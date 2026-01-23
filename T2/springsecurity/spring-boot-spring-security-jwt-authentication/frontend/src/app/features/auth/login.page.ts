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

      @if (error()) {
        <p style="color:#b00020;">{{ error() }}</p>
      }
    </form>
  `,
})
export class LoginPage {
  private readonly api = inject(AuthService);
  private readonly auth = inject<AuthStore>(AuthStore);
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
