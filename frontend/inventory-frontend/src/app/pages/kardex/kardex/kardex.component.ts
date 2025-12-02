import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { KardexService } from 'src/app/core/services/kardex.service';
import { StorageService } from 'src/app/core/services/almacen.service';
import { ProductoService } from 'src/app/core/services/producto.service';
import { KardexItem } from 'src/app/core/models/kardex.model';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Observable, of } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { ProductoResponse } from 'src/app/core/models/producto-response.model';


@Component({
  selector: 'app-kardex',
  standalone: true,
  imports: [
    CommonModule,
    AsyncPipe,
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
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatAutocompleteModule
  ],
  templateUrl: './kardex.component.html',
  styleUrls: ['./kardex.component.scss']
})
export class KardexComponent implements OnInit {

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filterForm: FormGroup;
  dataSource: MatTableDataSource<KardexItem>;
  
  // COLUMNAS ADAPTADAS AL BACKEND
  readonly displayedColumns: string[] = [
    'fechaMovimiento',
    'almacenNombre',
    'tipoMovimiento',
    'tipoOperacion',
    'referencia',
    'productoNombre',
    'productoCodigo',
    'loteCodigo',
    'numeroOp',
    'fechaProduccion',
    'fechaVencimiento',
    'presentacion',
    'proveedor',
    'cantidad',
    'costoUnitarioSoles',
    'totalSoles',
    'stockAnterior',
    'stockActual'
  ];
  
  almacenes: any[] = [];
  productos: any[] = [];
  filteredProductos: Observable<ProductoResponse[]>;
  productoControl = new FormControl<string | ProductoResponse>('');
  
  readonly tiposOperacion: string[] = [
    'PRODUCCION',
    'VENTA',
    'COMPRA',
    'AJUSTE',
    'TRANSFERENCIA',
    'LABORATORIO',
    'ELIMINACION'
  ];

  isLoading = false;
  showSummary = false;
  today = new Date();
  isDataReady = false; // Flag para controlar renderizado
  
  // Paginación
  totalElements = 0;
  pageSize = 20;
  pageIndex = 0;

  constructor(
    private fb: FormBuilder,
    private kardexService: KardexService,
    private almacenService: StorageService,
    private productoService: ProductoService,
    private cdr: ChangeDetectorRef
  ) {
    this.filterForm = this.createFilterForm();
    this.dataSource = new MatTableDataSource<KardexItem>([]);
    // Inicializar filteredProductos con un observable vacío hasta que se carguen los productos
    this.filteredProductos = of([]);
  }

  ngOnInit(): void {
    console.log('ngOnInit - tiposOperacion:', this.tiposOperacion);
    console.log('ngOnInit - displayedColumns:', this.displayedColumns);
    console.log('ngOnInit - Es array tiposOperacion?', Array.isArray(this.tiposOperacion));
    this.loadAlmacenes();
    this.loadProductos();
    // Inicializar como listo desde el principio
    this.isDataReady = true;
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    // Detectar cambios y luego cargar datos
    this.cdr.detectChanges();
    
    // Llamar a applyFilters después de que la vista esté inicializada
    setTimeout(() => {
      this.applyFilters();
    }, 0);
  }

  createFilterForm(): FormGroup {
    const fechaInicio = new Date();
    fechaInicio.setMonth(fechaInicio.getMonth() - 1);
    const fechaFin = new Date();
    
    return this.fb.group({
      fechaInicio: [fechaInicio],
      fechaFin: [fechaFin],
      almacenId: [''],
      tipoOperacion: ['']
    });
  }

