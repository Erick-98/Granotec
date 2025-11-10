import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { inject } from '@angular/core';
import { UserContextService } from '../services/user-context.service';

/**
 * Guard funcional que protege rutas según roles o permisos del usuario.
 * 
 * Uso en rutas:
 * ```
 * {
 *   path: 'admin',
 *   canActivate: [roleGuard],
 *   data: { roles: ['admin', 'superadmin'] }  // o { permissions: ['users.write'] }
 * }
 * ```
 */
export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const userContext = inject(UserContextService);
  const router = inject(Router);

  if (!userContext.isAuthenticated()) {
    return router.createUrlTree(['/authentication/login']);
  }

  // Verificar roles si están definidos en data
  const requiredRoles = route.data['roles'] as string[] | undefined;
  if (requiredRoles && requiredRoles.length > 0) {
    if (!userContext.hasAnyRole(requiredRoles)) {
      // Redirigir a página de acceso denegado o dashboard
      return router.createUrlTree(['/dashboard']);
    }
  }

  // Verificar permisos si están definidos en data
  const requiredPermissions = route.data['permissions'] as string[] | undefined;
  if (requiredPermissions && requiredPermissions.length > 0) {
    if (!userContext.hasAnyPermission(requiredPermissions)) {
      return router.createUrlTree(['/dashboard']);
    }
  }

  return true;
};
