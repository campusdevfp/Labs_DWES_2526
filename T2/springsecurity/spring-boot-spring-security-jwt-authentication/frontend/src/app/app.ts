import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthStore } from './core/auth/auth.store';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <nav style="display:flex; gap:12px; padding:12px; border-bottom:1px solid #ddd;">
      <a routerLink="/login">Login</a>
      <a routerLink="/register">Registro</a>
      <a routerLink="/user">User</a>

      @if (auth.isAuthenticated()) {
        <button (click)="logout()">Logout</button>
      }
    </nav>

    <main style="padding:12px;">
      <router-outlet />
    </main>
  `,
})
export class AppComponent {
  readonly auth = inject<AuthStore>(AuthStore);

  logout(): void {
    this.auth.logout();
  }
}
