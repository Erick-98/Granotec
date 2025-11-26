import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from 'src/app/material.module';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StorageService } from 'src/app/core/services/almacen.service'; // ← Ruta corregida
import { StorageResponse } from 'src/app/core/models/storage-response.model'; // ← Ruta corregida
import { StorageModalComponent } from 'src/app/components/storage-modal/storage-modal.component'; // ← Modal correcto

@Component({
  selector: 'app-badge-list',
  standalone: true,
  imports: [CommonModule, MaterialModule],
  templateUrl: './badge-list.component.html',
})
export class BadgeListComponent implements OnInit {
  almacenes: StorageResponse[] = [];
  displayedColumns: string[] = ['nombre', 'descripcion', 'acciones'];

  constructor(
    private dialog: MatDialog,
    private storageService: StorageService, // ← Nombre correcto
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarAlmacenes();
  }

  cargarAlmacenes() {
    this.storageService.getStorages().subscribe({
      next: (resp: any) => {
        // Manejar diferentes formatos de respuesta
        if (resp && resp.data && Array.isArray(resp.data)) {
          this.almacenes = resp.data;
        } else if (Array.isArray(resp)) {
          this.almacenes = resp;
        } else {
          this.almacenes = [];
          console.warn('Formato de respuesta inesperado:', resp);
        }
      },
      error: (err: any) => {
        console.error('Error cargando almacenes', err);
        this.snackBar.open('Error cargando almacenes', 'Cerrar', { duration: 3000 });
      },
    });
  }

  abrirDialogNuevo() {
    const ref = this.dialog.open(StorageModalComponent, { 
      width: '520px', 
      maxHeight: '80vh', 
      data: null 
    });
    
    ref.afterClosed().subscribe((result: boolean) => {
      if (result) this.cargarAlmacenes();
    });
  }

  editar(item: StorageResponse) {
    const ref = this.dialog.open(StorageModalComponent, { 
      width: '520px', 
      maxHeight: '80vh', 
      data: { storage: item } 
    });
    
    ref.afterClosed().subscribe((result: boolean) => {
      if (result) this.cargarAlmacenes();
    });
  }

  eliminar(item: StorageResponse) {
    const ok = confirm(`¿Eliminar el almacén "${item.nombre}"?`);
    if (!ok) return;
    
    this.storageService.deleteStorage(item.id).subscribe({
      next: () => {
        this.snackBar.open('Almacén eliminado', 'Cerrar', { duration: 2500 });
        this.cargarAlmacenes();
      },
      error: (err: any) => {
        console.error('Error eliminar almacen', err);
        this.snackBar.open('Error al eliminar almacén', 'Cerrar', { duration: 3000 });
      },
    });
  }
}