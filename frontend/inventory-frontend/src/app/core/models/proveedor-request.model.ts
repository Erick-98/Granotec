export interface ProveedorRequest {
  nombre: string;
  tipoDocumento?: DocumentType;  
  documento?: string;
  direccion?: string;
  telefono?: string;
  email?: string;
  notas?: string;
  moneda: Currency;             
  condicionPago?: CondicionPago; 
}

export type DocumentType = 'DNI' | 'RUC';

export type Currency = 'PEN' | 'USD';

export type CondicionPago =
  | 'EFECTIVO'
  | 'CREDIT_15_DAYS'
  | 'CREDIT_30_DAYS'
  | 'CREDIT_45_DAYS'
  | 'CREDIT_60_DAYS';
