// src/app/pages/almacen/almacen.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { StorageService } from 'src/app/core/services/almacen.service';
import { StorageResponse } from 'src/app/core/models/storage-response.model';

import { CrudListComponent, CrudColumn } from 'src/app/components/crud-list/crud-list.component';
import { StorageModalComponent } from 'src/app/components/storage-modal/storage-modal.component';

@Component({
  selector: 'app-almacen',
  templateUrl: './almacen.component.html',
  styleUrls: ['./almacen.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    CrudListComponent,
    MatDialogModule
  ]
})
export class AlmacenComponent implements OnInit {

  items: StorageResponse[] = [];

  columns: CrudColumn[] = [
    { field: 'nombre', label: 'Nombre', type: 'text' },
    { field: 'descripcion', label: 'Descripción', type: 'text' }
  ];

  constructor(
    private storageService: StorageService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadStorages();
  }

  loadStorages(): void {
    this.storageService.getAll().subscribe({
      next: (response) => {
        this.items = response?.data || [];
      },
      error: (err) => {
        console.error('Error cargando almacenes', err);
        this.items = [];
      }
    });
  }

  onAdd(): void {
    const dialogRef = this.dialog.open(StorageModalComponent, {
      width: '500px',
      data: { storage: null }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadStorages();
      }
    });
  }

  onEdit(storage: StorageResponse): void {
    const dialogRef = this.dialog.open(StorageModalComponent, {
      width: '500px',
      data: { storage }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadStorages();
      }
    });
  }

  onDelete(storage: StorageResponse): void {
    if (confirm(`¿Seguro que deseas eliminar el almacén "${storage.nombre}"?`)) {
      this.storageService.deleteStorage(storage.id).subscribe({
        next: () => this.loadStorages(),
        error: (error) => {
          console.error('Error eliminando almacén:', error);
          alert('Error al eliminar almacén: ' + (error.error?.message || error.message));
        }
      });
    }
  }
}
