import { Component, ViewEncapsulation } from '@angular/core';
import { CrudListComponent, CrudColumn } from 'src/app/components/crud-list/crud-list.component';
import { MaterialModule } from '../../material.module';
import { ProveedorService } from 'src/app/core/services/proveedor.service';

@Component({
  selector: 'app-proveedor',
  templateUrl: './proveedor.component.html',
  styleUrls: ['./proveedor.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [MaterialModule, CrudListComponent]
})
export class ProveedorComponent {
  items: any[] = [];
  columns: CrudColumn[] = [
    { field: 'nombre', label: 'Nombre', type: 'text' },
    { field: 'documento', label: 'RUC', type: 'text' },
    { field: 'telefono', label: 'Teléfono', type: 'text' },
    { field: 'email', label: 'Email', type: 'text' },
    { field: 'moneda', label: 'Moneda', type: 'text' },
    { field: 'condicionPago', label: 'Condición de Pago', type: 'text' },
  ];

  constructor(private proveedorService: ProveedorService) {
    this.load();
  }

    load() {
        this.proveedorService.getAll().subscribe({
            next: res => {
                this.items = (res.data ?? res) as any[];
            },
            error: err => console.error('Proveedor load error', err)
        });
    }

  onAdd() { console.log('Add proveedor'); }
  onEdit(item: any) { console.log('Edit proveedor', item); }
  onDelete(item: any) { console.log('Delete proveedor', item); }
}
