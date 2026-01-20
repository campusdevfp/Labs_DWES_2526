import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-card',
  imports: [CommonModule],
  templateUrl: './user-card.html',
  styleUrl: './user-card.scss',
})
export class UserCard {
  name = input.required<string>();
  age = input<number>(0); // Input opcional con valor por defecto

  // --- NUEVA FORMA DE OUTPUTS ---
  // En lugar de @Output() con EventEmitter, usamos output()
  onGreet = output<string>();

  notificar() {
    this.onGreet.emit(`¡Hola desde el componente de ${this.name()}!`);
  }
}
