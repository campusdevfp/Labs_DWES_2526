import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-guest-list',
  imports: [],
  templateUrl: './guest-list.html',
  styleUrl: './guest-list.scss',
})
export class GuestList {
  // Recibimos la lista como un Signal Input
  guests = input.required<string[]>();
  // Avisamos al padre para borrar
  onRemove = output<number>();
}
