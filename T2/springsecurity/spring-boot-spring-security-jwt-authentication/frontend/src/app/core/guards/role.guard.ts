import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthStore } from '../auth/auth.store';

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthStore);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    return router.parseUrl('/login');
  }

  const allowed = (route.data?.['roles'] as string[] | undefined) ?? [];
  if (allowed.length === 0) return true;

  if (!auth.hasAnyRole(allowed)) {
    return router.parseUrl('/login');
  }

  return true;
};
