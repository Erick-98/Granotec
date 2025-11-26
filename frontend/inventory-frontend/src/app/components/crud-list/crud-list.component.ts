import { Component, EventEmitter, Input, Output, SimpleChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatMenuModule } from '@angular/material/menu';
import { MatRippleModule } from '@angular/material/core';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Subscription } from 'rxjs';

export type CrudColumnType = 'text' | 'image' | 'date' | 'badge' | 'price';

export interface CrudColumn {
  field: string;
  label?: string;
  type?: CrudColumnType;
  subField?: string;
}

export interface CrudAction {
  label: string;
  icon?: string;
  value: string;
}

@Component({
  selector: 'app-crud-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatCheckboxModule,
    MatMenuModule,
    MatRippleModule
  ],
  templateUrl: './crud-list.component.html',
  styleUrls: ['./crud-list.component.scss']
})
export class CrudListComponent implements OnDestroy {
  // Data & columns
  @Input() title = 'List';
  @Input() items: any[] = [];
  @Input() columns: CrudColumn[] = [
    { field: 'name', label: 'Name', type: 'text', subField: 'subtitle' }
  ];

  // pagination & server mode
  @Input() pageSizeOptions = [5, 10, 25];
  @Input() pageSize = 5;
  @Input() length = 0; // total length for server-side
  @Input() serverSide = false; // if true, emit search/page events instead of paginating client-side

  // actions
  @Input() actions: CrudAction[] = [
    { label: 'Editar', icon: 'editar', value: 'edit' },
    { label: 'Eliminar', icon: 'eliminar', value: 'delete' }
  ];

  @Output() add = new EventEmitter<void>();
  @Output() edit = new EventEmitter<any>();
  @Output() delete = new EventEmitter<any>();
  @Output() selectionChange = new EventEmitter<any[]>();

  // server events
  @Output() searchChange = new EventEmitter<string>();
  @Output() pageChange = new EventEmitter<{ pageIndex: number; pageSize: number }>();
  @Output() actionTriggered = new EventEmitter<{ action: string; item: any }>();

  search = new FormControl<string | null>('');

  private sub?: Subscription;

  // client-side pagination state
  pageIndex = 0;

  selected = new Set<any>();

  ngOnChanges(changes: SimpleChanges) {
    if (changes['items'] && !this.serverSide) {
      // reset selection when items change
      this.selected.clear();
      this.selectionChange.emit([]);
    }
  }

  ngOnInit(): void {
    // when in server mode, emit search events to allow parent to load data
    this.sub = this.search.valueChanges
      .pipe(debounceTime(200), distinctUntilChanged())
      .subscribe((v: string | null) => {
        // reset pagination on search change
        this.pageIndex = 0;
        if (this.serverSide) {
          this.searchChange.emit(v || '');
        }
      });
  }

  displayed(): any[] {
    if (this.serverSide) return this.items;

    const q = (this.search.value || '').toString().trim().toLowerCase();
    let filtered = this.items;
    if (q) {
      filtered = this.items.filter(item => {
        // check each configured column
        for (const col of this.columns || []) {
          const val = item?.[col.field];
          const sub = col.subField ? item?.[col.subField] : undefined;
          const combined = [val, sub].filter(x => x !== undefined && x !== null).join(' ');
          if (combined.toString().toLowerCase().includes(q)) return true;
        }
        // fallback: search in JSON representation
        try {
          if (JSON.stringify(item).toLowerCase().includes(q)) return true;
        } catch (e) {}
        return false;
      });
    }

    const start = this.pageIndex * this.pageSize;
    return filtered.slice(start, start + this.pageSize);
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  get displayedColumns(): string[] {
    const cols = this.columns?.map(c => c.field) ?? [];
    return ['select', ...cols, 'action'];
  }

  toggleSelect(item: any) {
    if (this.selected.has(item)) this.selected.delete(item);
    else this.selected.add(item);
    this.selectionChange.emit(Array.from(this.selected));
  }

  selectAllOnPage() {
    const page = this.displayed();
    const allSelected = page.every(p => this.selected.has(p));
    if (allSelected) page.forEach(p => this.selected.delete(p));
    else page.forEach(p => this.selected.add(p));
    this.selectionChange.emit(Array.from(this.selected));
  }

  onPage(e: PageEvent) {
    this.pageIndex = e.pageIndex;
    this.pageSize = e.pageSize;
    if (this.serverSide) {
      this.pageChange.emit({ pageIndex: this.pageIndex, pageSize: this.pageSize });
    }
  }

// En tu crud-list.component.ts, cambia esta línea:
triggerAction(action: string, item: any) {
  // default convenience outputs
  if (action === 'edit') this.edit.emit(item);
  else if (action === 'delete') this.delete.emit(item); // ← Cambiar a enviar el objeto completo
  this.actionTriggered.emit({ action, item });
}

  trackByFn(index: number, item: any) {
    return item?.id ?? index;
  }
}
