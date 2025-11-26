// models/customer.model.ts
export interface CustomerRequest {
  nombre: string;
  apellidos: string;
  razonSocial: string;
  zona: string;
  distritoId: number;
  tipoClienteId: number;
  rubro: string;
  condicionPago: string;
  limiteDolares: number;
  limiteCreditoSoles: number;
  notas: string;
  tipoDocumento: string;
  nroDocumento: string;
  direccion: string;
  telefono: string;
  email: string;
}

export interface CustomerResponse {
  id: number;
  nombre: string;
  apellidos: string;
  razonSocial: string;
  zona: string;
  rubro: string;
  condicionPago: string;
  limiteDolares: number;
  limiteCreditoSoles: number;
  notas: string;
  tipoDocumento: string;
  nroDocumento: string;
  direccion: string;
  telefono: string;
  email: string;
  distrito: string;
  provincia: string;
  departamento: string;
  tipoCliente: string;
}

// AGREGAR ESTA INTERFACE QUE FALTABA
export interface TypeCustomerResponse {
  id: number;
  nombre: string;
  descripcion: string;
}