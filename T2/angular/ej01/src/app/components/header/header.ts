import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: true,
  template: `
    <header class="header">
      <h1>🌐 {{ titulo() }}</h1>
      <nav>
        <button (click)="seleccionar('inicio')">Inicio</button>
        <button (click)="seleccionar('acerca')">Acerca de</button>
        <button (click)="seleccionar('contacto')">Contacto</button>
      </nav>
    </header>
  `,
  styles: [
    `
      .header {
        background: #2c3e50;
        color: white;
        padding: 20px;
        text-align: center;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
      }
      nav {
        margin-top: 15px;
      }
      button {
        margin: 0 10px;
        padding: 10px 20px;
        cursor: pointer;
        border-radius: 5px;
        border: none;
        background: #34495e;
        color: white;
        transition: background 0.3s;
      }
      button:hover {
        background: #1abc9c;
      }
    `,
  ],
})
export class HeaderComponent {
  titulo = input.required<string>();
  onMenuSelect = output<string>();

  seleccionar(opcion: string) {
    this.onMenuSelect.emit(opcion);
  }
}
