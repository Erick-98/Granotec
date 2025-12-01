export interface ProductRequest {
  code: string;
  nombreComercial: string;
  description?: string;
  proveedorId?: number | null;
  tipoPresentacion: string;
  tipoProducto: string;
  unitOfMeasure: string;
  familiaId?: number | null;
  blocked?: boolean;
}

export interface ProductResponse {
  id: number;
  code: string;
  name: string;
  description?: string;

  unitOfMeasure: string;
  tipoPresentacion: string;
  tipoProducto: string;   // ✅ AGREGAR ESTA LÍNEA

  proveedorId?: number;
  proveedor?: string;

  familiaId?: number;
  familia?: string;

  isLocked?: boolean;
}



export interface FamilyProductResponse {
  id: number;
  nombre: string;
  descripcion?: string;
}

export interface VendorResponse {
  id: number;
  razonSocial: string;
}

// ENUMs para los dropdowns
export const TIPO_PRESENTACION = [
  { value: 'KG_1', label: 'KG 1' },
  { value: 'KG_5', label: 'KG 5' },
  { value: 'KG_10', label: 'KG 10' },
  { value: 'KG_12_5', label: 'KG 12.5' },
  { value: 'KG_15', label: 'KG 15' },
  { value: 'KG_20', label: 'KG 20' },
  { value: 'KG_25', label: 'KG 25' },
  { value: 'KG_50', label: 'KG 50' }
];

export const UNIT_OF_MEASURE = [
  { value: 'SACOS', label: 'Sacos' },
  { value: 'CAJAS', label: 'Cajas' },
  { value: 'BARRILES', label: 'Barriles' },
  { value: 'PAQUETES', label: 'Paquetes' }
];

export const TYPE_PRODUCT = [
  { value: 'INSUMO', label: 'Insumo' },
  { value: 'PRODUCTO_TERMINADO', label: 'Producto Terminado' },
  { value: 'PRODUCTO_INTERMEDIO', label: 'Producto Intermedio' },
  { value: 'ENVASE', label: 'Envase' }
];
