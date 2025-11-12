import { Component, ViewEncapsulation } from '@angular/core';
import { CrudListComponent, CrudColumn } from 'src/app/components/crud-list/crud-list.component';
import { MaterialModule } from '../../material.module';
import { AlmacenService } from 'src/app/core/services/almacen.service';

@Component({
  selector: 'app-almacen',
  templateUrl: './almacen.component.html',
  styleUrls: ['./almacen.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [MaterialModule, CrudListComponent]
})
export class AlmacenComponent {
  items: any[] = [];
  columns: CrudColumn[] = [
    { field: 'nombre', label: 'Nombre', type: 'text', subField: 'text' },
    { field: 'descripcion', label: 'Descripción', type: 'text' }
  ];

  constructor(private almacenService: AlmacenService) {
    this.load();
  }

  load() {
    this.almacenService.getAll().subscribe({
      next: res => {
        this.items = (res.data ?? res) as any[];
      },
      error: err => console.error('Almacen load error', err)
    });
  }

  onAdd() {
    // navigate or open create dialog
    console.log('Add almacen');
  }

  onEdit(item: any) {
    console.log('Edit', item);
  }

  onDelete(item: any) {
    console.log('Delete', item);
  }
}
