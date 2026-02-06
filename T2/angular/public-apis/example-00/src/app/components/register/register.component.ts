import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="register-container">
      <div class="register-card">
        <h1>Crear Cuenta</h1>

        @if (errorMessage()) {
          <div class="error-message">{{ errorMessage() }}</div>
        }

        @if (successMessage()) {
          <div class="success-message">{{ successMessage() }}</div>
        }

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="name">Nombre completo</label>
            <input
              type="text"
              id="name"
              formControlName="name"
              [class.invalid]="
                registerForm.get('name')?.invalid && registerForm.get('name')?.touched
              "
            />
            @if (registerForm.get('name')?.invalid && registerForm.get('name')?.touched) {
              <span class="error">El nombre es requerido (mínimo 3 caracteres)</span>
            }
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input
              type="email"
              id="email"
              formControlName="email"
              [class.invalid]="
                registerForm.get('email')?.invalid && registerForm.get('email')?.touched
              "
            />
            @if (
              registerForm.get('email')?.hasError('required') && registerForm.get('email')?.touched
            ) {
              <span class="error">El email es requerido</span>
            }
            @if (
              registerForm.get('email')?.hasError('email') && registerForm.get('email')?.touched
            ) {
              <span class="error">Email inválido</span>
            }
            @if (registerForm.get('email')?.hasError('emailTaken')) {
              <span class="error">Este email ya está registrado</span>
            }
            @if (emailChecking()) {
              <span class="info">Verificando disponibilidad...</span>
            }
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <input
              type="password"
              id="password"
              formControlName="password"
              [class.invalid]="
                registerForm.get('password')?.invalid && registerForm.get('password')?.touched
              "
            />
            @if (registerForm.get('password')?.invalid && registerForm.get('password')?.touched) {
              <span class="error">La contraseña debe tener al menos 6 caracteres</span>
            }
          </div>

          <div class="form-group">
            <label for="confirmPassword">Confirmar contraseña</label>
            <input
              type="password"
              id="confirmPassword"
              formControlName="confirmPassword"
              [class.invalid]="
                registerForm.get('confirmPassword')?.invalid &&
                registerForm.get('confirmPassword')?.touched
              "
            />
            @if (
              registerForm.hasError('passwordMismatch') &&
              registerForm.get('confirmPassword')?.touched
            ) {
              <span class="error">Las contraseñas no coinciden</span>
            }
          </div>

          <div class="form-group">
            <label for="avatar">URL del Avatar (opcional)</label>
            <input
              type="url"
              id="avatar"
              formControlName="avatar"
              placeholder="https://example.com/avatar.jpg"
            />
          </div>

          <button
            type="submit"
            [disabled]="registerForm.invalid || authService.isLoading()"
            class="btn-primary"
          >
            @if (authService.isLoading()) {
              <span>Creando cuenta...</span>
            } @else {
              <span>Registrarse</span>
            }
          </button>
        </form>

        <div class="login-link">
          ¿Ya tienes cuenta?
          <a routerLink="/login">Inicia sesión aquí</a>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .register-container {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 100vh;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        padding: 1rem;
      }

      .register-card {
        background: white;
        padding: 2rem;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        width: 100%;
        max-width: 400px;
        margin: 2rem 0;
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

      .info {
        color: #3498db;
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

      .success-message {
        background: #efe;
        color: #3c3;
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

      .login-link {
        text-align: center;
        margin-top: 1.5rem;
        color: #666;
      }

      .login-link a {
        color: #667eea;
        text-decoration: none;
        font-weight: 600;
      }

      .login-link a:hover {
        text-decoration: underline;
      }
    `,
  ],
})
export class RegisterComponent {
  readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);

  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  emailChecking = signal<boolean>(false);

  registerForm = this.fb.nonNullable.group(
    {
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      avatar: [''],
    },
    { validators: this.passwordMatchValidator },
  );

  constructor() {
    // Check email availability on change
    this.registerForm
      .get('email')
      ?.valueChanges.pipe(
        debounceTime(500),
        distinctUntilChanged(),
        switchMap((email) => {
          if (email && this.registerForm.get('email')?.valid) {
            this.emailChecking.set(true);
            return this.authService.checkEmailAvailability(email);
          }
          return [];
        }),
      )
      .subscribe({
        next: (result) => {
          this.emailChecking.set(false);
          if (!result.isAvailable) {
            this.registerForm.get('email')?.setErrors({ emailTaken: true });
          }
        },
        error: () => {
          this.emailChecking.set(false);
        },
      });
  }

  passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    return password.value === confirmPassword.value ? null : { passwordMismatch: true };
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.errorMessage.set('');
    this.successMessage.set('');

    const formData = this.registerForm.getRawValue();
    const { confirmPassword, ...registerData } = formData;

    this.authService.register(registerData).subscribe({
      next: () => {
        this.successMessage.set('¡Cuenta creada exitosamente! Iniciando sesión...');

        // Auto-login after successful registration
        this.authService
          .login({
            email: formData.email,
            password: formData.password,
          })
          .subscribe({
            next: () => {
              this.router.navigate(['/home']);
            },
            error: (loginError) => {
              console.error('Auto-login error:', loginError);
              // If auto-login fails, redirect to login page
              setTimeout(() => {
                this.router.navigate(['/login']);
              }, 1500);
            },
          });
      },
      error: (error) => {
        console.error('Register error:', error);
        let errorMsg = 'Error al crear la cuenta. Por favor, intenta de nuevo.';

        // Extract error message from API response
        if (error.error && error.error.message) {
          if (Array.isArray(error.error.message)) {
            errorMsg = error.error.message.join(', ');
          } else {
            errorMsg = error.error.message;
          }
        }

        this.errorMessage.set(errorMsg);
      },
    });
  }
}
