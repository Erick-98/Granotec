import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { AsyncPipe, CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, FormsModule, NgForm, Validators, ReactiveFormsModule, FormControl } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MaterialModule } from 'src/app/material.module';
import { TablerIconsModule } from "angular-tabler-icons";
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router, RouterLink } from '@angular/router';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
	import {map, startWith} from 'rxjs/operators';
import { ProveedorService } from 'src/app/core/services/proveedor.service';
import { ProveedorResponse } from 'src/app/core/models/proveedor-response.model';
import { ProductoService } from 'src/app/core/services/producto.service';
import { ProductoResponse } from 'src/app/core/models/producto-response.model';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {provideNativeDateAdapter} from '@angular/material/core';



@Component({
  selector: 'app-orden-compra-form',
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [FormsModule, HttpClientModule, MaterialModule, TablerIconsModule, RouterLink,CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatAutocompleteModule,
    MatProgressSpinnerModule,
  	AsyncPipe,
    MatDatepickerModule],
  templateUrl: './orden-compra-form.component.html',
  styleUrls: ['./orden-compra-form.component.scss'],
})
export class OrdenCompraFormComponent implements OnInit{

  invoiceForm!: FormGroup;
  dataSource = new MatTableDataSource<any>();
  displayedColumns: string[] = [
    '#', 'producto', 'cantidad', 'precio_unitario', 'subtotal',
    'cantidad_recibida', 'lote', 'fecha_prod', 'fecha_venc', 'acciones'
  ];

  invoiceNumber: string = ""; // Dato fijo para el ejemplo
  vatPercentage: number = 18;   // Porcentaje de IVA/VAT (9% según tu imagen)

  subTotal: number = 0;
  vatAmount: number = 0;
  grandTotal: number = 0;

  firstControl = new FormControl('');
  proveedores: ProveedorResponse[] = [];
  filteredOptions: Observable<ProveedorResponse[]>;
  
  productos: ProductoResponse[] = [];
  filteredProductos: Map<number, Observable<ProductoResponse[]>> = new Map();



  constructor(
    private fb: FormBuilder,
    private router: Router,
    private proveedorService: ProveedorService,
    private productoService: ProductoService
  ) { }

  ngOnInit(): void {
    this.invoiceForm = this.fb.group({
      invoiceNumber: [''],
      billFrom: [''],
      billTo: [''],
      fromAddress: [''],
      items: this.fb.array([]) 
    });

    // Cargar proveedores y productos primero
    this.loadProveedores();
    this.loadProductos();

    // Agregar ítem inicial después de un pequeño delay para asegurar carga
    setTimeout(() => {
      this.addItem();
    }, 100);

    // Suscribe a los cambios del formulario para recalcular totales
    const itemsControl = this.invoiceForm.get('items') as FormArray | null;
    if (itemsControl) {
      itemsControl.valueChanges.subscribe(() => {
        this.calculateTotals();
      });
    }

    // Inicializa los cálculos con el ítem de ejemplo
    this.calculateTotals();

    this.filteredOptions = this.firstControl.valueChanges.pipe(
     startWith(''),
     map((value) => {
       const filterValue = typeof value === 'string' ? value : (value as any)?.nombre || '';
       return this._filter(filterValue);
     })
    );
    
  }

  loadProveedores(): void {
    this.proveedorService.getAll().subscribe({
      next: (response) => {
        if (response.data) {
          this.proveedores = response.data;
        }
      },
      error: (error) => {
        console.error('Error al cargar proveedores:', error);
      }
    });
  }

  private _filter(value: string): ProveedorResponse[] {
    const filterValue = value.toLowerCase();
    return this.proveedores.filter((proveedor) => 
      proveedor.nombre.toLowerCase().includes(filterValue)
    );
  }

  displayProveedorFn(proveedor: ProveedorResponse): string {
    return proveedor && proveedor.nombre ? proveedor.nombre : '';
  }

