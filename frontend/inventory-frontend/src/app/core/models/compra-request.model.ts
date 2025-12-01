export interface CompraRequest {
  numeroFactura?: string;
  proveedorId: number;
  fecha?: string; // ISO date
  almacenId: number;
  detalles: CompraDetalleRequest[];
}

export interface CompraDetalleRequest {
  productoId: number;
  cantidadOrdenada: number;
  precioUnitario: number;
  cantidadRecibida: number;
  lote?: string;
  fechaProduccion?: string;
  fechaVencimiento?: string;
}
