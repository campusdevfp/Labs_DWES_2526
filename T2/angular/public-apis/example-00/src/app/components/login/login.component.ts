import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="login-container">
      <div class="login-card">
        <div class="jwt-badge">🔐 Autenticación JWT</div>
        <h1>Iniciar Sesión</h1>

        @if (errorMessage()) {
          <div class="error-message">{{ errorMessage() }}</div>
        }

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="email">Email</label>
            <input
              type="email"
              id="email"
              formControlName="email"
              [class.invalid]="loginForm.get('email')?.invalid && loginForm.get('email')?.touched"
            />
            @if (loginForm.get('email')?.invalid && loginForm.get('email')?.touched) {
              <span class="error">Email inválido</span>
            }
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <input
              type="password"
              id="password"
              formControlName="password"
              [class.invalid]="
                loginForm.get('password')?.invalid && loginForm.get('password')?.touched
              "
            />
            @if (loginForm.get('password')?.invalid && loginForm.get('password')?.touched) {
              <span class="error">La contraseña es requerida</span>
            }
          </div>

          <button
            type="submit"
            [disabled]="loginForm.invalid || authService.isLoading()"
            class="btn-primary"
          >
            @if (authService.isLoading()) {
              <span>Cargando...</span>
            } @else {
              <span>Iniciar Sesión</span>
            }
          </button>
        </form>

        <div class="register-link">
          ¿No tienes cuenta?
          <a routerLink="/register">Regístrate aquí</a>
        </div>

        <div class="demo-credentials">
          <p><strong>Credenciales de prueba:</strong></p>
          <p>Email: john&#64;mail.com</p>
          <p>Contraseña: changeme</p>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .login-container {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 100vh;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        padding: 1rem;
      }

      .login-card {
        background: white;
        padding: 2rem;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        width: 100%;
        max-width: 400px;
      }

      .jwt-badge {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 0.75rem;
        border-radius: 6px;
        text-align: center;
        font-weight: 600;
        font-size: 0.9rem;
        margin-bottom: 1.5rem;
      }

      h1 {
        margin: 0 0 1.5rem;
        color: #333;
        text-align: center;
      }

      .form-group {
        margin-bottom: 1.5rem;
      }

      label {
        display: block;
        margin-bottom: 0.5rem;
        color: #555;
        font-weight: 500;
      }

      input {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid #ddd;
        border-radius: 4px;
        font-size: 1rem;
        transition: border-color 0.3s;
        box-sizing: border-box;
      }

      input:focus {
        outline: none;
        border-color: #667eea;
      }

      input.invalid {
        border-color: #e74c3c;
      }

      .error {
        color: #e74c3c;
        font-size: 0.875rem;
        margin-top: 0.25rem;
        display: block;
      }

      .error-message {
        background: #fee;
        color: #c33;
        padding: 0.75rem;
        border-radius: 4px;
        margin-bottom: 1rem;
        text-align: center;
      }

      .btn-primary {
        width: 100%;
        padding: 0.75rem;
        background: #667eea;
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        transition: background 0.3s;
      }

      .btn-primary:hover:not(:disabled) {
        background: #5568d3;
      }

      .btn-primary:disabled {
        background: #ccc;
        cursor: not-allowed;
      }

      .register-link {
        text-align: center;
        margin-top: 1.5rem;
        color: #666;
      }

      .register-link a {
        color: #667eea;
        text-decoration: none;
        font-weight: 600;
      }

      .register-link a:hover {
        text-decoration: underline;
      }

      .demo-credentials {
        margin-top: 1.5rem;
        padding: 1rem;
        background: #f8f9fa;
        border-radius: 4px;
        font-size: 0.875rem;
        color: #666;
      }

      .demo-credentials p {
        margin: 0.25rem 0;
      }
    `,
  ],
})
export class LoginComponent {
  readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);

  errorMessage = signal<string>('');

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(3)]],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.errorMessage.set('');
    const credentials = this.loginForm.getRawValue();

    this.authService.login(credentials).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Login error:', error);
        this.errorMessage.set('Credenciales inválidas. Por favor, intenta de nuevo.');
      },
    });
  }
}
