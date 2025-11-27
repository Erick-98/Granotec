export interface ProductRequest {
  code: string;
  nombreComercial: string;
  description?: string;
  proveedorId?: number;
  tipoPresentacion: string;
  unitOfMeasure: string;
  familiaId?: number;
  blocked?: boolean;
}

export interface ProductResponse {
  id: number;
  code: string;
  nombreComercial: string;
  description?: string;
  unitOfMeasure: string;
  tipoPresentacion: string;

  proveedorId?: number;   // 👈 nuevo
  proveedor?: string;

  familiaId?: number;     // 👈 nuevo
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
  // Agrega otros campos si los necesitas
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