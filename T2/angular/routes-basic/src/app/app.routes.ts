import { Routes } from '@angular/router';
import { AboutComponent } from './about-component/about-component';
import { ContactComponent } from './contact-component/contact-component';
import { HomeComponent } from './home-component/home-component';
import { NotFoundComponent } from './not-found-component/not-found-component';

export const routes: Routes = [
  { path: '', component: HomeComponent }, // Ruta raíz
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: ContactComponent },
  { path: '**', component: NotFoundComponent }, // Ruta comodín (debe ir al final)
];
