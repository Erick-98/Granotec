import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CrudListComponent, CrudColumn } from 'src/app/components/crud-list/crud-list.component';
import { MaterialModule } from '../../material.module';
import { ProveedorService } from 'src/app/core/services/proveedor.service';
import { ProveedorResponse } from 'src/app/core/models/proveedor-response.model';
import { MatDialog } from '@angular/material/dialog';
import { ProveedorDialogComponent } from 'src/app/components/proveedor-modal/proveedor-dialog.component';

@Component({
  selector: 'app-proveedor',
  templateUrl: './proveedor.component.html',
  styleUrls: ['./proveedor.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [MaterialModule, CrudListComponent]
})
export class ProveedorComponent implements OnInit {
  items: ProveedorResponse[] = [];

  columns: CrudColumn[] = [
    { field: 'nombre', label: 'Razón Social', type: 'text' },
    { field: 'tipoDocumento', label: 'Tipo Doc.', type: 'text' },
    { field: 'documento', label: 'Documento', type: 'text' },
    { field: 'telefono', label: 'Teléfono', type: 'text' },
    { field: 'email', label: 'Email', type: 'text' },
    { field: 'moneda', label: 'Moneda', type: 'text' },
    { field: 'condicionPago', label: 'Condición Pago', type: 'text' },
  ];

  constructor(
    private dialog: MatDialog,
    private proveedorService: ProveedorService
  ) {}

  ngOnInit(): void {
    this.getProveedores();
  }

  getProveedores(): void {
    this.proveedorService.getAll().subscribe({
      next: (response) => (this.items = response?.data || []),
      error: (err) => console.error('Error cargando proveedores', err),
    });
  }

  onAdd(): void {
    const dialogRef = this.dialog.open(ProveedorDialogComponent, {
      width: '700px'
    });
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) this.getProveedores();
    });
  }

  onEdit(proveedor: ProveedorResponse): void {
    const dialogRef = this.dialog.open(ProveedorDialogComponent, {
      width: '700px',
      data: proveedor,
    });
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) this.getProveedores();
    });
  }

  onDelete(proveedor: ProveedorResponse): void {
    if (!proveedor?.id) {
      console.error('Proveedor sin id, no se puede eliminar', proveedor);
      return;
    }

    const ok = confirm(
      `¿Seguro que deseas eliminar al proveedor "${proveedor.nombre}"?`
    );
    if (!ok) return;

    this.proveedorService.delete(proveedor.id).subscribe({
      next: () => this.getProveedores(),
      error: (err) => console.error('Error eliminando proveedor', err),
    });
  }
}

