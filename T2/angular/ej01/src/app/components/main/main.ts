import { Component, input } from '@angular/core';

@Component({
  selector: 'app-main',
  standalone: true,
  template: `
    <main class="main-container">
      <div class="card">
        <h2>Contenido Dinámico</h2>
        <p class="text">{{ contenido() }}</p>
      </div>
    </main>
  `,
  styles: [
    `
      .main-container {
        padding: 50px 20px;
        display: flex;
        justify-content: center;
        background: #f4f7f6;
        min-height: 60vh;
      }
      .card {
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        max-width: 600px;
        width: 100%;
        text-align: center;
      }
      .text {
        font-size: 1.2rem;
        color: #555;
        line-height: 1.6;
      }
    `,
  ],
})
export class MainComponent {
  contenido = input.required<string>();
}
