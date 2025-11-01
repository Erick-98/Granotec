import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

interface Cliente {
  codigo: string;                 // ID interno
  ruc: string;
  razonSocial: string;
  representante: string;
  telefono: string;
  email: string;
  direccion: string;
  zona: string;                   // distrito/sector
  departamento: string;
  provincia: string;
  distrito: string;
  tipoCliente: 'Minorista' | 'Mayorista' | 'Industrial' | 'Distribuidor' | 'Otro';
  rubro: string;                  // sector/actividad
  condicionesPago: 'Contado' | 'Crédito 15d' | 'Crédito 30d' | 'Crédito 45d';
  limiteCredito: number;
  estado: 'Activo' | 'Inactivo' | 'Bloqueado';
  fechaRegistro: Date | null;
  ultimaCompra: Date | null;
  notas: string;
}

@Component({
  selector: 'app-lists',
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
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './lists.component.html',
  styleUrls: ['./lists.component.scss']
})
export class AppListsComponent  {
  cliente: Cliente = {
    codigo: '',
    ruc: '',
    razonSocial: '',
    representante: '',
    telefono: '',
    email: '',
    direccion: '',
    zona: '',
    departamento: '',
    provincia: '',
    distrito: '',
    tipoCliente: 'Industrial',
    rubro: '',
    condicionesPago: 'Crédito 30d',
    limiteCredito: 0,
    estado: 'Activo',
    fechaRegistro: new Date('2024-06-10'),
    ultimaCompra: new Date('2025-10-15'),
    notas: ''
  };

  guardar() {
    this.cliente.ultimaCompra = this.cliente.ultimaCompra ?? null;
    alert('✅ Cliente guardado correctamente');
  }

  verKardexCompras() {
    alert(`📑 Ver historial de compras de: ${this.cliente.razonSocial}`);
  }
}
