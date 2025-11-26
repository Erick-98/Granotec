// proveedor-response.model.ts

import { DocumentType, Currency, CondicionPago } from './proveedor-request.model';

export interface ProveedorResponse {
  id: number;
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
