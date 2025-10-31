import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Angular Material
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

// Íconos externos
import { TablerIconsModule } from 'angular-tabler-icons';

// 🧾 Interfaz de Producto
interface Producto {
  codigo: string;
  nombreComercial: string;
  categoria: string;
  presentacion: string;
  unidadMedida: string;
  lote: string;
  fechaRegistro: Date | null;
  fechaActualizacion: Date | null;
  stockActual: number;
  stockMinimo: number;
  proveedor: string;
  ubicacion: string;
  costoSoles: number;
  costoDolares: number;
  descripcion: string;
  estado: 'Activo' | 'Inactivo';
}

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    TablerIconsModule,
  ],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'],
})
export class AppMenuComponent {
  // 📦 Datos simulados (ejemplo)
  producto: Producto = {
    codigo: '',
    nombreComercial: '',
    categoria: '',
    presentacion: '',
    unidadMedida: '',
    lote: '',
    fechaRegistro: new Date('2023-05-12'),
    fechaActualizacion: new Date('2025-10-31'),
    stockActual: 0,
    stockMinimo: 0,
    proveedor: '',
    ubicacion: '',
    costoSoles: 0,
    costoDolares: 0,
    descripcion:
      '',
    estado: 'Activo',
  };

  constructor() {}

  // 📜 Acción al hacer clic en el botón
  verKardexHistorico(): void {
    alert(`📊 Mostrando Kardex histórico de: ${this.producto.nombreComercial}`);
  }

  // 🔄 Cambiar estado del producto
  toggleEstado(): void {
    this.producto.estado =
      this.producto.estado === 'Activo' ? 'Inactivo' : 'Activo';
  }

  // 💾 Simular guardado o actualización
  guardarCambios(): void {
    this.producto.fechaActualizacion = new Date();
    alert(`✅ Cambios guardados para el producto: ${this.producto.nombreComercial}`);
  }
}
