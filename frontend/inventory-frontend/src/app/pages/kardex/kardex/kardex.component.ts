import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
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


@Component({
  selector: 'app-kardex',
  standalone: true,
  imports: [    CommonModule,
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
    MatProgressSpinnerModule
],
  templateUrl: './kardex.component.html',
  styleUrls: ['./kardex.component.scss']
})
export class KardexComponent implements OnInit {

    @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filterForm: FormGroup;
  dataSource = new MatTableDataSource<any>();
  
  // COLUMNAS COMPLETAS CON TODOS LOS CAMPOS
  displayedColumns: string[] = [
    'fecha', 'almacen', 'movimiento', 'tipoOperacion', 'numeroDocumento',
    'nombreComercial', 'codigo', 'lote', 'op', 'fechaIng', 'fechaProd', 
    'fechaVcto', 'presentacion', 'proveedor', 'destinoCliente', 'cantidad', 
    'cuSoles', 'totalSoles', 'cuDolares', 'totalDolares', 'saldoCantidad'
  ];
  
  almacenes: any[] = [];
  productos: any[] = [];
  proveedores: any[] = [];
    tiposOperacion = [
  'PRODUCCION', 'VENTA', 'COMPRA', 'AJUSTE', 'TRANSFERENCIA', 
  'LABORATORIO', 'SALDO INICIAL'
];


  isLoading = false;
  showSummary = false;
  today = new Date();


  constructor(private fb: FormBuilder) {
    this.filterForm = this.createFilterForm();
  }

  ngOnInit(): void {
    //this.dataSource.data = this.demoData;

  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  createFilterForm(): FormGroup {
  // Usar fechas de 2022 para que coincida con los datos demo
  const fechaInicio = new Date('2022-01-01');
  const fechaFin = new Date('2022-12-31');
  
  return this.fb.group({
    fechaInicio: [fechaInicio.toISOString().split('T')[0]],
    fechaFin: [fechaFin.toISOString().split('T')[0]],
    almacenId: [''],
    productoId: [''],
    lote: [''],
    tipoOperacion: [''],
    proveedorId: ['']
  });
}


  applyFilters(): void {
    this.isLoading = true;
    setTimeout(() => {
      //this.dataSource.data = this.demoData;
      this.isLoading = false;
    }, 1000);
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  clearFilters(): void {
    this.filterForm.reset();
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    
    this.filterForm.patchValue({
      fechaInicio: firstDay.toISOString().split('T')[0],
      fechaFin: today.toISOString().split('T')[0]
    });
    
    this.applyFilters();
  }

  exportToExcel(): void {
    alert('Funcionalidad de exportación a Excel pendiente de implementar');
  }

  toggleView(): void {
    this.showSummary = !this.showSummary;
  }


}
