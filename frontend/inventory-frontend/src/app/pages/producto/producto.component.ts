import { Component, ViewEncapsulation } from '@angular/core';
import { CrudListComponent, CrudColumn } from 'src/app/components/crud-list/crud-list.component';
import { MaterialModule } from '../../material.module';

@Component({
  selector: 'app-producto',
  templateUrl: './producto.component.html',
  styleUrls: ['./producto.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [MaterialModule, CrudListComponent]
})
export class ProductoComponent {
  items: any[] = [];
  columns: CrudColumn[] = [
    { field: 'codigo', label: 'Código', type: 'text' },
    { field: 'nombreComercial', label: 'Nombre', type: 'text' },
    { field: 'unidadMedida', label: 'Unidad de Medida', type: 'text' },
    { field: 'familia', label: 'Familia', type: 'text' },
    { field: 'proveedor', label: 'Proveedor', type: 'text' },
    {field: 'precio', label: 'Precio', type: 'text' }
  ];

  onAdd() { console.log('Add producto'); }
  onEdit(item: any) { console.log('Edit producto', item); }
  onDelete(item: any) { console.log('Delete producto', item); }
}
