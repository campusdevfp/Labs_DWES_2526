import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { JwtResponse, LoginRequest, SignupRequest } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  register(body: SignupRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/api/auth/signup`, body);
  }

  login(body: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.baseUrl}/api/auth/signin`, body);
  }

  getUserContent(): Observable<string> {
    return this.http.get(`${this.baseUrl}/api/test/user`, { responseType: 'text' });
  }
}
