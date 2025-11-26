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
    razonSocial: '',        // ← CORRECTO
    tipoDocumento: 'RUC',   // ← CORRECTO
    nroDocumento: '',       // ← CORRECTO
    telefono: '',
    email: '',
    direccion: '',
    notas: '',              // ← CORRECTO
    condicionPago: 'EFECTIVO', // ← CORRECTO
    moneda: 'PEN'
  };

  proveedores: any[] = [];
  confirmacion = false;
  columnas = ['razonSocial', 'nroDocumento', 'telefono', 'email', 'condicionPago', 'moneda'];

  guardarProveedor() {
    // CORRIGE las validaciones y propiedades
    if (!this.proveedor.razonSocial || !this.proveedor.nroDocumento) return;

    this.proveedores.push({ ...this.proveedor });
    this.confirmacion = true;

    setTimeout(() => (this.confirmacion = false), 3000);

    // CORRIGE el reset con las nuevas propiedades
    this.proveedor = {
      razonSocial: '',        // ← USA las nuevas propiedades
      tipoDocumento: 'RUC',   
      nroDocumento: '',       
      telefono: '',
      email: '',
      direccion: '',
      notas: '',              
      condicionPago: 'EFECTIVO', 
      moneda: 'PEN'
    };
  }

  cancelar() {
    // CORRIGE el reset con las nuevas propiedades
    this.proveedor = {
      razonSocial: '',        // ← USA las nuevas propiedades
      tipoDocumento: 'RUC',   
      nroDocumento: '',       
      telefono: '',
      email: '',
      direccion: '',
      notas: '',              
      condicionPago: 'EFECTIVO', 
      moneda: 'PEN'
    };
  }
}