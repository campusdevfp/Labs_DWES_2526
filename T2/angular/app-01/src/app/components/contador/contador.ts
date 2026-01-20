import { Component, signal } from '@angular/core';
@Component({
  selector: 'app-contador',
  imports: [],
  templateUrl: './contador.html',
  styleUrl: './contador.scss',
})
export class Contador {
  counter = signal(0);

  incrementar() {
    this.counter.update((v) => v + 1);
  }

  reset() {
    this.counter.set(0);
  }
}
