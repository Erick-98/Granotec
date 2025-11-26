import { Component, ViewEncapsulation } from '@angular/core';
import { CrudListComponent, CrudColumn } from 'src/app/components/crud-list/crud-list.component';
import { MaterialModule } from '../../material.module';
import { StorageService } from 'src/app/core/services/almacen.service'; // ← Cambiado a StorageService
import { MatDialog } from '@angular/material/dialog';
import { StorageModalComponent } from 'src/app/components/storage-modal/storage-modal.component';
import { StorageResponse } from 'src/app/core/models/storage-response.model';

@Component({
  selector: 'app-almacen',
  templateUrl: './almacen.component.html',
  styleUrls: ['./almacen.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [MaterialModule, CrudListComponent]
})
export class AlmacenComponent {
  items: StorageResponse[] = []; // ← Mejor tipado
  columns: CrudColumn[] = [
    { field: 'nombre', label: 'Nombre', type: 'text' },
    { field: 'descripcion', label: 'Descripción', type: 'text' }
  ];

  constructor(
    private storageService: StorageService, // ← Cambiado a StorageService
    private dialog: MatDialog
  ) {
    this.load();
  }

  load() {
    this.storageService.getStorages().subscribe({ // ← Cambiado a getStorages()
      next: (res: any) => { // ← CORREGIDO: agregar tipo
        // Manejar diferentes formatos de respuesta
        if (res && res.data && Array.isArray(res.data)) {
          this.items = res.data;
        } else if (Array.isArray(res)) {
          this.items = res;
        } else {
          this.items = [];
          console.warn('Formato de respuesta inesperado:', res);
        }
      },
      error: (err: Error) => console.error('Almacen load error', err)
    });
  }

  onAdd() {
    const dialogRef = this.dialog.open(StorageModalComponent, {
      width: '500px',
      data: {} // Sin datos para crear nuevo
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.load(); // Recargar si se creó exitosamente
      }
    });
  }

  onEdit(item: StorageResponse) {
    const dialogRef = this.dialog.open(StorageModalComponent, {
      width: '500px', 
      data: { storage: item } // Enviar datos del almacén a editar
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.load(); // Recargar si se editó exitosamente
      }
    });
  }

  onDelete(item: StorageResponse) {
    if (confirm(`¿Está seguro de eliminar el almacén "${item.nombre}"?`)) {
      this.storageService.deleteStorage(item.id).subscribe({ // ← Cambiado a deleteStorage()
        next: () => {
          this.load(); // Recargar después de eliminar
        },
        error: (err: Error) => {
          console.error('Error eliminando almacén:', err);
          alert('Error al eliminar almacén: ' + err.message);
        }
      });
    }
  }
}