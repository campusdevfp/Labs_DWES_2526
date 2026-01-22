// src/app/core/auth.store.ts
import { Injectable, inject } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root',
})
export class AuthStore {
  private readonly jwtHelper = inject(JwtHelperService);

  isAuthenticated(): boolean {
    const token = localStorage.getItem('access_token');
    return !!token && !this.jwtHelper.isTokenExpired(token);
  }

  hasAnyRole(roles: string[]): boolean {
    if (roles.length === 0) {
      return true;
    }

    const token = localStorage.getItem('access_token');
    if (!token) {
      return false;
    }

    const decoded: any = this.jwtHelper.decodeToken(token) ?? {};
    const userRoles: string[] = Array.isArray(decoded.roles)
      ? decoded.roles
      : decoded.role
        ? Array.isArray(decoded.role)
          ? decoded.role
          : [decoded.role]
        : [];

    if (userRoles.length === 0) {
      return false;
    }

    return roles.some((role) => userRoles.includes(role));
  }

  logout(): void {
    localStorage.removeItem('access_token');
  }
}
