// petición que se envía al backend (VendorRequest)
export interface ProveedorRequest {
  nombre: string;  // ← mapea a VendorRequest.nombre (razón social)
  tipoDocumento: 'DNI' | 'RUC' | null; // enum DocumentType en el backend
  documento: string;                   // ← VendorRequest.documento
  direccion?: string;
  telefono?: string;
  email?: string;
  notas?: string;
  moneda: 'PEN' | 'USD';               // enum Currency
  condicionPago?:
    | 'EFECTIVO'
    | 'CREDIT_15_DAYS'
    | 'CREDIT_30_DAYS'
    | 'CREDIT_45_DAYS'
    | 'CREDIT_60_DAYS';               // enum CondicionPago
}
