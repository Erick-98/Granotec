import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

/**
 * Lista de endpoints públicos que no requieren autenticación.
 * Se excluyen del header Authorization.
 */
const PUBLIC_ENDPOINTS = [
  `${environment.apiUrl}/auth/login`,
  `${environment.apiUrl}/auth/register`,
  `${environment.apiUrl}/auth/refresh`,
  // Agregar más endpoints públicos aquí según necesites
];

/**
 * Interceptor funcional que adjunta el JWT al header Authorization.
 * Excluye rutas públicas de autenticación y maneja 401/403 redirigiendo a login.
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // Verificar si la URL actual está en la lista de endpoints públicos
  const isPublicEndpoint = PUBLIC_ENDPOINTS.some(endpoint => req.url.includes(endpoint));

  const token = auth.getToken();
  const authReq = !isPublicEndpoint && token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    // Manejo centralizado de 401/403
    catchError((error: any) => {
      if (error?.status === 401 || error?.status === 403) {
        auth.logout();
        router.navigate(['/authentication/login']);
      }
      return throwError(() => error);
    })
  );
};
