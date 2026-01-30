import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [RouterLink], // Necesario para [routerLink]
  templateUrl: './product-list-component.html',
  styleUrls: ['./product-list-component.scss'],
})
export class ProductListComponent {
  // Datos de ejemplo
  productos = signal([
    { id: 1, nombre: 'Laptop' },
    { id: 123, nombre: 'Smartphone' },
    { id: 456, nombre: 'Tablet' },
  ]);
}
