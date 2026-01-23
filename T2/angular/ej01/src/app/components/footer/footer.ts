import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `
    <footer class="footer">
      <p>© {{ anio }} - Curso Angular 21 Moderno - Abacus AI</p>
    </footer>
  `,
  styles: [
    `
      .footer {
        background: #2c3e50;
        color: #bdc3c7;
        text-align: center;
        padding: 15px;
        position: relative;
        width: 100%;
        margin-top: auto;
      }
    `,
  ],
})
export class FooterComponent {
  anio = new Date().getFullYear();
}
