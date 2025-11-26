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
  { field: 'nombre', label: 'Nombre', type: 'text' },
  { field: 'tipoDocumento', label: 'Tipo Doc.', type: 'text' },
  { field: 'documento', label: 'Documento', type: 'text' },
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

  onDelete(id: number): void {
    this.proveedorService.delete(id).subscribe(() => this.getProveedores());
  }
}
