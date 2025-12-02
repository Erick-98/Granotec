import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ProduccionService } from '../../services/produccion.service';

@Component({
  selector: 'app-orden-create',
  standalone: false,
  templateUrl: './orden-create.component.html',
  styleUrls: ['./orden-create.component.scss']
})
export class OrdenCreateComponent {
  model: any = { numero: '', productoId: null, cantidadProgramada: null, almacenDestinoId: null, listaMaterialId: null };
  loading = false;

  constructor(private svc: ProduccionService, private router: Router) {}

  guardar() {
    this.loading = true;
    this.svc.crearOrden(this.model).subscribe({
      next: () => { this.loading = false; this.router.navigate(['/produccion']); },
      error: () => { this.loading = false; }
    });
  }

  cancelar() { this.router.navigate(['/produccion']); }
}
