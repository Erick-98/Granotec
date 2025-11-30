export interface KardexItem {
  id: number;
  fecha: string;
  almacen: string;
  almacenId: number;
  movimiento: string; // ENTRADA, SALIDA
  tipoOperacion: string; // PRODUCCION, VENTA, COMPRA, AJUSTE
  numeroDocumento: string; // Factura/Guía
  
  // Campos específicos que mencionaste
  nombreComercial: string;
  codigo: string;
  lote: string;
  op: string;
  fechaIng: string;
  fechaProd: string;
  fechaVcto: string;
  presentacion: string;
  proveedor: string;
  proveedorId?: number;
  destinoCliente: string;
  
  // Campos numéricos
  cantidad: number;
  cuSoles: number;
  totalSoles: number;
  cuDolares: number;
  totalDolares: number;
  
  // Saldos
  saldoCantidad: number;
  saldoValorSoles: number;
  saldoValorDolares: number;
  
  productoId: number;
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