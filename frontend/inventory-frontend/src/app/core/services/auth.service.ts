import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, timer, Subscription } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { LoginRequest } from '../models/login-request.model';
import { AuthResponse } from '../models/auth-response.model';
import { UserClaims } from '../models/user-claims.model';
import { UserContextService } from './user-context.service';
import { environment } from '../../../environments/environment';

/**
 * Servicio responsable de la lógica de autenticación con refresh automático.
 * Sincroniza con UserContextService para mantener estado reactivo del usuario.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly ACCESS_TOKEN_KEY = 'Access_token';
  private readonly REFRESH_TOKEN_KEY = 'Auth_Refresh_token';
  private refreshTimerSubscription?: Subscription;

  constructor(
    private http: HttpClient,
    private userContext: UserContextService
  ) {
    // Al iniciar, verificar si hay token y restaurar contexto
    this.initializeFromStorage();
  }

  /**
   * Realiza la petición de login al backend.
   * @param request Credenciales de usuario.
   * @returns Observable con los tokens JWT.
   */
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, request)
      .pipe(
        map((resp) => {
          this.storeTokens(resp);
          return resp;
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Registro de nuevo usuario; devuelve también tokens.
   */
  register(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/register`, request)
      .pipe(
        map((resp) => {
          this.storeTokens(resp);
          return resp;
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Renueva el access token usando el refresh token.
   */
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/refresh`, { refreshToken })
      .pipe(
        map((resp) => {
          this.storeTokens(resp);
          return resp;
        }),
        catchError((err) => {
          // Si el refresh falla, desloguear
          this.logout();
          return throwError(() => err);
        })
      );
  }

  /**
   * Elimina el token y cualquier estado relacionado.
   */
  logout(): void {
    this.cancelRefreshTimer();
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    this.userContext.clear();
  }

  /**
   * Retorna el token actual (si existe).
   */
  getToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  /**
   * Determina si el usuario está autenticado verificando token y expiración.
   */
  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const payload = this.decodePayload(token);
      if (payload?.exp) {
        const now = Math.floor(Date.now() / 1000);
        return payload.exp > now;
      }
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Obtiene refresh token.
   */
  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  /**
   * Obtiene los claims del usuario del token actual.
   */
  getUserClaims(): UserClaims | null {
    const token = this.getToken();
    if (!token) return null;
    return this.decodePayload(token);
  }

  /**
   * Inicializa el contexto desde localStorage si existe token válido.
   */
  private initializeFromStorage(): void {
    const token = this.getToken();
    if (token && this.isAuthenticated()) {
      const claims = this.decodePayload(token);
      if (claims) {
        this.userContext.setUser(claims);
        this.scheduleTokenRefresh(claims.exp);
      }
    }
  }

  /**
   * Almacena tokens y actualiza contexto del usuario.
   */
  private storeTokens(resp: AuthResponse): void {
    // Algunos backends usan "accessToken" (con doble 's'), otros "accesToken".
    // Aceptamos varias variantes comunes para evitar problemas de mapeo.
    const accessToken = (resp as any)?.accesToken ?? (resp as any)?.accessToken ?? (resp as any)?.token ?? (resp as any)?.access_token;
    const refreshToken = (resp as any)?.refreshToken ?? (resp as any)?.refresh_token ?? null;

    if (accessToken) {
      localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
      const claims = this.decodePayload(accessToken);
      if (claims) {
        this.userContext.setUser(claims);
        this.scheduleTokenRefresh(claims.exp);
      }
    }
    if (refreshToken) {
      localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
    }

    // Log útil para depuración durante integración (puede quitarse en producción).
    console.log('AuthService: storeTokens -> accessToken set=', !!accessToken, ' refreshToken set=', !!refreshToken);
  }

  /**
   * Programa renovación automática del token 60 segundos antes de expirar.
   */
  private scheduleTokenRefresh(exp?: number): void {
    this.cancelRefreshTimer();
    if (!exp) return;

    const now = Math.floor(Date.now() / 1000);
    const expiresIn = exp - now;
    const refreshIn = Math.max(expiresIn - 60, 5); // Renovar 60s antes, mínimo 5s

    if (refreshIn > 0) {
      this.refreshTimerSubscription = timer(refreshIn * 1000)
        .pipe(switchMap(() => this.refreshToken()))
        .subscribe({
          next: () => console.log('Token refreshed successfully'),
          error: (err) => console.error('Token refresh failed', err),
        });
    }
  }

  /**
   * Cancela el timer de refresh si existe.
   */
  private cancelRefreshTimer(): void {
    if (this.refreshTimerSubscription) {
      this.refreshTimerSubscription.unsubscribe();
      this.refreshTimerSubscription = undefined;
    }
  }

  /**
   * Decodifica el payload de un JWT (sin validar firma, sólo uso cliente para exp/claims básicos).
   */
  private decodePayload(token: string): UserClaims | null {
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    const payload = parts[1];
    try {
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded) as UserClaims;
    } catch {
      return null;
    }
  }

  /**
   * Manejo genérico de errores HTTP.
   */
  private handleError(error: HttpErrorResponse) {
    // El backend envía el mensaje en error.error.message
    const backendMessage = (error?.error && (error.error.message || error.error.error || error.error.detail)) as
      | string
      | undefined;
    const message = backendMessage
      ? backendMessage
      : error.error instanceof ErrorEvent
      ? `Error de cliente: ${error.error.message}`
      : `Error del servidor (${error.status}): ${error.message}`;
    return throwError(() => new Error(message));
  }
}
