import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  template: `
    <div class="home-container">
      <div class="protected-badge">🔒 Zona Protegida - Acceso Autorizado</div>

      <div class="header">
        <h1>Panel de Usuario</h1>
        <p class="subtitle">Autenticado con JWT</p>
      </div>

      @if (authService.currentUser(); as user) {
        <div class="user-card">
          <img [src]="user.avatar" [alt]="user.name" class="avatar" />
          <div class="user-info">
            <h2>{{ user.name }}</h2>
            <p class="email">{{ user.email }}</p>
            <span class="role" [class.admin]="user.role === 'admin'">
              {{ user.role === 'admin' ? 'Administrador' : 'Cliente' }}
            </span>
            <p class="user-id">ID de Usuario: #{{ user.id }}</p>
          </div>
        </div>

        <div class="info-section">
          <h3>🔐 Información de Autenticación JWT</h3>
          <div class="auth-details">
            <div class="detail-item">
              <strong>Estado:</strong>
              <span class="status-active">✅ Sesión Activa</span>
            </div>
            <div class="detail-item">
              <strong>Tipo:</strong>
              <span>Bearer Token (JWT)</span>
            </div>
            <div class="detail-item">
              <strong>Access Token:</strong>
              <code class="token">{{ getTokenPreview() }}</code>
            </div>
            <div class="detail-item">
              <strong>Endpoint Protegido:</strong>
              <span class="protected-endpoint">GET /api/v1/auth/profile</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h3>📋 Datos del Perfil (Obtenidos con Token)</h3>
          <div class="profile-data">
            <div class="data-row">
              <span class="label">Nombre:</span>
              <span class="value">{{ user.name }}</span>
            </div>
            <div class="data-row">
              <span class="label">Email:</span>
              <span class="value">{{ user.email }}</span>
            </div>
            <div class="data-row">
              <span class="label">Rol:</span>
              <span class="value">{{ user.role }}</span>
            </div>
            <div class="data-row">
              <span class="label">ID:</span>
              <span class="value">{{ user.id }}</span>
            </div>
          </div>
          <p class="api-note">
            ℹ️ Estos datos se obtuvieron llamando al endpoint protegido
            <code>/auth/profile</code> con tu token JWT
          </p>
        </div>
      } @else {
        <div class="loading">Cargando perfil de usuario...</div>
      }
    </div>
  `,
  styles: [
    `
      .home-container {
        max-width: 800px;
        margin: 0 auto;
        padding: 2rem;
      }

      .protected-badge {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 1rem;
        border-radius: 8px;
        text-align: center;
        font-weight: 700;
        font-size: 1.1rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 6px rgba(102, 126, 234, 0.3);
      }

      .header {
        margin-bottom: 2rem;
        padding-bottom: 1rem;
        border-bottom: 2px solid #667eea;
      }

      h1 {
        margin: 0 0 0.5rem;
        color: #333;
      }

      .subtitle {
        margin: 0;
        color: #667eea;
        font-size: 1rem;
        font-weight: 500;
      }

      .user-card {
        display: flex;
        gap: 2rem;
        padding: 2rem;
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        margin-bottom: 2rem;
      }

      .avatar {
        width: 120px;
        height: 120px;
        border-radius: 50%;
        object-fit: cover;
        border: 4px solid #667eea;
      }

      .user-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
      }

      .user-info h2 {
        margin: 0 0 0.5rem;
        color: #333;
      }

      .email {
        margin: 0 0 1rem;
        color: #666;
        font-size: 1.1rem;
      }

      .user-id {
        margin: 0.5rem 0 0;
        color: #999;
        font-size: 0.9rem;
      }

      .role {
        display: inline-block;
        padding: 0.5rem 1rem;
        background: #3498db;
        color: white;
        border-radius: 20px;
        font-size: 0.875rem;
        font-weight: 600;
        text-transform: uppercase;
        width: fit-content;
      }

      .role.admin {
        background: #e67e22;
      }

      .info-section {
        background: white;
        padding: 2rem;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        margin-bottom: 2rem;
      }

      .info-section h3 {
        margin: 0 0 1.5rem;
        color: #333;
      }

      .auth-details {
        display: flex;
        flex-direction: column;
        gap: 1rem;
      }

      .detail-item {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        padding: 1rem;
        background: #f8f9fa;
        border-radius: 6px;
        border-left: 4px solid #667eea;
      }

      .detail-item strong {
        color: #555;
        font-size: 0.875rem;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }

      .status-active {
        color: #27ae60;
        font-weight: 600;
      }

      .token {
        background: #2c3e50;
        color: #2ecc71;
        padding: 0.75rem;
        border-radius: 4px;
        font-family: 'Courier New', monospace;
        font-size: 0.85rem;
        word-break: break-all;
        display: block;
        overflow-x: auto;
      }

      .protected-endpoint {
        color: #e67e22;
        font-weight: 600;
        font-family: monospace;
      }

      .profile-data {
        display: flex;
        flex-direction: column;
        gap: 1rem;
      }

      .data-row {
        display: flex;
        justify-content: space-between;
        padding: 0.75rem;
        background: #f8f9fa;
        border-radius: 4px;
      }

      .data-row .label {
        font-weight: 600;
        color: #555;
      }

      .data-row .value {
        color: #333;
      }

      .api-note {
        margin-top: 1.5rem;
        padding: 1rem;
        background: #e3f2fd;
        border-left: 4px solid #2196f3;
        border-radius: 4px;
        color: #1565c0;
        font-size: 0.9rem;
      }

      .api-note code {
        background: #bbdefb;
        padding: 0.2rem 0.5rem;
        border-radius: 3px;
        font-family: monospace;
      }

      .loading {
        text-align: center;
        padding: 3rem;
        color: #666;
        font-size: 1.2rem;
      }
    `,
  ],
})
export class HomeComponent {
  readonly authService = inject(AuthService);

  getTokenPreview(): string {
    const token = this.authService.getAccessToken();
    if (!token) {
      return 'No token available';
    }

    if (token.length > 50) {
      const start = token.substring(0, 30);
      const end = token.substring(token.length - 20);
      return start + '...' + end;
    }
    return token;
  }
}
