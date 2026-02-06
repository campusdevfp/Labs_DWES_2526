import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError } from 'rxjs';
import {
  User,
  LoginCredentials,
  RegisterData,
  AuthResponse,
  EmailAvailability,
} from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly API_URL = 'https://api.escuelajs.co/api/v1';

  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';

  // Signals for state management
  private currentUserSignal = signal<User | null>(null);
  private loadingSignal = signal<boolean>(false);

  // Public computed values
  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.currentUserSignal() !== null);
  readonly isLoading = this.loadingSignal.asReadonly();

  constructor() {
    this.loadUserFromToken();
  }

  /**
   * Register a new user
   */
  register(data: RegisterData): Observable<User> {
    this.loadingSignal.set(true);

    const payload = {
      name: data.name,
      email: data.email,
      password: data.password,
      avatar: data.avatar && data.avatar.trim() ? data.avatar : 'https://i.imgur.com/yhW6Yw1.jpg',
    };

    return this.http.post<User>(`${this.API_URL}/users/`, payload).pipe(
      tap(() => this.loadingSignal.set(false)),
      catchError((error) => {
        this.loadingSignal.set(false);
        console.error('Registration error details:', error);
        return throwError(() => error);
      }),
    );
  }

  /**
   * Login user and store tokens
   */
  login(credentials: LoginCredentials): Observable<AuthResponse> {
    this.loadingSignal.set(true);

    return this.http.post<AuthResponse>(`${this.API_URL}/auth/login`, credentials).pipe(
      tap((response) => {
        console.log(' ========== AUTENTICACIÓN JWT ==========');
        console.log(' TOKEN RECIBIDO desde la API');
        console.log(' Endpoint:', `${this.API_URL}/auth/login`);
        console.log(' Respuesta completa:', response);
        console.log(' Access Token:', response.access_token);
        console.log(' Refresh Token:', response.refresh_token);
        console.log('========================================');

        this.storeTokens(response);
        this.loadUserProfile();
        this.loadingSignal.set(false);
      }),
      catchError((error) => {
        this.loadingSignal.set(false);
        return throwError(() => error);
      }),
    );
  }

  /**
   * Logout user and clear tokens
   */
  logout(): void {
    this.clearTokens();
    this.currentUserSignal.set(null);
    this.router.navigate(['/login']);
  }

  /**
   * Get user profile from API
   */
  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/auth/profile`);
  }

  /**
   * Refresh access token
   */
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();

    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http
      .post<AuthResponse>(`${this.API_URL}/auth/refresh-token`, {
        refreshToken,
      })
      .pipe(tap((response) => this.storeTokens(response)));
  }

  /**
   * Check if email is available
   */
  checkEmailAvailability(email: string): Observable<EmailAvailability> {
    return this.http.post<EmailAvailability>(`${this.API_URL}/users/is-available`, {
      email,
    });
  }

  /**
   * Get access token from storage
   */
  getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  /**
   * Get refresh token from storage
   */
  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  /**
   * Store authentication tokens
   */
  private storeTokens(response: AuthResponse): void {
    console.log(' GUARDANDO TOKENS en localStorage');
    console.log(' Key para Access Token:', this.ACCESS_TOKEN_KEY);
    console.log(' Key para Refresh Token:', this.REFRESH_TOKEN_KEY);

    localStorage.setItem(this.ACCESS_TOKEN_KEY, response.access_token);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refresh_token);

    console.log('✅ Tokens guardados exitosamente en localStorage');
  }

  /**
   * Clear authentication tokens
   */
  private clearTokens(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }

  /**
   * Load user from existing token
   */
  private loadUserFromToken(): void {
    const token = this.getAccessToken();

    if (token) {
      this.loadUserProfile();
    }
  }

  /**
   * Load user profile and update signal
   */
  private loadUserProfile(): void {
    console.log(' CARGANDO PERFIL DE USUARIO con el token');
    console.log(' Endpoint protegido:', `${this.API_URL}/auth/profile`);
    console.log(' Usando Access Token para autenticación...');

    this.getProfile().subscribe({
      next: (user) => {
        console.log('✅ Perfil cargado exitosamente:', user);
        console.log('========================================\n');
        this.currentUserSignal.set(user);
      },
      error: () => {
        console.error('❌ Error al cargar perfil');
        this.clearTokens();
        this.currentUserSignal.set(null);
      },
    });
  }
}
