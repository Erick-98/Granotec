import { Component, Inject, Optional, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
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
export class AppBadgeComponent implements OnInit {
  constructor(
    @Optional() @Inject(MatDialogRef) private dialogRef?: MatDialogRef<AppBadgeComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public dialogData?: any
  ) {}

  almacen = {
    codigo: '',
    nombre: '',
    descripcion: '',
    tipo: '',
    capacidad: null,
    estado: 'activo',
  };

  // Si el componente se abrió como dialog con datos para editar, prellenar
  ngOnInit(): void {
    if (this.dialogData) {
      // mapear campos si existen
      const d = this.dialogData as any;
      this.almacen = {
        codigo: d.codigo ?? '',
        nombre: d.nombre ?? '',
        descripcion: d.descripcion ?? '',
        tipo: d.tipo ?? '',
        capacidad: d.capacidad ?? null,
        estado: d.estado ?? 'activo',
      };
    }
  }

  almacenes: any[] = [];
  confirmacion = false;

  columnas: string[] = ['codigo', 'nombre', 'responsable', 'tipo', 'estado'];

  guardarAlmacen() {
    if (!this.almacen.codigo || !this.almacen.nombre) return;

    // Si estamos dentro de un dialog, no modificamos la lista local; devolvemos el resultado al llamador
    if (this.dialogRef) {
      const result = this.dialogData && (this.dialogData.id || this.dialogData.id === 0) ? { result: 'updated' } : { result: 'created' };
      this.dialogRef.close(result);
      return;
    }

    // Comportamiento normal cuando se usa en página (no modal)
    this.almacenes.push({ ...this.almacen });
    this.confirmacion = true;

    setTimeout(() => (this.confirmacion = false), 3000);

    this.almacen = {
      codigo: '',
      nombre: '',
      descripcion: '',
      tipo: '',
      capacidad: null,
      estado: 'activo',
    };
  }

  cancelar() {
    // Si es modal, cerrarlo; si no, simplemente resetear el formulario
    if (this.dialogRef) {
      this.dialogRef.close(null);
      return;
    }
    this.almacen = {
      codigo: '',
      nombre: '',
      descripcion: '',
      tipo: '',
      capacidad: null,
      estado: 'activo',
    };
  }
}
