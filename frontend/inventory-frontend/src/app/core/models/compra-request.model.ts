export interface CompraRequest {
  numeroFactura: string; // El backend lo requiere con @NotNull
  proveedorId: number;
  fecha?: string; // ISO date yyyy-MM-dd
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
