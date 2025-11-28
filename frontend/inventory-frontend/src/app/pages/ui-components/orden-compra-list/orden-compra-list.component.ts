import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MaterialModule } from 'src/app/material.module';

@Component({
  selector: 'app-orden-compra-list',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule, MaterialModule],
  templateUrl: './orden-compra-list.component.html',
  styleUrls: ['./orden-compra-list.component.scss'],
})
export class OrdenCompraListComponent implements OnInit {
  // filtro de búsqueda
  searchTerm = '';

  // lista que viene del backend
  compras: any[] = [];
  // lista filtrada para mostrar
  comprasFiltradas: any[] = [];

  private readonly apiUrl = 'http://localhost:8080/compras';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.cargarCompras();
  }

  cargarCompras() {
    this.http.get<any[]>(this.apiUrl).subscribe({
      next: (data) => {
        this.compras = data;
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
    this.router.navigate(['/ui-components/orden-compra']);
  }

  editar(compra: any) {
    console.log('Editar compra', compra);
    // Aquí luego puedes navegar a un formulario de edición
  }

  verDetalle(compra: any) {
    console.log('Ver detalle compra', compra);
    // Aquí luego puedes abrir un dialog o una página de detalle
  }

  eliminar(compra: any) {
    console.log('Eliminar compra', compra);
    // Aquí luego integras DELETE al backend con confirmación
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
