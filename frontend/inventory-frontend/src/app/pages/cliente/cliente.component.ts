// src/app/pages/cliente/cliente.component.ts

import { Component, ViewEncapsulation } from '@angular/core';
import {
  CrudListComponent,
  CrudColumn,
} from 'src/app/components/crud-list/crud-list.component';
import { MaterialModule } from '../../material.module';
import { MatDialog } from '@angular/material/dialog';
import { CustomerService } from '../../core/services/customer.service';
import { CustomerModalComponent } from '../../components/customer-modal/customer-modal.component';
import {
  CustomerResponse,
} from '../../core/models/customer.model';
import { ApiResponse } from 'src/app/core/models/api-response.models';

@Component({
  selector: 'app-cliente',
  templateUrl: './cliente.component.html',
  styleUrls: ['./cliente.component.scss'],
  encapsulation: ViewEncapsulation.None,
  standalone: true,
  imports: [MaterialModule, CrudListComponent],
})
export class ClienteComponent {
  // items que se muestran en la tabla (ya transformados)
  items: any[] = [];

  // columnas del CrudList
  columns: CrudColumn[] = [
    { field: 'cliente', label: 'Cliente', type: 'text' },
    { field: 'documento', label: 'Documento', type: 'text' },
    { field: 'telefono', label: 'Teléfono', type: 'text' },
    { field: 'email', label: 'Email', type: 'text' },
    { field: 'tipoCliente', label: 'Tipo de Cliente', type: 'text' },
    { field: 'condicionPago', label: 'Condición de Pago', type: 'text' },
  ];

  constructor(
    private customerService: CustomerService,
    private dialog: MatDialog
  ) {
    this.loadCustomers();
  }

  // ====== CARGAR LISTA ======
  loadCustomers(): void {
    this.customerService.getCustomers().subscribe({
      next: (response: ApiResponse<CustomerResponse[]>) => {
        this.items = this.transformCustomers(response.data ?? []);
      },
      error: (error) => {
        console.error('Error loading customers:', error);
        this.items = [];
      },
    });
  }

  private transformCustomers(customers: CustomerResponse[]): any[] {
    return customers.map((customer) => ({
      id: customer.id,
      cliente: this.getCustomerName(customer),
      documento: customer.nroDocumento,
      telefono: customer.telefono,
      email: customer.email,
      tipoCliente: customer.tipoCliente,
      condicionPago: customer.condicionPago,
      originalData: customer,
    }));
  }

  private getCustomerName(customer: CustomerResponse): string {
    if (customer.razonSocial) return customer.razonSocial;
    return `${customer.nombre || ''} ${customer.apellidos || ''}`.trim();
  }

  // ====== NUEVO CLIENTE ======
  onAdd() {
    const dialogRef = this.dialog.open(CustomerModalComponent, {
      width: '800px',
      data: {},
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) this.loadCustomers();
    });
  }

  // ====== EDITAR CLIENTE ======
  onEdit(item: any) {
    const customerId = item.id || item.originalData?.id;
    if (!customerId) {
      alert('No se puede identificar el cliente a editar.');
      return;
    }

    this.customerService.getCustomer(customerId).subscribe({
      next: (resp: ApiResponse<CustomerResponse>) => {
        const customer = resp.data;
        const dialogRef = this.dialog.open(CustomerModalComponent, {
          width: '800px',
          data: { customer, isEdit: true },
        });

        dialogRef.afterClosed().subscribe((result) => {
          if (result) this.loadCustomers();
        });
      },
      error: (error) => {
        console.error('Error cargando datos del cliente:', error);
        alert('Error al cargar los datos del cliente');
      },
    });
  }

  // ====== ELIMINAR CLIENTE ======
  onDelete(item: any) {
    const customerId = item.id || item.originalData?.id;
    const customerName = item.cliente || 'este cliente';

    if (!customerId) {
      alert('No se puede identificar el cliente a eliminar.');
      return;
    }

    if (confirm(`¿Está seguro de eliminar a ${customerName}?`)) {
      this.customerService.deleteCustomer(customerId).subscribe({
        next: () => this.loadCustomers(),
        error: (error) => {
          console.error('Error eliminando cliente:', error);
          alert(
            'Error al eliminar el cliente: ' +
              (error.error?.message || error.message)
          );
        },
      });
    }
  }
}
