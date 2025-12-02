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
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import { ProveedorService } from 'src/app/core/services/proveedor.service';
import { ProveedorResponse } from 'src/app/core/models/proveedor-response.model';
import { ProductoService } from 'src/app/core/services/producto.service';
import { ProductoResponse } from 'src/app/core/models/producto-response.model';
import { StorageService } from 'src/app/core/services/almacen.service';
import { StorageResponse } from 'src/app/core/models/storage-response.model';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {provideNativeDateAdapter, MAT_DATE_FORMATS} from '@angular/material/core';
import { CompraService } from 'src/app/core/services/compra.service';
import { CompraRequest, CompraDetalleRequest } from 'src/app/core/models/compra-request.model';
import { MatSnackBar } from '@angular/material/snack-bar';

// Formato personalizado para mostrar fechas como dd/MM/yy
export const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'dd/MM/yy',
  },
  display: {
    dateInput: 'dd/MM/yy',
    monthYearLabel: 'MMM yyyy',
    dateA11yLabel: 'dd/MM/yy',
    monthYearA11yLabel: 'MMMM yyyy',
  }
};



@Component({
  selector: 'app-orden-compra-form',
  standalone: true,
  providers: [
    provideNativeDateAdapter(),
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS }
  ],
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

  firstControl = new FormControl<string | ProveedorResponse>('');
  proveedores: ProveedorResponse[] = [];
  filteredOptions: Observable<ProveedorResponse[]>;
  // flags to force showing all options when opening panel
  private showAllProveedores = false;
  
  // Almacenes
  almacenes: StorageResponse[] = [];
  filteredAlmacenes: Observable<StorageResponse[]> | undefined;
  private showAllAlmacenes = false;
  
  productos: ProductoResponse[] = [];
  filteredProductos: Map<number, Observable<ProductoResponse[]>> = new Map();

  // Variables para modo edición
  isEditMode = false;
  compraId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private proveedorService: ProveedorService,
    private productoService: ProductoService,
    private almacenService: StorageService,
    private compraService: CompraService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.invoiceForm = this.fb.group({
      invoiceNumber: ['', Validators.required],
      billFrom: [''],
      billTo: ['', Validators.required],
      fromAddress: [''],
      fecha: ['', Validators.required],
      items: this.fb.array([]) 
    });

    // Cargar proveedores y productos primero
    this.loadProveedores();
    this.loadProductos();
    this.loadAlmacenes();

    // Verificar si estamos en modo edición
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.compraId = +params['id'];
        this.loadCompraData(this.compraId);
      } else {
        // Solo agregar ítem inicial si no estamos cargando datos
        setTimeout(() => {
          this.addItem();
        }, 100);
      }
    });

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
       if (this.showAllProveedores) {
         this.showAllProveedores = false;
         return this.proveedores;
       }
       const filterValue = typeof value === 'string' ? value : (value as any)?.nombre || '';
       return this._filter(filterValue);
     })
    );
    // Inicializar filtro de almacenes (usa el control 'billTo' del form)
    const billToControl = this.invoiceForm.get('billTo') as FormControl;
    this.filteredAlmacenes = billToControl.valueChanges.pipe(
      startWith(''),
      map((value) => {
        if (this.showAllAlmacenes) {
          this.showAllAlmacenes = false;
          return this.almacenes;
        }
        const filterValue = typeof value === 'string' ? value : (value as any)?.nombre || '';
        return this._filterAlmacenes(filterValue);
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

  // Almacenes
  loadAlmacenes(): void {
    this.almacenService.getAll().subscribe({
      next: (response) => {
        if (response && response.data) {
          this.almacenes = response.data;
        }
      },
      error: (err) => console.error('Error cargando almacenes:', err)
    });
  }

  private _filterAlmacenes(value: string): StorageResponse[] {
    if (!this.almacenes || !Array.isArray(this.almacenes)) return [];
    const filterValue = value.toLowerCase();
    return this.almacenes.filter(a => a.nombre.toLowerCase().includes(filterValue));
  }

  displayAlmacenFn(almacen: StorageResponse): string {
    return almacen && almacen.nombre ? almacen.nombre : '';
  }

  onAlmacenSelected(almacen: StorageResponse): void {
    // Guardar el objeto seleccionado en el control 'billTo'
    const control = this.invoiceForm.get('billTo');
    control?.setValue(almacen);
  }

  // Open handlers used from template: set flag to show all and open panel
  openProveedorPanel(trigger: any) {
    this.showAllProveedores = true;
    try { trigger.openPanel(); } catch (e) { }
  }

  openAlmacenPanel(trigger: any) {
    this.showAllAlmacenes = true;
    try { trigger.openPanel(); } catch (e) { }
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
    // Guardamos temporalmente el producto seleccionado para mostrar nombre
    itemGroup.patchValue({
      producto: producto.id,
      productoControl: producto
    });

    // Llamamos al endpoint que devuelve el producto con precio promedio
    this.productoService.getByIdWithPrice(producto.id).subscribe({
      next: (response) => {
        if (response && response.data) {
          const prodWithPrice = response.data as ProductoResponse & { precioPromedioPonderado?: number };
          // Reemplazamos el control por la versión que trae precio
          itemGroup.patchValue({
            producto: prodWithPrice.id,
            productoControl: prodWithPrice
          });
          const precioProm = prodWithPrice.precioPromedioPonderado;
          if (precioProm !== undefined && precioProm !== null) {
            const precioNum = Number(precioProm);
            itemGroup.get('precio_unitario')?.setValue(Number(precioNum.toFixed(2)));
            this.calculateSubtotal(itemGroup);
            this.updateDataSource();
          }
        }
      },
      error: (err) => {
        console.error('Error obteniendo producto con precio:', err);
      }
    });
  }

  // Formatea el precio_unitario del item a 2 decimales y recalcula subtotal/ totales
  formatPrecio(index: number): void {
    const itemGroup = this.items.at(index) as FormGroup;
    const control = itemGroup.get('precio_unitario');
    if (!control) return;
    const raw = control.value;
    const num = parseFloat(raw);
    if (isNaN(num)) {
      control.setValue(0, { emitEvent: false });
    } else {
      // Guardamos como número con dos decimales
      const fixed = Number(num.toFixed(2));
      control.setValue(fixed, { emitEvent: false });
    }
    // Recalcular subtotal/totales
    this.calculateSubtotal(itemGroup);
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
    if (!this.invoiceForm.valid) {
      this.snackBar.open('Por favor completa todos los campos requeridos', 'Cerrar', { duration: 3000 });
      return;
    }

    // Validar que firstControl (proveedor) tenga valor
    const proveedorValue = this.firstControl.value;
    if (!proveedorValue || typeof proveedorValue === 'string') {
      this.snackBar.open('Selecciona un proveedor válido', 'Cerrar', { duration: 3000 });
      return;
    }
    const proveedorObj = proveedorValue as ProveedorResponse;

    // Validar que billTo (almacén) tenga valor
    const almacenValue = this.invoiceForm.get('billTo')?.value;
    if (!almacenValue || typeof almacenValue === 'string') {
      this.snackBar.open('Selecciona un almacén válido', 'Cerrar', { duration: 3000 });
      return;
    }
    const almacenObj = almacenValue as StorageResponse;

    // Validar que haya al menos un item válido
    if (this.items.length === 0) {
      this.snackBar.open('Agrega al menos un producto', 'Cerrar', { duration: 3000 });
      return;
    }

    // Construir el payload
    const detalles: CompraDetalleRequest[] = [];
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items.at(i).value;
      const productoId = item.producto;
      
      if (!productoId) {
        this.snackBar.open(`Selecciona un producto en la fila ${i + 1}`, 'Cerrar', { duration: 3000 });
        return;
      }

      const cantidadOrdenada = Number(item.cantidad) || 0;
      const precioUnitario = Number(item.precio_unitario) || 0;
      const cantidadRecibida = Number(item.cantidad_recibida) || 0;

      if (cantidadOrdenada <= 0) {
        this.snackBar.open(`La cantidad ordenada debe ser mayor a 0 en la fila ${i + 1}`, 'Cerrar', { duration: 3000 });
        return;
      }

      const detalle: CompraDetalleRequest = {
        productoId: productoId,
        cantidadOrdenada: cantidadOrdenada,
        precioUnitario: precioUnitario,
        cantidadRecibida: cantidadRecibida
      };

      // Solo agregar campos opcionales si tienen valor
      if (item.lote && item.lote.trim() !== '') {
        detalle.lote = item.lote.trim();
      }
      if (item.fecha_prod && item.fecha_prod !== '') {
        detalle.fechaProduccion = item.fecha_prod;
      }
      if (item.fecha_venc && item.fecha_venc !== '') {
        detalle.fechaVencimiento = item.fecha_venc;
      }

      detalles.push(detalle);
    }

    // Formatear fecha para el backend (yyyy-MM-dd ISO format)
    const fechaValue = this.invoiceForm.get('fecha')?.value;
    let fechaFormatted: string | undefined = undefined;
    if (fechaValue instanceof Date) {
      const year = fechaValue.getFullYear();
      const month = String(fechaValue.getMonth() + 1).padStart(2, '0');
      const day = String(fechaValue.getDate()).padStart(2, '0');
      fechaFormatted = `${year}-${month}-${day}`;
    } else if (typeof fechaValue === 'string' && fechaValue.trim() !== '') {
      fechaFormatted = fechaValue;
    }

    const payload: CompraRequest = {
      numeroFactura: this.invoiceForm.get('invoiceNumber')?.value || '',
      proveedorId: proveedorObj.id,
      almacenId: almacenObj.id,
      detalles: detalles
    };

    // Solo agregar fecha si tiene valor
    if (fechaFormatted) {
      payload.fecha = fechaFormatted;
    }

    console.log('Payload a enviar:', payload);

    // Llamar al servicio para crear o actualizar la orden de compra
    const request$ = this.isEditMode && this.compraId
      ? this.compraService.update(this.compraId, payload)
      : this.compraService.create(payload);

    request$.subscribe({
      next: (response) => {
        console.log('Orden de compra guardada:', response);
        const mensaje = this.isEditMode ? 'Orden de compra actualizada exitosamente' : 'Orden de compra registrada exitosamente';
        this.snackBar.open(mensaje, 'Cerrar', { duration: 3000 });
        // Redirigir a la lista de compras
        setTimeout(() => {
          this.router.navigate(['/compras/orden-compra-list']);
        }, 1500);
      },
      error: (err) => {
        console.error('Error al guardar orden de compra:', err);
        const mensaje = this.isEditMode ? 'Error al actualizar la orden de compra' : 'Error al registrar la orden de compra';
        this.snackBar.open(mensaje + ': ' + err.message, 'Cerrar', { duration: 5000 });
      }
    });
  }

  loadCompraData(id: number): void {
    this.compraService.getById(id).subscribe({
      next: (compra) => {
        console.log('Datos de compra cargados:', compra);
        
        // Llenar los campos del formulario
        this.invoiceForm.patchValue({
          invoiceNumber: compra.numero || '',
          fecha: compra.fecha ? new Date(compra.fecha) : null
        });

        // Buscar y asignar proveedor
        if (compra.proveedorId) {
          setTimeout(() => {
            const proveedor = this.proveedores.find(p => p.id === compra.proveedorId);
            console.log('Proveedor encontrado:', proveedor);
            if (proveedor) {
              (this.firstControl as any).setValue(proveedor);
              this.invoiceForm.patchValue({ billFrom: proveedor });
            }
          }, 500);
        }

        // Buscar y asignar almacén
        if (compra.almacenId) {
          setTimeout(() => {
            const almacen = this.almacenes.find(a => a.id === compra.almacenId);
            if (almacen) {
              this.invoiceForm.patchValue({ billTo: almacen });
            }
          }, 500);
        }

        // Limpiar items existentes
        while (this.items.length > 0) {
          this.items.removeAt(0);
        }

        // Agregar items de la compra
        if (compra.detalles && compra.detalles.length > 0) {
          setTimeout(() => {
            compra.detalles?.forEach(detalle => {
              console.log('Detalle cargado:', detalle);
              // Buscar el producto completo
              const productoCompleto = this.productos.find(p => p.id === detalle.productoId);
              
              const itemGroup = this.fb.group({
                producto: [detalle.productoId || null],
                productoControl: [productoCompleto || null],
                cantidad: [detalle.cantidad || 0],
                precio_unitario: [detalle.precioUnitario || 0],
                subtotal: [{ value: detalle.subtotal || 0, disabled: true }],
                cantidad_recibida: [detalle.cantidad || 0],
                lote: [detalle.codigoLote || ''],
                fecha_prod: [detalle.fechaProduccion || ''],
                fecha_venc: [detalle.fechaVencimiento || '']
              });
              console.log('ItemGroup creado con fechas:', itemGroup.value);

              this.items.push(itemGroup);
              
              // Calcular subtotal
              const cantidad = detalle.cantidad || 0;
              const precio = detalle.precioUnitario || 0;
              itemGroup.patchValue({ subtotal: cantidad * precio });
            });
            
            this.updateDataSource();
            this.calculateTotals();
          }, 800);
        }
      },
      error: (err) => {
        console.error('Error al cargar datos de compra:', err);
        this.snackBar.open('Error al cargar los datos de la orden de compra', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/compras/orden-compra-list']);
      }
    });
  }

  cancelInvoice(): void {
    this.router.navigate(['/compras/orden-compra-list']);
    // Lógica para volver a la página anterior o resetear el formulario
  }

}
