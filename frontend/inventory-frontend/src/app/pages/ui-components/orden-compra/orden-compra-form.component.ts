import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { MaterialModule } from 'src/app/material.module';

interface OpcionBasica {
  id: number;
  nombre: string;
}

@Component({
  selector: 'app-orden-compra-form',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, MaterialModule],
  templateUrl: './orden-compra-form.component.html',
  styleUrls: ['./orden-compra-form.component.scss'],
})
export class OrdenCompraFormComponent {
  // Objeto igual a tu CompraRequest (pero definido aquí mismo)
  compra: any = {
    numero: '',
    proveedorId: 0,
    almacenId: 0,
    detalles: [],
  };

  proveedores: OpcionBasica[] = [
    { id: 1, nombre: 'Proveedor A' },
    { id: 2, nombre: 'Proveedor B' },
  ];

  almacenes: OpcionBasica[] = [
    { id: 1, nombre: 'Almacén Principal' },
    { id: 2, nombre: 'Almacén Secundario' },
  ];

  productos: OpcionBasica[] = [
    { id: 101, nombre: 'Producto 1' },
    { id: 102, nombre: 'Producto 2' },
  ];

  confirmacion = false;
  cargando = false;

  // TU ENDPOINT REAL
  private readonly apiUrl = 'http://localhost:8080/compras';

  constructor(private http: HttpClient) {}

  agregarDetalle() {
    const nuevoDetalle = {
      productoId: 0,
      cantidad: 0,
      precioUnitario: 0,
    };

    this.compra.detalles.push(nuevoDetalle);
  }

  eliminarDetalle(index: number) {
    this.compra.detalles.splice(index, 1);
  }

  get totalCalculado(): number {
    return this.compra.detalles.reduce(
      (acc: number, d: any) =>
        acc + (Number(d.cantidad) || 0) * (Number(d.precioUnitario) || 0),
      0
    );
  }

  guardar(form: NgForm) {
    if (form.invalid || this.compra.detalles.length === 0) {
      return;
    }

    this.cargando = true;

    // Envío directo a tu backend
    this.http.post(this.apiUrl, this.compra).subscribe({
      next: () => {
        this.cargando = false;
        this.confirmacion = true;

        // Limpiar formulario
        this.compra = {
          numero: '',
          proveedorId: 0,
          almacenId: 0,
          detalles: [],
        };

        form.resetForm({
          numero: '',
          proveedorId: 0,
          almacenId: 0,
        });

        setTimeout(() => (this.confirmacion = false), 3000);
      },
      error: (err) => {
        console.error('Error registrando compra:', err);
        this.cargando = false;
      },
    });
  }

  cancelar(form: NgForm) {
    this.compra = {
      numero: '',
      proveedorId: 0,
      almacenId: 0,
      detalles: [],
    };

    form.resetForm({
      numero: '',
      proveedorId: 0,
      almacenId: 0,
    });
  }
}
