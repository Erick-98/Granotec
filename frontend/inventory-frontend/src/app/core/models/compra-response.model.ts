export interface CompraResponse {
  id?: number;
  numero?: string;
  proveedorId?: number;
  proveedorNombre?: string;
  almacenId?: number;
  almacenNombre?: string;
  fecha?: string;
  estado?: string;
  total?: number;
  detalles?: CompraDetalleResponse[];
}

export interface CompraDetalleResponse {
  productoId?: number;
  productoNombre?: string;
  loteId?: number;
  codigoLote?: string;
  cantidad?: number;
  precioUnitario?: number;
  subtotal?: number;
  estado?: string;
  fechaProduccion?: string;
  fechaVencimiento?: string;
}
