import { Component, ViewEncapsulation } from '@angular/core';
import { CrudListComponent, CrudColumn } from 'src/app/components/crud-list/crud-list.component';
import { MaterialModule } from '../../material.module';

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
    {field: 'condicionPago', label: 'Condición de Pago', type: 'text'}

  ];

  onAdd() { console.log('Add cliente'); }
  onEdit(item: any) { console.log('Edit cliente', item); }
  onDelete(item: any) { console.log('Delete cliente', item); }
}
