/**
 * Respuesta del backend para login/register que incluye tokens de acceso y refresco.
 * Si el backend incluye expiración se puede derivar desde el JWT (claim `exp`).
 */
export interface AuthResponse {
  accesToken: string;
  refreshToken: string;
}
