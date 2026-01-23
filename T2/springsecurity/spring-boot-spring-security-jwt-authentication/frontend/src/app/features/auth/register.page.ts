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

      @if (error()) {
        <p style="color:#b00020;">{{ error() }}</p>
      }

      @if (done()) {
        <p style="color:#0a7a0a;">Registro OK. Ya puedes hacer login.</p>
      }
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
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(6)],
    }),
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
      error: (e: any) => {
        this.error.set(String(e?.error?.message ?? 'Error registrando usuario'));
        this.busy.set(false);
      },
    });
  }
}
