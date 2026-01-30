import { Component, inject, signal, effect } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './product-detail-component.html',
  styleUrls: ['./product-detail-component.scss'],
})
export class ProductDetailComponent {
  // Inyecta ActivatedRoute para leer parámetros de la URL
  route = inject(ActivatedRoute);
  productId = signal<string | null>(null);

  constructor() {
    // Cuando el parámetro :id cambia, actualiza el signal

    this.route.paramMap.subscribe((params) => {
      this.productId.set(params.get('id'));
    });
  }
}
