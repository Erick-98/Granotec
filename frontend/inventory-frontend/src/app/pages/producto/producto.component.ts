import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProductModalComponent } from '../../components/product-modal/product-modal.component';
import { ProductService } from '../../core/services/product.service';
import { ProductResponse } from '../../core/models/product.model';
import { CrudListComponent, CrudColumn } from '../../components/crud-list/crud-list.component';

@Component({
  selector: 'app-producto',
  templateUrl: './producto.component.html',
  styleUrls: ['./producto.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    CrudListComponent,
    MatDialogModule
  ]
})
export class ProductoComponent implements OnInit {
  items: ProductResponse[] = [];

  columns: CrudColumn[] = [
    { field: 'code', label: 'Código', type: 'text' },
    { field: 'name', label: 'Nombre Comercial', type: 'text' }, // 🔹 aquí "name"
    { field: 'proveedor', label: 'Proveedor', type: 'text' },
    { field: 'familia', label: 'Familia', type: 'text' },
    { field: 'unitOfMeasure', label: 'Unidad Medida', type: 'text' },
    { field: 'tipoPresentacion', label: 'Presentación', type: 'text' },
    { field: 'tipoProducto', label: 'Tipo Producto', type: 'text' },
    { field: 'isLocked', label: 'Estado', type: 'badge' as const }
  ];


  constructor(
    private productService: ProductService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (response) => {
        // response viene de product.service -> response.data (Page)
        if (response.content) {
          // opcional: transformar isLocked a texto bonito
          this.items = response.content.map((p: ProductResponse) => ({
            ...p,
            // si quieres que el badge muestre "Bloqueado"/"Activo":
            isLocked: p.isLocked ?? false
          }));
        } else if (Array.isArray(response)) {
          this.items = response;
        } else {
          this.items = [];
        }
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.items = [];
      }
    });
  }

  onAdd(): void {
    const dialogRef = this.dialog.open(ProductModalComponent, {
      width: '700px',
      data: { product: null }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts();
      }
    });
  }

  onEdit(product: ProductResponse): void {
    const dialogRef = this.dialog.open(ProductModalComponent, {
      width: '700px',
      data: { product }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts();
      }
    });
  }

  onDelete(product: ProductResponse): void {
    if (confirm(`¿Estás seguro de eliminar el producto ${product.name}?`)) { // ← name
      this.productService.deleteProduct(product.id).subscribe({
        next: () => this.loadProducts(),
        error: (error) => {
          console.error('Error deleting product:', error);
          alert('Error al eliminar producto: ' + (error.error?.message || error.message));
        }
      });
    }
  }
}
