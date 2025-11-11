import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from 'src/app/material.module';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AlmacenService } from 'src/app/core/services/almacen.service';
import { StorageResponse } from 'src/app/core/models/storage-response.model';
import { AppBadgeComponent } from './badge.component';

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
    private almacenService: AlmacenService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarAlmacenes();
  }

  cargarAlmacenes() {
    this.almacenService.getAll().subscribe({
      next: (resp) => (this.almacenes = resp.data ?? []),
      error: (err) => {
        console.error('Error cargando almacenes', err);
        this.snackBar.open('Error cargando almacenes', 'Cerrar', { duration: 3000 });
      },
    });
  }

  abrirDialogNuevo() {
    const ref = this.dialog.open(AppBadgeComponent, { width: '520px', maxHeight: '80vh', data: null });
    ref.afterClosed().subscribe((result) => {
      // el formulario original del componente badge hace su propio push localmente.
      // Si deseas que la creación vaya al backend, cambia AppBadgeComponent para emitir resultado o usar AlmacenService directamente.
      if (result) this.cargarAlmacenes();
    });
  }

  editar(item: StorageResponse) {
    const ref = this.dialog.open(AppBadgeComponent, { width: '520px', maxHeight: '80vh', data: item });
    ref.afterClosed().subscribe((result) => {
      if (result) this.cargarAlmacenes();
    });
  }

  eliminar(item: StorageResponse) {
    const ok = confirm(`¿Eliminar el almacén "${item.nombre}"?`);
    if (!ok) return;
    this.almacenService.delete(item.id).subscribe({
      next: () => {
        this.snackBar.open('Almacén eliminado', 'Cerrar', { duration: 2500 });
        this.cargarAlmacenes();
      },
      error: (err) => {
        console.error('Error eliminar almacen', err);
        this.snackBar.open('Error al eliminar almacén', 'Cerrar', { duration: 3000 });
      },
    });
  }
}
