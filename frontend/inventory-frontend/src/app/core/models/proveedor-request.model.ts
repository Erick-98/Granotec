export interface ProveedorRequest {
  razonSocial: string;  // ← CAMBIA 'nombre' por 'razonSocial'
  tipoDocumento?: string;
  nroDocumento?: string;  // ← CAMBIA 'documento' por 'nroDocumento'
  direccion?: string;
  telefono?: string;
  email?: string;
  moneda: string;
  condicionPago?: string;
  notas?: string;
}

export type DocumentType = 'DNI' | 'RUC';

export type Currency = 'PEN' | 'USD';

export type CondicionPago =
  | 'EFECTIVO'
  | 'CREDIT_15_DAYS'
  | 'CREDIT_30_DAYS'
  | 'CREDIT_45_DAYS'
  | 'CREDIT_60_DAYS';