  setupProductoAutocomplete(): void {
    this.filteredProductos = this.productoControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.name;
        return name ? this._filterProductos(name as string) : this.productos.slice();
      })
    );
  }

  private _filterProductos(value: string): ProductoResponse[] {
    const filterValue = value.toLowerCase();
    return this.productos.filter(producto => 
      producto.name?.toLowerCase().includes(filterValue) ||
      producto.code?.toLowerCase().includes(filterValue)
    );
  }

  displayProducto = (producto: ProductoResponse): string => {
    return producto && producto.name ? producto.name : '';
  }

  onProductoSelected(producto: ProductoResponse | null): void {
    if (producto && producto.id) {
      this.filterForm.patchValue({ productoId: producto.id });
    } else {
      this.filterForm.patchValue({ productoId: '' });
    }
  }

  loadAlmacenes(): void {
    this.almacenService.getAll().subscribe({
      next: (response) => {
        console.log('Respuesta almacenes:', response);
        if (response && response.data && Array.isArray(response.data)) {
          this.almacenes = response.data;
        } else if (Array.isArray(response)) {
          this.almacenes = response;
        } else {
          console.warn('Almacenes no es un array:', response);
          this.almacenes = [];
        }
        console.log('Almacenes asignados:', this.almacenes.length);
      },
      error: (err) => {
        console.error('Error al cargar almacenes', err);
        this.almacenes = [];
      }
    });
  }

  loadProductos(): void {
    this.productoService.getAll().subscribe({
      next: (response: any) => {
        console.log('Respuesta productos:', response);
        // Verificar si tiene la estructura {mensaje, data: {content: []}}
        if (response?.data?.content && Array.isArray(response.data.content)) {
          this.productos = response.data.content;
          console.log('Productos asignados (content):', this.productos.length);
          this.setupProductoAutocomplete();
        } 
        // Verificar si data es directamente el array
        else if (response?.data && Array.isArray(response.data)) {
          this.productos = response.data;
          console.log('Productos asignados (data):', this.productos.length);
          this.setupProductoAutocomplete();
        } else {
          console.error('Estructura de respuesta inesperada:', response);
          this.productos = [];
          this.setupProductoAutocomplete();
        }
      },
      error: (err) => {
        console.error('Error al cargar productos', err);
        this.productos = [];
        this.setupProductoAutocomplete();
      }
    });
  }

  applyFilters(): void {
    this.isLoading = true;
    const formValue = this.filterForm.value;
    
    const filters: any = {
      page: this.pageIndex,
      size: this.pageSize
    };

    // Obtener el ID del producto desde productoControl
    const productoValue = this.productoControl.value;
    if (productoValue && typeof productoValue === 'object' && productoValue.id) {
      filters.productoId = productoValue.id;
    }
    
    if (formValue.almacenId) {
      filters.almacenId = formValue.almacenId;
    }
    if (formValue.fechaInicio) {
      const fecha = new Date(formValue.fechaInicio);
      filters.desde = fecha.toISOString().split('T')[0];
    }
    if (formValue.fechaFin) {
      const fecha = new Date(formValue.fechaFin);
      filters.hasta = fecha.toISOString().split('T')[0];
    }

    console.log('Filtros aplicados:', filters);

    this.kardexService.searchKardex(filters).subscribe({
      next: (response) => {
        console.log('Respuesta del kardex:', response);
        let data: KardexItem[] = [];
        
        // Verificar si la respuesta tiene la estructura esperada
        if (response && response.content && Array.isArray(response.content)) {
          data = [...response.content];
          this.totalElements = response.totalElements;
        } else if (Array.isArray(response)) {
          data = [...response];
          this.totalElements = response.length;
        } else {
          console.warn('Estructura de respuesta inesperada:', response);
          this.totalElements = 0;
        }

        // Aplicar filtro de tipoOperacion del lado del cliente
        if (formValue.tipoOperacion) {
          data = data.filter(item => item.tipoOperacion === formValue.tipoOperacion);
          console.log('Filtrado por tipoOperacion:', formValue.tipoOperacion, '- Resultados:', data.length);
        }

        this.dataSource.data = data;
        console.log('Datos asignados:', data.length, 'registros');
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar kardex', err);
        this.dataSource.data = [];
        this.totalElements = 0;
        this.isLoading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.applyFilters();
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.productoControl.setValue('');
    const fechaInicio = new Date();
    fechaInicio.setMonth(fechaInicio.getMonth() - 1);
    const fechaFin = new Date();
    
    this.filterForm.patchValue({
      fechaInicio: fechaInicio,
      fechaFin: fechaFin
    });
    
    this.pageIndex = 0;
    this.applyFilters();
  }

  exportToExcel(): void {
    alert('Funcionalidad de exportación a Excel pendiente de implementar');
  }

  toggleView(): void {
    this.showSummary = !this.showSummary;
  }

  trackByAlmacenId(index: number, item: any): any {
    return item?.id || index;
  }

  trackByProductoId(index: number, item: any): any {
    return item?.id || index;
  }
}
