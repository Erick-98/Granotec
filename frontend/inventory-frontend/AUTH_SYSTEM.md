# Sistema de Autenticación - Angular 17+

## 📋 Descripción

Sistema completo de autenticación JWT con las siguientes características:
- Login y registro de usuarios
- Refresh automático de tokens
- Guards de autenticación y roles
- Servicio de contexto de usuario reactivo
- Interceptor HTTP con endpoints públicos configurables
- Validación de expiración de tokens
- Protección automática de rutas

## 🏗️ Arquitectura

```
src/app/core/
├── guards/
│   ├── auth.guard.ts         # Protege rutas autenticadas
│   ├── role.guard.ts         # Protege rutas por rol/permisos
│   └── index.ts              # Barrel exports
├── interceptors/
│   └── jwt.interceptor.ts    # Añade token automáticamente
├── models/
│   ├── login-request.model.ts
│   ├── auth-response.model.ts
│   └── user-claims.model.ts  # Claims del JWT decodificado
└── services/
    ├── auth.service.ts       # Lógica de autenticación
    └── user-context.service.ts # Estado reactivo del usuario
```

## 🚀 Uso

### 1. Login básico

```typescript
// En componente
constructor(
  private auth: AuthService,
  private router: Router
) {}

onSubmit() {
  this.auth.login({ email, password }).subscribe({
    next: () => this.router.navigate(['/dashboard']),
    error: (err) => console.error(err.message)
  });
}
```

### 2. Proteger rutas con autenticación

```typescript
// app.routes.ts
{
  path: 'dashboard',
  component: DashboardComponent,
  canActivate: [authGuard]
}
```

### 3. Proteger rutas por roles

```typescript
{
  path: 'admin',
  component: AdminComponent,
  canActivate: [roleGuard],
  data: { roles: ['admin', 'superadmin'] }
}
```

### 4. Proteger rutas por permisos

```typescript
{
  path: 'users',
  component: UsersComponent,
  canActivate: [roleGuard],
  data: { permissions: ['users.read', 'users.write'] }
}
```

### 5. Acceder al usuario actual en componentes

```typescript
export class HeaderComponent {
  user$ = inject(UserContextService).user$;
  isAuthenticated$ = inject(UserContextService).isAuthenticated$;
}
```

```html
<div *ngIf="user$ | async as user">
  Bienvenido, {{ user.name }}
  <span>Rol: {{ user.role }}</span>
</div>
```

### 6. Verificar permisos en componentes

```typescript
export class MyComponent {
  private userContext = inject(UserContextService);
  
  canEdit = this.userContext.hasPermission('users.write');
  isAdmin = this.userContext.hasRole('admin');
}
```

## 🔑 Estructura del Token JWT

El backend debe devolver un JWT con el siguiente payload:

```json
{
  "email": "usuario@ejemplo.com",
  "name": "Nombre Usuario",
  "role": "admin",
  "permissions": ["users.read", "users.write", "reports.view"],
  "exp": 1699999999,
  "iat": 1699999000
}
```

## 🔄 Refresh Automático

El sistema programa automáticamente la renovación del token **60 segundos antes de expirar**. 

Proceso:
1. Al hacer login, se decodifica el token para obtener `exp`
2. Se programa un timer para llamar a `/env/auth/refresh` antes de expirar
3. Si el refresh falla, se hace logout automático
4. El nuevo token reemplaza al anterior y se reprograma el refresh

## 🌐 Endpoints del Backend

El sistema espera los siguientes endpoints:

| Endpoint | Método | Body | Respuesta |
|----------|--------|------|-----------|
| `/env/auth/login` | POST | `{ email, password }` | `{ accesToken, refreshToken }` |
| `/env/auth/register` | POST | `{ email, password, ... }` | `{ accesToken, refreshToken }` |
| `/env/auth/refresh` | POST | `{ refreshToken }` | `{ accesToken, refreshToken }` |

En caso de error, el backend debe devolver:
```json
{
  "message": "Credenciales inválidas"
}
```

