import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet], // Necesario para mostrar componentes dinámicos
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
})
export class AppComponent {}
