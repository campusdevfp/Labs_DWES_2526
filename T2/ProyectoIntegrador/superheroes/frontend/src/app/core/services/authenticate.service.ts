import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, Observable, tap, throwError } from "rxjs";
import { JwtHelperService } from "@auth0/angular-jwt";
@Injectable({
  providedIn: "root",
})
export class AuthenticateService {
  constructor(
    private http: HttpClient,
    private jwtHelper: JwtHelperService,
  ) {}

  login(data: { email: string; password: string }): Observable<any> {
    return this.http
      .post<any>(`/api/authenticate`, data)
      .pipe(
        tap((data: any) => data),
        catchError((err) => throwError(() => err)),
      );
  }

  register(data: { email: string; password: string }): Observable<any> {
    return this.http.post<any>(`/api/register`, data).pipe(
      tap((data: any) => data),
      catchError((err) => throwError(() => err)),
    );
  }

  getToken(): string {
    return localStorage.getItem("token") ?? "";
  }

  /**
   * Devuelve el email/username del token (claim `sub`) o null si no hay token/está inválido.
   */
  getLoggedInUsername(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const decoded: any = this.jwtHelper.decodeToken(token);
      return decoded?.sub ?? null;
    } catch {
      return null;
    }
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token && !this.jwtHelper.isTokenExpired(token);
  }

  logout(): void {
    localStorage.removeItem("token");
  }
}
