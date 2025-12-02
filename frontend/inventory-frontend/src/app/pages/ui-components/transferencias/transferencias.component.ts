import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from 'src/app/material.module';
import { TablerIconsModule } from 'angular-tabler-icons';
import { 
  TransferenciasService,
  ProductoDisponible,
  LoteDisponible,
  Almacen
} from './transferencias.service';

interface TransferenciaItem {
  producto: string;
  lote: string;
  cantidad: number;
  origen: string;
  destino: string;
}

@Component({
  selector: 'app-transferencias',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    TablerIconsModule
  ],
  templateUrl: './transferencias.component.html',
  styleUrls: ['./transferencias.component.scss']
})
export class TransferenciasComponent implements OnInit {
  transferenciaForm!: FormGroup;
  almacenes: Almacen[] = [];
  productos: ProductoDisponible[] = [];
  lotes: LoteDisponible[] = [];
  transferenciasRealizadas: TransferenciaItem[] = [];
  
  loadingAlmacenes = false;
  loadingProductos = false;
  loadingLotes = false;
  submitting = false;

  displayedColumns: string[] = ['producto', 'lote', 'cantidad', 'origen', 'destino'];

  constructor(
    private fb: FormBuilder,
    private transferenciasService: TransferenciasService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadAlmacenes();
  }

  initForm(): void {
    this.transferenciaForm = this.fb.group({
      almacenOrigenId: [null, Validators.required],
      almacenDestinoId: [null, Validators.required],
      productoId: [null, Validators.required],
      loteId: [null, Validators.required],
      cantidad: [null, [Validators.required, Validators.min(0.01)]],
      motivo: ['']
    });

    // Listeners para cargar datos dinámicamente
    this.transferenciaForm.get('almacenOrigenId')?.valueChanges.subscribe(almacenId => {
      if (almacenId) {
        this.loadProductos(almacenId);
        this.transferenciaForm.patchValue({ productoId: null, loteId: null });
        this.productos = [];
        this.lotes = [];
      }
    });

    this.transferenciaForm.get('productoId')?.valueChanges.subscribe(productoId => {
      const almacenOrigenId = this.transferenciaForm.get('almacenOrigenId')?.value;
      if (almacenOrigenId && productoId) {
        this.loadLotes(almacenOrigenId, productoId);
        this.transferenciaForm.patchValue({ loteId: null });
      }
    });
  }

  loadAlmacenes(): void {
    this.loadingAlmacenes = true;
    this.transferenciasService.getAlmacenes().subscribe({
      next: (response: any) => {
        this.almacenes = response.data || [];
        this.loadingAlmacenes = false;
      },
      error: (error: any) => {
        console.error('Error al cargar almacenes:', error);
        this.loadingAlmacenes = false;
      }
    });
  }

  loadProductos(almacenId: number): void {
    this.loadingProductos = true;
    this.transferenciasService.getProductosDisponibles(almacenId).subscribe({
      next: (productos: ProductoDisponible[]) => {
        this.productos = productos;
        this.loadingProductos = false;
      },
      error: (error: any) => {
        console.error('Error al cargar productos:', error);
        this.loadingProductos = false;
        this.productos = [];
      }
    });
  }

  loadLotes(almacenId: number, productoId: number): void {
    this.loadingLotes = true;
    this.transferenciasService.getLotesDisponibles(almacenId, productoId).subscribe({
      next: (lotes: LoteDisponible[]) => {
        this.lotes = lotes;
        this.loadingLotes = false;
      },
      error: (error: any) => {
        console.error('Error al cargar lotes:', error);
        this.loadingLotes = false;
        this.lotes = [];
      }
    });
  }

  getLoteLabel(lote: LoteDisponible): string {
    return `${lote.codigoLote} - ${lote.cantidadDisponible}kg`;
  }

  agregarTransferencia(): void {
    if (this.transferenciaForm.invalid) {
      this.transferenciaForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const formValue = this.transferenciaForm.value;

    const request = {
      almacenOrigenId: formValue.almacenOrigenId,
      almacenDestinoId: formValue.almacenDestinoId,
      productoId: formValue.productoId,
      usuarioId: 1, // TODO: Obtener del usuario logueado
      motivo: formValue.motivo || 'Transferencia entre almacenes',
      lotes: [
        {
          loteId: formValue.loteId,
          cantidad: formValue.cantidad
        }
      ]
    };

    this.transferenciasService.realizarTransferencia(request).subscribe({
      next: (response: any) => {
        console.log('Transferencia realizada:', response);
        
        // Agregar a la tabla de transferencias realizadas
        const productoNombre = this.productos.find(p => p.productoId === formValue.productoId)?.productoNombre || '';
        const loteNombre = this.lotes.find(l => l.loteId === formValue.loteId)?.codigoLote || '';
        const origenNombre = this.almacenes.find(a => a.id === formValue.almacenOrigenId)?.nombre || '';
        const destinoNombre = this.almacenes.find(a => a.id === formValue.almacenDestinoId)?.nombre || '';

        this.transferenciasRealizadas.push({
          producto: productoNombre,
          lote: loteNombre,
          cantidad: formValue.cantidad,
          origen: origenNombre,
          destino: destinoNombre
        });

        // Resetear formulario parcialmente
        this.transferenciaForm.patchValue({
          loteId: null,
          cantidad: null
        });

        // Recargar lotes para actualizar cantidades disponibles
        this.loadLotes(formValue.almacenOrigenId, formValue.productoId);
        
        this.submitting = false;
      },
      error: (error: any) => {
        console.error('Error al realizar transferencia:', error);
        this.submitting = false;
      }
    });
  }

  get almacenOrigenInvalid(): boolean {
    const control = this.transferenciaForm.get('almacenOrigenId');
    return !!(control?.invalid && control?.touched);
  }

  get almacenDestinoInvalid(): boolean {
    const control = this.transferenciaForm.get('almacenDestinoId');
    return !!(control?.invalid && control?.touched);
  }

  get productoInvalid(): boolean {
    const control = this.transferenciaForm.get('productoId');
    return !!(control?.invalid && control?.touched);
  }

  get loteInvalid(): boolean {
    const control = this.transferenciaForm.get('loteId');
    return !!(control?.invalid && control?.touched);
  }

  get cantidadInvalid(): boolean {
    const control = this.transferenciaForm.get('cantidad');
    return !!(control?.invalid && control?.touched);
  }
}
