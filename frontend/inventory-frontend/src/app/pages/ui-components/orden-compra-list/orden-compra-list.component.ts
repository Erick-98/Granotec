import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MaterialModule } from 'src/app/material.module';
import { CompraService } from 'src/app/core/services/compra.service';
import { CompraResponse } from 'src/app/core/models/compra-response.model';
import { A11yModule } from '@angular/cdk/a11y';
import { TablerIconComponent } from "angular-tabler-icons";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-orden-compra-list',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule, MaterialModule, A11yModule, TablerIconComponent],
  templateUrl: './orden-compra-list.component.html',
  styleUrls: ['./orden-compra-list.component.scss'],
})
export class OrdenCompraListComponent implements OnInit {
  // filtro de búsqueda
  searchTerm = '';

  // lista que viene del backend
  compras: CompraResponse[] = [];
  // lista filtrada para mostrar
  comprasFiltradas: any[] = [];


  constructor(
    private compraService: CompraService, 
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarCompras();
  }

  cargarCompras() {
    this.compraService.getAll().subscribe({
      next: (data) => {
        console.log('Compras recibidas del backend:', data);
        this.compras = data ?? [];
        this.aplicarFiltro();
      },
      error: (err) => {
        console.error('Error al cargar compras', err);
      },
    });
  }

  aplicarFiltro() {
    const term = this.searchTerm.toLowerCase().trim();
    if (!term) {
      this.comprasFiltradas = [...this.compras];
      return;
    }

    this.comprasFiltradas = this.compras.filter((c) =>
      [
        c.numero,
        c.proveedorNombre,
        c.almacenNombre,
        c.estado,
        c.id,
      ]
        .filter(Boolean)
        .some((campo: any) => campo.toString().toLowerCase().includes(term))
    );
  }

  // navegación al formulario que ya hicimos
  agregarOrden() {
    this.router.navigate(['/compras/orden-compra']);
  }

  editar(compra: CompraResponse) {
    if (!compra.id) {
      console.error('ID de compra no disponible para editar', compra);
      return;
    }
    this.router.navigate(['/compras/orden-compra', compra.id]);
  }

  verDetalle(compra: CompraResponse) {
    console.log('Ver detalle - Compra completa:', compra);
    if (!compra.id) {
      console.error('ID de compra no disponible', compra);
      return;
    }
    this.router.navigate(['/compras/orden-compra/view', compra.id]);
  }

  eliminar(compra: CompraResponse) {
    if (!compra.id) {
      console.error('ID de compra no disponible para eliminar', compra);
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDeleteDialogComponent, {
      width: '400px',
      data: { 
        title: 'Eliminar Orden de Compra',
        message: `¿Está seguro de eliminar la orden de compra ${compra.numero}? Esta acción revertirá los movimientos del Kardex y actualizará el stock.`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.compraService.delete(compra.id!).subscribe({
          next: () => {
            this.snackBar.open('Orden de compra eliminada correctamente', 'Cerrar', {
              duration: 3000,
              horizontalPosition: 'end',
              verticalPosition: 'top'
            });
            this.cargarCompras();
          },
          error: (err) => {
            console.error('Error al eliminar compra', err);
            this.snackBar.open('Error al eliminar la orden de compra', 'Cerrar', {
              duration: 3000,
              horizontalPosition: 'end',
              verticalPosition: 'top'
            });
          }
        });
      }
    });
  }

  // para asignar clases según estado
  getEstadoClase(estado: string): string {
    const e = (estado || '').toLowerCase();
    if (e.includes('aprob') || e.includes('delivered')) return 'pill pill-success';
    if (e.includes('pend') || e.includes('pending')) return 'pill pill-warning';
    if (e.includes('ship') || e.includes('enviado')) return 'pill pill-info';
    return 'pill pill-default';
  }
}

// Componente de diálogo de confirmación
@Component({
  selector: 'app-confirm-delete-dialog',
  standalone: true,
  imports: [CommonModule, MaterialModule],
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <p>{{ data.message }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancelar</button>
      <button mat-raised-button color="warn" (click)="onConfirm()">Eliminar</button>
    </mat-dialog-actions>
  `,
  styles: [`
    mat-dialog-content {
      padding: 20px 0;
    }
    mat-dialog-actions {
      padding: 8px 0;
      gap: 8px;
    }
  `]
})
export class ConfirmDeleteDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string }
  ) {}

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}
