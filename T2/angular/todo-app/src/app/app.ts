import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TareaLista } from './tarea-lista/tarea-lista';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, TareaLista],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('todo-app');
  version: number = 21;
  contador = signal(0); // Nueva Signal
}
