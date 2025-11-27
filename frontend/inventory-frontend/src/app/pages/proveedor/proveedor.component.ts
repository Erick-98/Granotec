import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProveedorDialogComponent } from './proveedor-dialog.component';
import { ProveedorService } from 'src/app/core/services/proveedor.service';
import { ProveedorResponse } from 'src/app/core/models/proveedor-response.model';
import { CrudListComponent } from 'src/app/components/crud-list/crud-list.component';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { CrudColumn, CrudColumnType } from 'src/app/components/crud-list/crud-list.component';



@Component({
  selector: 'app-proveedor',
  templateUrl: './proveedor.component.html',
  imports: [
    CrudListComponent,
    MatButtonModule,
    MatDialogModule,
    CommonModule
  ]
})
export class ProveedorComponent implements OnInit {
  items: ProveedorResponse[] = [];
columns: CrudColumn[] = [
  { field: 'razonSocial', label: 'Razón Social', type: 'text' },
  { field: 'tipoDocumento', label: 'Tipo Doc.', type: 'text' },
  { field: 'nroDocumento', label: 'Documento', type: 'text' },
  { field: 'telefono', label: 'Teléfono', type: 'text' },
  { field: 'email', label: 'Email', type: 'text' },
  { field: 'moneda', label: 'Moneda', type: 'text' },
  { field: 'condicionPago', label: 'Condición Pago', type: 'text' }
];



  constructor(private dialog: MatDialog, private proveedorService: ProveedorService) {}

  ngOnInit(): void {
    this.getProveedores();
  }

  getProveedores(): void {
  this.proveedorService.getAll().subscribe({
    next: (response) => this.items = response?.data || [],
    error: (err) => console.error('Error cargando proveedores', err)
  });
}


  onAdd(): void {
    const dialogRef = this.dialog.open(ProveedorDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.getProveedores();
    });
  }

  onEdit(proveedor: ProveedorResponse): void {
    const dialogRef = this.dialog.open(ProveedorDialogComponent, { data: proveedor });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.getProveedores();
    });
  }

  onDelete(proveedor: ProveedorResponse): void {
  if (!proveedor?.id) {
    console.error('Proveedor sin id, no se puede eliminar', proveedor);
    return;
  }

  const ok = confirm(`¿Seguro que deseas eliminar al proveedor "${proveedor.razonSocial}"?`);
  if (!ok) return;

  this.proveedorService.delete(proveedor.id).subscribe({
    next: () => this.getProveedores(),
    error: (err) => console.error('Error eliminando proveedor', err)
  });
}

}
