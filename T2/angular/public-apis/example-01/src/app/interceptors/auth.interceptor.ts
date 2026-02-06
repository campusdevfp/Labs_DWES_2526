import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getAccessToken();

  // 🔐 INTERCEPTOR: Aquí se AÑADE el token a cada petición HTTP
  if (token) {
    console.log('🔒 INTERCEPTOR HTTP: Añadiendo token JWT a la petición');
    console.log('📍 URL:', req.url);
    console.log('🎟️ Token:', token.substring(0, 30) + '...');
    console.log('📤 Header Authorization: Bearer ' + token.substring(0, 20) + '...');
  }

  // Clone the request and add authorization header if token exists
  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Handle 401 Unauthorized errors
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }

      return throwError(() => error);
    }),
  );
};