## 🛡️ Endpoints Públicos

Los siguientes endpoints NO requieren token:

```typescript
// jwt.interceptor.ts
const PUBLIC_ENDPOINTS = [
  `${environment.apiUrl}/env/auth/login`,
  `${environment.apiUrl}/env/auth/register`,
  `${environment.apiUrl}/env/auth/refresh`,
];
```

Para agregar más endpoints públicos, simplemente añadirlos al array.

## 🎯 Flujo de Autenticación

1. **Inicio de App**: 
   - `AuthService` verifica si hay token en localStorage
   - Si es válido, restaura el contexto del usuario
   - Programa refresh automático

2. **Login**:
   - Usuario ingresa credenciales
   - Backend devuelve tokens
   - Se almacenan en localStorage
   - Se decodifica y actualiza `UserContextService`
   - Se programa refresh automático
   - Redirige a `/dashboard`

3. **Navegación Protegida**:
   - `authGuard` verifica autenticación
   - `roleGuard` verifica roles/permisos (opcional)
   - Si falla, redirige a login

4. **Peticiones HTTP**:
   - `jwtInterceptor` añade `Authorization: Bearer <token>`
   - Excluye endpoints públicos
   - Si recibe 401/403, hace logout y redirige

5. **Refresh Automático**:
   - Timer se dispara 60s antes de expirar
   - Llama a `/env/auth/refresh`
   - Actualiza tokens y reprograma
   - Si falla, hace logout

6. **Logout**:
   - Limpia localStorage
   - Limpia contexto de usuario
   - Cancela timer de refresh
   - Redirige a login

## 📦 Métodos Principales

### AuthService

```typescript
login(request: LoginRequest): Observable<AuthResponse>
register(request: LoginRequest): Observable<AuthResponse>
refreshToken(): Observable<AuthResponse>
logout(): void
isAuthenticated(): boolean
getToken(): string | null
getUserClaims(): UserClaims | null
```

### UserContextService

```typescript
user$: Observable<UserClaims | null>
isAuthenticated$: Observable<boolean>
setUser(claims: UserClaims | null): void
getUser(): UserClaims | null
isAuthenticated(): boolean
hasRole(role: string): boolean
hasPermission(permission: string): boolean
hasAnyRole(roles: string[]): boolean
hasAnyPermission(permissions: string[]): boolean
clear(): void
```

## 🔧 Configuración

### Environment

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://192.168.18.60:8080'
};
```

### Bootstrap

```typescript
// app.config.ts
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([jwtInterceptor])),
    // ... otros providers
  ],
};
```

## ⚠️ Consideraciones de Seguridad

1. **XSS**: Los tokens en localStorage son vulnerables a XSS. Sanitiza siempre el contenido del usuario.
2. **HTTPS**: En producción, usa siempre HTTPS para proteger los tokens en tránsito.
3. **Expiración**: Los tokens deben tener tiempo de vida corto (ej. 15-30 min).
4. **Refresh Token**: Debe tener vida más larga pero revocable por el backend.
5. **Validación Backend**: NUNCA confíes solo en validaciones del cliente; el backend debe validar tokens, roles y permisos.

## 🧪 Testing

Ejemplo de test para AuthService:

```typescript
describe('AuthService', () => {
  it('should store token on login', () => {
    const mockResponse = { accesToken: 'token123', refreshToken: 'refresh123' };
    httpMock.expectOne(environment.apiUrl + '/auth/login').flush(mockResponse);
    
    expect(localStorage.getItem('auth_access_token')).toBe('token123');
  });
});
```

## 📝 Mejoras Futuras

- [ ] Almacenamiento en cookies httpOnly (requiere backend adaptado)
- [ ] Multi-factor authentication (MFA)
- [ ] Remember me con refresh tokens de larga duración
- [ ] Audit log de accesos
- [ ] Rate limiting en login
- [ ] Captcha tras múltiples intentos fallidos
- [ ] Notificación de sesiones activas
- [ ] Revocación de tokens desde el cliente
