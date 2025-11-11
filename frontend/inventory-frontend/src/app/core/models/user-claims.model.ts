/**
 * Claims extraídos del JWT del usuario autenticado.
 * Estos datos se obtienen decodificando el access_token.
 */
export interface UserClaims {
  email: string;
  name: string;
  role: string;
  permissions: string[];
  exp?: number; // Tiempo de expiración UNIX
  iat?: number; // Tiempo de emisión UNIX
}
