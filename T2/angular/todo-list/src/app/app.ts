import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { GuestForm } from './components/guest-form/guest-form';
import { GuestList } from './components/guest-list/guest-list';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, GuestForm, GuestList],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  title = signal('Todo List');
  // El estado principal vive aquí
  guests = signal<string[]>(['Ana', 'Pedro']);

  addGuest(name: string) {
    this.guests.update((prev) => [...prev, name]);
  }

  removeGuest(index: number) {
    this.guests.update((prev) => prev.filter((_, i) => i !== index));
  }
}
