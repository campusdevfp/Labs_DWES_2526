import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar">
      <div class="nav-container">
        <div class="nav-brand">
          <a routerLink="/">
            <span class="logo">🔐</span>
            <span class="brand-name">Auth App</span>
          </a>
        </div>

        <div class="nav-links">
          @if (authService.isAuthenticated()) {
            <a routerLink="/home" routerLinkActive="active" class="nav-link"> Inicio </a>
            <div class="user-menu">
              <span class="user-name">{{ authService.currentUser()?.name }}</span>
              <button (click)="onLogout()" class="btn-logout">Cerrar Sesión</button>
            </div>
          } @else {
            <a routerLink="/login" routerLinkActive="active" class="nav-link"> Iniciar Sesión </a>
            <a routerLink="/register" routerLinkActive="active" class="nav-link btn-primary">
              Registrarse
            </a>
          }
        </div>
      </div>
    </nav>
  `,
  styles: [
    `
      .navbar {
        background: white;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        position: sticky;
        top: 0;
        z-index: 100;
        padding: 1rem 0;
      }

      .nav-container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 2rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .nav-brand a {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        text-decoration: none;
        color: #333;
        font-size: 1.25rem;
        font-weight: 700;
      }

      .logo {
        font-size: 1.5rem;
      }

      .brand-name {
        color: #667eea;
      }

      .nav-links {
        display: flex;
        align-items: center;
        gap: 1.5rem;
      }

      .nav-link {
        text-decoration: none;
        color: #555;
        font-weight: 500;
        padding: 0.5rem 1rem;
        border-radius: 4px;
        transition: all 0.3s;
      }

      .nav-link:hover {
        color: #667eea;
        background: #f0f0f0;
      }

      .nav-link.active {
        color: #667eea;
        background: #f0f0f0;
      }

      .nav-link.btn-primary {
        background: #667eea;
        color: white;
      }

      .nav-link.btn-primary:hover {
        background: #5568d3;
      }

      .user-menu {
        display: flex;
        align-items: center;
        gap: 1rem;
      }

      .user-name {
        color: #555;
        font-weight: 500;
      }

      .btn-logout {
        padding: 0.5rem 1rem;
        background: #e74c3c;
        color: white;
        border: none;
        border-radius: 4px;
        font-weight: 600;
        cursor: pointer;
        transition: background 0.3s;
      }

      .btn-logout:hover {
        background: #c0392b;
      }

      @media (max-width: 768px) {
        .nav-container {
          padding: 0 1rem;
        }

        .nav-links {
          gap: 0.5rem;
        }

        .user-name {
          display: none;
        }
      }
    `,
  ],
})
export class NavbarComponent {
  readonly authService = inject(AuthService);

  onLogout(): void {
    this.authService.logout();
  }
}
