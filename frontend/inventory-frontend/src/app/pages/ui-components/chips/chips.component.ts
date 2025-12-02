import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from 'src/app/material.module';

@Component({
  selector: 'app-chips',
  standalone: true,
  imports: [CommonModule, FormsModule, MaterialModule],
  templateUrl: './chips.component.html',
  styleUrls: ['./chips.component.scss'],
})
export class AppChipsComponent {
  proveedor = {
    nombre: '',
    ruc: '',
    contacto: '',
    telefono: '',
    email: '',
    direccion: '',
    observaciones: '',
    tipo: '',
    condicionesPago: '',
    moneda: '',
    estado: 'activo',
  };

  proveedores: any[] = [];
  confirmacion = false;
  columnas: string[] = ['nombre', 'ruc', 'contacto', 'telefono', 'email', 'tipo', 'estado'];

  guardarProveedor() {
    if (!this.proveedor.nombre || !this.proveedor.ruc) return;

    this.proveedores.push({ ...this.proveedor });
    this.confirmacion = true;

    setTimeout(() => (this.confirmacion = false), 3000);

    this.proveedor = {
      nombre: '',
      ruc: '',
      contacto: '',
      telefono: '',
      email: '',
      direccion: '',
      observaciones: '',
      tipo: '',
      condicionesPago: '',
      moneda: '',
      estado: 'activo',
    };
  }

  cancelar() {
    this.proveedor = {
      nombre: '',
      ruc: '',
      contacto: '',
      telefono: '',
      email: '',
      direccion: '',
      observaciones: '',
      tipo: '',
      condicionesPago: '',
      moneda: '',
      estado: 'activo',
    };
  }
}
