export interface KardexItem {
  id: number;
  fechaMovimiento: string;
  almacenId: number;
  almacenNombre: string;
  tipoMovimiento: string; // ENTRADA, SALIDA
  tipoOperacion: string; // PRODUCCION, VENTA, COMPRA, AJUSTE, ELIMINACION, etc.
  referencia: string; // Factura/Guía
  
  // Producto
  productoId: number;
  productoCodigo: string;
  productoNombre: string;
  familiaProducto: any;
  tipoProducto: string;
  
  // Lote
  loteId?: number;
  loteCodigo?: string;
  fechaProduccion?: string;
  fechaVencimiento?: string;
  
  // OP
  numeroOp?: string;
  fechaIngresoOp?: string;
  
  // Adicionales
  presentacion?: string;
  proveedor?: string;
  destinoCliente?: string;
  
  // Valores monetarios
  cantidad: number;
  costoUnitarioSoles: number;
  totalSoles: number;
  costoUnitarioDolares?: number;
  totalDolares?: number;
  
  // Stock
  stockAnterior: number;
  stockActual: number;
  
  // Auditoría
  observacion?: string;
  usuarioId?: number;
  usuarioNombre?: string;
}

export interface KardexFilter {
  fechaInicio?: string;
  fechaFin?: string;
  almacenId?: number;
  productoId?: number;
  lote?: string;
  tipoOperacion?: string;
  proveedorId?: number;
}

export interface KardexSummary {
  producto: string;
  codigo: string;
  saldoInicial: number;
  entradas: number;
  salidas: number;
  saldoFinal: number;
  valorTotalSoles: number;
  valorTotalDolares: number;
}

export interface KardexPageResponse {
  content: KardexItem[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}