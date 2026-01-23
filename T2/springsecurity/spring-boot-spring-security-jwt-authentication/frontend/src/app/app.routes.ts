// frontend/src/app/app.routes.ts
import { Routes } from '@angular/router';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login.page').then((m) => m.LoginPage),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register.page').then((m) => m.RegisterPage),
  },
  {
    path: 'user',
    loadComponent: () =>
      import('./features/auth/user.page').then((m) => m.UserPage),
   
  },

  { path: '**', redirectTo: 'login' },
];
