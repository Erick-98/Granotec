import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MaterialModule } from 'src/app/material.module';
import { StorageResponse } from 'src/app/core/models/storage-response.model';

@Component({
  selector: 'app-badge-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MaterialModule],
  templateUrl: './badge-dialog.component.html',
})
export class BadgeDialogComponent {
  modelo: Partial<StorageResponse> = { nombre: '', descripcion: '' };

  constructor(
    private dialogRef: MatDialogRef<BadgeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StorageResponse | null
  ) {
    if (data) {
      this.modelo = { id: data.id, nombre: data.nombre, descripcion: data.descripcion };
    }
  }

  guardar() {
    // Validación mínima
    if (!this.modelo.nombre || this.modelo.nombre.trim() === '') return;
    this.dialogRef.close({ nombre: this.modelo.nombre, descripcion: this.modelo.descripcion });
  }

  cancelar() {
    this.dialogRef.close(null);
  }
}
