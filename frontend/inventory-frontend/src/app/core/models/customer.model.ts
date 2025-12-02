export type DocumentType = 'DNI' | 'RUC';

export type CondicionPago =
  | 'EFECTIVO'
  | 'CREDIT_15_DAYS'
  | 'CREDIT_30_DAYS'
  | 'CREDIT_45_DAYS'
  | 'CREDIT_60_DAYS';

// ====== RESPUESTA DEL BACKEND ======
export interface CustomerResponse {
  id: number;
  nombre: string | null;
  apellidos: string | null;
  razonSocial: string | null;
  zona: string | null;
  rubro: string | null;
  condicionPago: string;         // nombre del enum
  limiteDolares: number | null;
  limiteCreditoSoles: number | null;
  notas: string | null;
  tipoDocumento: DocumentType;
  nroDocumento: string;
  direccion: string | null;
  telefono: string | null;
  email: string | null;
  distrito: string | null;
  provincia: string | null;
  departamento: string | null;
  tipoCliente: string | null;
}

// ====== REQUEST QUE ESPERA EL BACKEND ======
export interface CustomerRequest {
  nombre?: string | null;
  apellidos?: string | null;
  razonSocial?: string | null;
  zona?: string | null;
  distritoId: number;        // ID del distrito
  tipoClienteId: number;     // ID del tipo de cliente
  rubro?: string | null;
  condicionPago: CondicionPago | string;
  limiteDolares?: number | null;
  limiteCreditoSoles?: number | null;
  notas?: string | null;
  tipoDocumento: DocumentType | string;
  nroDocumento: string;
  direccion?: string | null;
  telefono?: string | null;
  email?: string | null;
}

// ====== TYPE CUSTOMER ======
export interface TypeCustomerResponse {
  id: number;
  nombre: string;
  descripcion: string | null;
}
