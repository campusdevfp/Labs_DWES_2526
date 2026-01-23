import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';
import { AuthStore } from '../../core/auth/auth.store';

@Component({
  standalone: true,
  imports: [],
  template: `
    <h2>Zona USER</h2>

    <p>
      Usuario: <strong>{{ auth.username() }}</strong>
    </p>
    <p>
      Roles: <code>{{ auth.roles().join(', ') }}</code>
    </p>

    <button (click)="load()" [disabled]="busy()">Cargar contenido protegido</button>

    @if (busy()) {
      <p>Cargando...</p>
    }

    @if (content()) {
      <pre>{{ content() }}</pre>
    }

    @if (error()) {
      <p style="color:#b00020;">{{ error() }}</p>
    }
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
