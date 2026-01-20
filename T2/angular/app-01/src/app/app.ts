import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Contador } from './components/contador/contador';
import { UserCard } from './components/user-card/user-card';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Contador, UserCard],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  my_title = 'My Angular App';
  userName = signal('Pepe');

  cambiarNombre() {
    this.userName.set('Juan');
  }

  manejarSaludo(mensaje: string) {
    alert(mensaje);
  }
}
