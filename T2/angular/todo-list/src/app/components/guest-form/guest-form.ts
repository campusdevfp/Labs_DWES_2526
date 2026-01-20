import { Component, output } from '@angular/core';

@Component({
  selector: 'app-guest-form',
  imports: [],
  templateUrl: './guest-form.html',
  styleUrl: './guest-form.scss',
})
export class GuestForm {
  // Definimos un output para enviar el nombre al padre
  onAddGuest = output<string>();

  add(input: HTMLInputElement) {
    if (input.value.trim()) {
      this.onAddGuest.emit(input.value);
      input.value = ''; // Limpiamos el input
    }
  }
}
