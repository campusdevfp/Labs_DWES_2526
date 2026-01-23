import { Component, signal } from '@angular/core';
import { HeaderComponent } from './components/header/header';
import { FooterComponent } from './components/footer/footer';
import { MainComponent } from './components/main/main';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderComponent, FooterComponent, MainComponent],
  template: `
    <div class="app-layout">
      <app-header [titulo]="tituloPagina()" (onMenuSelect)="actualizarContenido($event)" />

      <app-main [contenido]="contenidoPrincipal()" />

      <app-footer />
    </div>
  `,
  styles: [
    `
      .app-layout {
        display: flex;
        flex-direction: column;
        min-height: 100vh;
      }
    `,
  ],
})
export class AppComponent {
  // Estado de la aplicación con Signals
  tituloPagina = signal('Mi Web Angular');
  contenidoPrincipal = signal(
    'Bienvenido. Por favor, usa el menú superior para navegar por las secciones.',
  );

  actualizarContenido(opcion: string) {
    switch (opcion) {
      case 'inicio':
        this.tituloPagina.set('Página de Inicio');
        this.contenidoPrincipal.set(
          'Esta es la sección principal donde verás las últimas noticias y actualizaciones.',
        );
        break;
      case 'acerca':
        this.tituloPagina.set('Sobre Nosotros');
        this.contenidoPrincipal.set(
          'Somos un equipo de desarrolladores aprendiendo las nuevas capacidades de Angular 21 y Signals.',
        );
        break;
      case 'contacto':
        this.tituloPagina.set('Centro de Contacto');
        this.contenidoPrincipal.set(
          '¿Tienes dudas? Escríbenos a soporte@ejemplo.com o llámanos al +34 900 000 000.',
        );
        break;
    }
  }
}
