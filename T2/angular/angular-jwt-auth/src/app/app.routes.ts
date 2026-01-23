import { Routes } from '@angular/router';
import { LoginPage } from './features/auth/login.page';
import { roleGuard } from './core/guards/role.guard';
import { RegisterPage } from './features/auth/regiser.page';
import { UserPage } from './features/auth/user.page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },

  {
    path: 'user',
    component: UserPage,
    canActivate: [roleGuard],
    data: { roles: ['ROLE_USER', 'USER'] },
  },

  { path: '**', redirectTo: 'login' },
];