  loadProductos(): void {
    this.productoService.getAll().subscribe({
      next: (response) => {
        console.log('Respuesta de productos:', response);
        if (response.data && (response.data as any).content) {
          this.productos = (response.data as any).content;
          console.log('Productos cargados:', this.productos.length);
        }
      },
      error: (error) => {
        console.error('Error al cargar productos:', error);
      }
    });
  }

  getFilteredProductos(index: number): Observable<ProductoResponse[]> {
    if (!this.filteredProductos.has(index)) {
      const control = this.items.at(index).get('productoControl');
      if (control) {
        const filtered = control.valueChanges.pipe(
          startWith(''),
          map((value) => {
            if (!this.productos || this.productos.length === 0) {
              return [];
            }
            const filterValue = typeof value === 'string' ? value : (value as any)?.name || '';
            return this._filterProductos(filterValue);
          })
        );
        this.filteredProductos.set(index, filtered);
      }
    }
    return this.filteredProductos.get(index) || new Observable<ProductoResponse[]>();
  }

  private _filterProductos(value: string): ProductoResponse[] {
    if (!this.productos || !Array.isArray(this.productos)) {
      console.log('Productos no disponibles aún');
      return [];
    }
    console.log('Filtrando productos con:', value, 'Total productos:', this.productos.length);
    const filterValue = value.toLowerCase();
    const filtered = this.productos.filter((producto) => 
      producto.name.toLowerCase().includes(filterValue)
    );
    console.log('Productos filtrados:', filtered.length);
    return filtered;
  }

  displayProductoFn(producto: ProductoResponse): string {
    return producto && producto.name ? producto.name : '';
  }

  onProductoSelected(index: number, producto: ProductoResponse): void {
    const itemGroup = this.items.at(index) as FormGroup;
    itemGroup.patchValue({
      producto: producto.id,
      productoControl: producto
    });
  }

  




  // Getter para acceder al FormArray 'items' fácilmente
  get items(): FormArray {
    return this.invoiceForm.get('items') as FormArray;
  }

  // Define la estructura de un ítem
  createItem(): FormGroup {
    return this.fb.group({
      producto: [null],
      productoControl: [null],
      cantidad: [0],
      precio_unitario: [0],
      subtotal: [{ value: 0, disabled: true }],
      cantidad_recibida: [0],
      lote: [''],
      fecha_prod: [''],
      fecha_venc: ['']
    });
  }


  addItem(): void {
    const newItem = this.createItem(); 
    this.items.push(newItem);
    this.updateDataSource();
  }


  removeItem(index: number): void {
    if (this.items.length > 1) {
      this.items.removeAt(index);
      this.updateDataSource();
    }
  }

    updateDataSource() {
    this.dataSource.data = this.items.controls.map(control => control.value);
  }


  calculateSubtotal(item: any) {
    const itemGroup = item as FormGroup;
    const cantidad = itemGroup.get('cantidad')?.value || 0;
    const precio = itemGroup.get('precio_unitario')?.value || 0;
    itemGroup.get('subtotal')?.setValue(cantidad * precio);
    this.calculateTotals();
  }



  calculateTotals(): void {
    this.subTotal = 0;

    this.items.controls.forEach(control => {
      const itemGroup = control as FormGroup;
      const price = itemGroup.get('precio_unitario')?.value || 0;
      const quantity = itemGroup.get('cantidad')?.value || 0;
      
      const rowTotal = price * quantity;
      

      itemGroup.get('subtotal')?.setValue(rowTotal, { emitEvent: false });
      
      this.subTotal += rowTotal;
    });

    this.vatAmount = this.subTotal * (this.vatPercentage / 100);
    this.grandTotal = this.subTotal + this.vatAmount;
  }

  saveInvoice(): void {
    if (this.invoiceForm.valid) {
      console.log('Factura guardada:', this.invoiceForm.value);
    } else {
      console.error('El formulario no es válido.');
    }
  }

  cancelInvoice(): void {
    this.router.navigate(['/compras/orden-compra-list']);
    // Lógica para volver a la página anterior o resetear el formulario
  }

}