import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from 'src/app/material.module';

@Component({
  selector: 'app-badge',
  standalone: true,
  imports: [CommonModule, FormsModule, MaterialModule],
  templateUrl: './badge.component.html',
  styleUrls: ['./badge.component.scss'],
})
export class AppBadgeComponent {
  almacen = {
    codigo: '',
    nombre: '',
    responsable: '',
    tipo: '',
    capacidad: null,
    estado: 'activo',
  };

  almacenes: any[] = [];
  confirmacion = false;

  columnas: string[] = ['codigo', 'nombre', 'responsable', 'tipo', 'estado'];

  guardarAlmacen() {
    if (!this.almacen.codigo || !this.almacen.nombre) return;

    this.almacenes.push({ ...this.almacen });
    this.confirmacion = true;

    setTimeout(() => (this.confirmacion = false), 3000);

    this.almacen = {
      codigo: '',
      nombre: '',
      responsable: '',
      tipo: '',
      capacidad: null,
      estado: 'activo',
    };
  }

  cancelar() {
    this.almacen = {
      codigo: '',
      nombre: '',
      responsable: '',
      tipo: '',
      capacidad: null,
      estado: 'activo',
    };
  }
}
