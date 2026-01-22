// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginPageComponent } from './features/auth/login-page.component';
import { RegisterPageComponent } from './features/auth/register-page.component';
import { UserPageComponent } from './features/user/user-page.component';
import { RoleGuard } from './core/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  { path: 'register', component: RegisterPageComponent },
  { path: 'login', component: LoginPageComponent },

  {
    path: 'user',
    component: UserPageComponent,
    canActivate: [RoleGuard],
    data: { roles: ['USER', 'ROLE_USER'] },
  },

  { path: '**', redirectTo: 'login' },
];
