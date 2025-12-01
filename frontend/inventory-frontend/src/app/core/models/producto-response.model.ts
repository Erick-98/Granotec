export interface ProductoResponse {
  id: number;
  code: string;
  name: string;
  description: string;
  unitOfMeasure: string;
  tipoPresentacion: string;
  tipoProducto: string;
  proveedor: string;
  familia: string;
  isLocked: boolean;
}