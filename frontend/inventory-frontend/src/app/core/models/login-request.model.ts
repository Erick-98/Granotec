/**
 * Representa la carga útil del formulario de inicio de sesión.
 * Ajusta las propiedades si tu backend usa otros nombres.
 */
export interface LoginRequest {
  email: string;
  password: string;
}
