import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { ProductModalComponent } from '../../components/product-modal/product-modal.component';
import { ProductService } from '../../core/services/product.service';
import { ProductResponse } from '../../core/models/product.model';
import { CrudListComponent, CrudColumn } from '../../components/crud-list/crud-list.component'; // ← AGREGA CrudColumn

@Component({
  selector: 'app-producto',
  templateUrl: './producto.component.html',
  styleUrls: ['./producto.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    CrudListComponent
  ]
})
export class ProductoComponent implements OnInit {
  items: ProductResponse[] = [];
  
  // CORRIGE LAS COLUMNAS - usa tipos específicos de CrudColumnType
  columns: CrudColumn[] = [
    { field: 'code', label: 'Código', type: 'text' },
    { field: 'nombreComercial', label: 'Nombre Comercial', type: 'text' }, // ← nombreComercial
    { field: 'proveedor', label: 'Proveedor', type: 'text' },
    { field: 'familia', label: 'Familia', type: 'text' },
    { field: 'unitOfMeasure', label: 'Unidad Medida', type: 'text' },
    { field: 'tipoPresentacion', label: 'Presentación', type: 'text' },
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
        if (response.content) {
          this.items = response.content;
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
    console.log('🔵 Abriendo modal...');
    const dialogRef = this.dialog.open(ProductModalComponent, {
      width: '700px',
      data: { product: null }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('🔵 Modal cerrado, resultado:', result);
      if (result) {
        this.loadProducts();
      }
    });
  }

  onEdit(product: ProductResponse): void {
    const dialogRef = this.dialog.open(ProductModalComponent, {
      width: '700px',
      data: { product: product }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts();
      }
    });
  }

  onDelete(product: ProductResponse): void {
    if (confirm(`¿Estás seguro de eliminar el producto ${product.nombreComercial}?`)) {
      this.productService.deleteProduct(product.id).subscribe({
        next: () => {
          this.loadProducts();
        },
        error: (error) => {
          console.error('Error deleting product:', error);
          alert('Error al eliminar producto: ' + (error.error?.message || error.message));
        }
      });
    }
  }
}