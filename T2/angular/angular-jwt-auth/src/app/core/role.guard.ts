// src/app/core/role.guard.ts
import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthStore } from './auth.store';

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthStore);
  const router = inject(Router);

  const allowed = (route.data?.['roles'] as string[] | undefined) ?? [];

  if (!auth.isAuthenticated()) {
    return router.parseUrl('/login');
  }

  if (allowed.length === 0) {
    return true;
  }

  if (!auth.hasAnyRole(allowed)) {
    return router.parseUrl('/login');
  }

  return true;
};
