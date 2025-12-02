import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserClaims } from '../models/user-claims.model';

/**
 * Servicio centralizado que mantiene el estado del usuario autenticado.
 * Expone observables para que componentes (header, sidebar, etc.) reaccionen a cambios.
 */
@Injectable({ providedIn: 'root' })
export class UserContextService {
  private userSubject = new BehaviorSubject<UserClaims | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);

  /** Observable del usuario actual (null si no autenticado) */
  public user$: Observable<UserClaims | null> = this.userSubject.asObservable();

  /** Observable del estado de autenticación */
  public isAuthenticated$: Observable<boolean> = this.isAuthenticatedSubject.asObservable();

  /**
   * Actualiza el contexto del usuario con los claims del token.
   */
  setUser(claims: UserClaims | null): void {
    this.userSubject.next(claims);
    this.isAuthenticatedSubject.next(claims !== null);
  }

  /**
   * Obtiene el usuario actual (snapshot).
   */
  getUser(): UserClaims | null {
    return this.userSubject.value;
  }

  /**
   * Retorna true si hay usuario autenticado.
   */
  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  /**
   * Limpia el contexto del usuario (usado en logout).
   */
  clear(): void {
    this.userSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }

  /**
   * Verifica si el usuario tiene un rol específico.
   */
  hasRole(role: string): boolean {
    return this.userSubject.value?.role === role;
  }

  /**
   * Verifica si el usuario tiene un permiso específico.
   */
  hasPermission(permission: string): boolean {
    return this.userSubject.value?.permissions.includes(permission) ?? false;
  }

  /**
   * Verifica si el usuario tiene al menos uno de los roles especificados.
   */
  hasAnyRole(roles: string[]): boolean {
    const userRole = this.userSubject.value?.role;
    return userRole ? roles.includes(userRole) : false;
  }

  /**
   * Verifica si el usuario tiene al menos uno de los permisos especificados.
   */
  hasAnyPermission(permissions: string[]): boolean {
    const userPermissions = this.userSubject.value?.permissions ?? [];
    return permissions.some(p => userPermissions.includes(p));
  }
}
