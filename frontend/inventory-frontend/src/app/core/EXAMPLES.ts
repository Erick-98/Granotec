/**
 * EJEMPLO DE USO DEL SISTEMA DE AUTENTICACIÓN
 * 
 * Este archivo muestra cómo integrar el sistema en componentes comunes.
 * NO es funcional, solo referencia.
 */

// ============================================
// 1. HEADER COMPONENT - Mostrar usuario actual
// ============================================

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserContextService } from '../core/services/user-context.service';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <header>
      <div *ngIf="user$ | async as user">
        <span>{{ user.name }}</span>
        <span>{{ user.email }}</span>
        <span class="badge">{{ user.role }}</span>
        <button (click)="logout()">Cerrar Sesión</button>
      </div>
    </header>
  `
})
export class HeaderComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  userContext = inject(UserContextService);
  
  user$ = this.userContext.user$;
  
  logout() {
    this.auth.logout();
    this.router.navigate(['/authentication/login']);
  }
}

// ============================================
// 2. SIDEBAR - Menú condicional por roles
// ============================================

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <nav>
      <a routerLink="/dashboard">Dashboard</a>
      <a routerLink="/profile">Mi Perfil</a>
      
      <!-- Solo para admins -->
      <a *ngIf="isAdmin" routerLink="/admin">Administración</a>
      
      <!-- Solo si tiene permiso específico -->
      <a *ngIf="canManageUsers" routerLink="/users">Usuarios</a>
    </nav>
  `
})
export class SidebarComponent {
  private userContext = inject(UserContextService);
  
  isAdmin = this.userContext.hasRole('admin');
  canManageUsers = this.userContext.hasPermission('users.write');
}

// ============================================
// 3. DASHBOARD - Verificar permisos
// ============================================

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div>
      <h1>Dashboard</h1>
      
      <div *ngIf="user$ | async as user">
        <p>Bienvenido, {{ user.name }}</p>
        
        <button *ngIf="canCreateReports" (click)="createReport()">
          Crear Reporte
        </button>
      </div>
    </div>
  `
})
export class DashboardComponent {
  private userContext = inject(UserContextService);
  
  user$ = this.userContext.user$;
  canCreateReports = this.userContext.hasPermission('reports.create');
  
  createReport() {
    // Lógica...
  }
}

// ============================================
// 4. RUTAS PROTEGIDAS - app.routes.ts
// ============================================

import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/guards';

export const routes: Routes = [
  // Ruta pública
  {
    path: 'authentication/login',
    component: LoginComponent
  },
  
  // Ruta protegida simple
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard]
  },
  
  // Ruta protegida por ROL
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [roleGuard],
    data: { roles: ['admin', 'superadmin'] }
  },
  
  // Ruta protegida por PERMISOS
  {
    path: 'users',
    component: UsersComponent,
    canActivate: [roleGuard],
    data: { permissions: ['users.read', 'users.write'] }
  },
  
  // Layout completo protegido
  {
    path: '',
    component: FullComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'profile', component: ProfileComponent },
      // todas las rutas hijas heredan la protección
    ]
  }
];

// ============================================
// 5. SERVICIO CUSTOM - Usar claims del usuario
// ============================================

import { Injectable, inject } from '@angular/core';
import { UserContextService } from '../core/services/user-context.service';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private userContext = inject(UserContextService);
  
  createReport(data: any) {
    const user = this.userContext.getUser();
    
    if (!user) {
      throw new Error('Usuario no autenticado');
    }
    
    // Usar datos del usuario
    return this.http.post('/api/reports', {
      ...data,
      createdBy: user.email,
      createdByName: user.name
    });
  }
  
  canDeleteReport(): boolean {
    return this.userContext.hasAnyPermission(['reports.delete', 'admin.all']);
  }
}

// ============================================
// 6. DIRECTIVA CUSTOM - Mostrar/ocultar por permiso
// ============================================

import { Directive, Input, TemplateRef, ViewContainerRef, inject } from '@angular/core';
import { UserContextService } from '../core/services/user-context.service';

@Directive({
  selector: '[hasPermission]',
  standalone: true
})
export class HasPermissionDirective {
  private userContext = inject(UserContextService);
  private templateRef = inject(TemplateRef<any>);
  private viewContainer = inject(ViewContainerRef);
  
  @Input() set hasPermission(permission: string) {
    if (this.userContext.hasPermission(permission)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}

// Uso:
// <button *hasPermission="'users.delete'" (click)="delete()">Eliminar</button>

// ============================================
// 7. INTERCEPTOR CUSTOM - Añadir headers extras
// ============================================

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { UserContextService } from '../core/services/user-context.service';

export const customHeadersInterceptor: HttpInterceptorFn = (req, next) => {
  const userContext = inject(UserContextService);
  const user = userContext.getUser();
  
  if (user) {
    // Añadir headers personalizados
    req = req.clone({
      setHeaders: {
        'X-User-Role': user.role,
        'X-User-Email': user.email
      }
    });
  }
  
  return next(req);
};

// Registrar en app.config.ts:
// provideHttpClient(withInterceptors([jwtInterceptor, customHeadersInterceptor]))
