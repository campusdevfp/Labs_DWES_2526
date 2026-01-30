import { Component, inject, signal, effect, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  website: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>Lista de Usuarios</h1>

    @if (isLoading()) {
      <p>Cargando usuarios...</p>
    } @else if (error()) {
      <p style="color: red;">Error: {{ error() }}</p>
      <button (click)="loadUsers()">Reintentar</button>
    } @else {
      <div class="users-grid">
        @for (user of users(); track user.id) {
          <div class="user-card">
            <h3>{{ user.name }}</h3>
            <p><strong>Email:</strong> {{ user.email }}</p>
            <p><strong>Teléfono:</strong> {{ user.phone }}</p>
            <p><strong>Sitio:</strong> {{ user.website }}</p>
          </div>
        }
      </div>
    }
  `,
  styles: [
    `
      .users-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        gap: 20px;
        margin-top: 20px;
      }
      .user-card {
        border: 1px solid #ddd;
        padding: 15px;
        border-radius: 8px;
        background: #f9f9f9;
      }
    `,
  ],
})
export class UsersComponent implements OnInit {
  http = inject(HttpClient);

  users = signal<User[]>([]);
  isLoading = signal(false);
  error = signal<string | null>(null);

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading.set(true);
    this.error.set(null);

    this.http.get<User[]>('https://jsonplaceholder.typicode.com/users').subscribe({
      next: (data) => {
        this.users.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set('No se pudieron cargar los usuarios. Intenta de nuevo.');
        this.isLoading.set(false);
      },
    });
  }
}
