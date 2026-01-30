import { Routes } from '@angular/router';
import { ProductDetailComponent } from './product-detail-component/product-detail-component';
import { ProductListComponent } from './product-list-component/product-list-component';

export const routes: Routes = [
  // Ruta estática: muestra la lista de productos
  { path: '', component: ProductListComponent },

  // Ruta dinámica: captura el ID del producto
  { path: 'product/:id', component: ProductDetailComponent },
];
