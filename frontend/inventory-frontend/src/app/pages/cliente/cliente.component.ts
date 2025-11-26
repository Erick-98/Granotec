import { Component, ViewEncapsulation } from '@angular/core';
import { CrudListComponent, CrudColumn } from 'src/app/components/crud-list/crud-list.component';
import { MaterialModule } from '../../material.module';
import { MatDialog } from '@angular/material/dialog';
import { CustomerService} from '../../services/customer.service';
import { CustomerModalComponent } from '../../components/customer-modal/customer-modal.component';
import { CustomerResponse } from '../../core/models/customer.model';

@Component({
  selector: 'app-cliente',
  templateUrl: './cliente.component.html',
  styleUrls: ['./cliente.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [MaterialModule, CrudListComponent]
})
export class ClienteComponent {
  items: any[] = [];
  columns: CrudColumn[] = [
    { field: 'cliente', label: 'Cliente', type: 'text' },
    { field: 'documento', label: 'Documento', type: 'text' },
    { field: 'telefono', label: 'Teléfono', type: 'text' },
    { field: 'email', label: 'Email', type: 'text' },
    { field: 'tipoCliente', label: 'Tipo de Cliente', type: 'text'},
    { field: 'condicionPago', label: 'Condición de Pago', type: 'text'}
  ];

  constructor(
    private customerService: CustomerService,
    private dialog: MatDialog
  ) {
    this.loadCustomers();
  }

  loadCustomers(): void {
  this.customerService.getCustomers().subscribe({
    next: (response: any) => {
      // Verificar si la respuesta es un array o está dentro de un objeto
      if (Array.isArray(response)) {
        this.items = this.transformCustomers(response);
      } else if (response.data && Array.isArray(response.data)) {
        // Si la respuesta tiene formato { data: [], message: "" }
        this.items = this.transformCustomers(response.data);
      } else if (response.items && Array.isArray(response.items)) {
        // Si la respuesta tiene formato { items: [] }
        this.items = this.transformCustomers(response.items);
      } else {
        console.error('Formato de respuesta inesperado:', response);
        this.items = [];
      }
    },
    error: (error) => {
      console.error('Error loading customers:', error);
      this.items = [];
    }
  });
}

private transformCustomers(customers: any[]): any[] {
  return customers.map(customer => ({
    id: customer.id,
    cliente: this.getCustomerName(customer),
    documento: customer.nroDocumento,
    telefono: customer.telefono,
    email: customer.email,
    tipoCliente: customer.tipoCliente,
    condicionPago: customer.condicionPago,
    originalData: customer
  }));
}

  private getCustomerName(customer: CustomerResponse): string {
    if (customer.razonSocial) {
      return customer.razonSocial;
    }
    return `${customer.nombre || ''} ${customer.apellidos || ''}`.trim();
  }

  onAdd() { 
    const dialogRef = this.dialog.open(CustomerModalComponent, {
      width: '800px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadCustomers();
      }
    });
  }

  // cliente.component.ts - CORREGIR onEdit y onDelete
onEdit(item: any) { 
  console.log('✏️ Editando item:', item);
  
  // Asegurar que tenemos el ID correcto
  const customerId = item.id || (item.originalData && item.originalData.id);
  
  if (!customerId) {
    console.error('❌ No se pudo obtener el ID del cliente');
    alert('Error: No se puede identificar el cliente a editar');
    return;
  }
  
  // ✅ CORREGIDO - Obtener los datos completos del cliente desde el backend
  this.customerService.getCustomer(customerId).subscribe({
    next: (customerData) => {
      console.log('📋 Datos del cliente para editar:', customerData);
      
      const dialogRef = this.dialog.open(CustomerModalComponent, {
        width: '800px',
        data: { 
          customer: customerData, 
          isEdit: true 
        }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.loadCustomers();
        }
      });
    },
    error: (error) => {
      console.error('❌ Error cargando datos del cliente:', error);
      alert('Error al cargar los datos del cliente');
    }
  });
}


onDelete(item: any) { 
  const customerId = item.id || (item.originalData && item.originalData.id);
  const customerName = item.cliente || 'este cliente';
  
  if (!customerId) {
    console.error('❌ No se pudo obtener el ID del cliente para eliminar');
    alert('Error: No se puede identificar el cliente a eliminar');
    return;
  }
  
  if (confirm(`¿Está seguro de eliminar a ${customerName}?`)) {
    console.log('🗑️ Eliminando cliente ID:', customerId);
    this.customerService.deleteCustomer(customerId).subscribe({
      next: () => {
        console.log('✅ Cliente eliminado exitosamente');
        this.loadCustomers();
      },
      error: (error) => {
        console.error('❌ Error eliminando cliente:', error);
        alert('Error al eliminar el cliente: ' + (error.error?.message || error.message));
      }
    });
  }
}
}