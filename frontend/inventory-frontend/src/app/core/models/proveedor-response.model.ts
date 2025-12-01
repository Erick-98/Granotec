// respuesta que viene del backend (VendorResponse)
export interface ProveedorResponse {
  id: number;
  nombre: string;          // viene de v.getRazonSocial()
  tipoDocumento?: string;  // 'DNI' | 'RUC'
  documento?: string;      // nro doc
  direccion?: string;
  telefono?: string;
  email?: string;
  notas?: string;
  moneda?: string;         // 'PEN' | 'USD'
  condicionPago?: string;  // 'EFECTIVO', 'CREDIT_15_DAYS', etc.
}
