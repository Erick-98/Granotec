import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProduccionService } from '../../services/produccion.service';

@Component({
  selector: 'app-orden-cerrar',
  standalone: false,
  templateUrl: './orden-cerrar.html',
  styleUrls: ['./orden-cerrar.scss']
})
export class OrdenCerrarComponent {
  ordenId!: number;
  cantidadProducida: number | null = null;
  codigoLote = '';
  precioVentaUnitario: number | null = null;
  loading = false;

  constructor(private route: ActivatedRoute, private svc: ProduccionService, private router: Router) {
    this.ordenId = Number(this.route.snapshot.paramMap.get('id'));
  }

  cerrar() {
    this.loading = true;
    const body: any = { cantidadProducida: this.cantidadProducida, codigoLote: this.codigoLote, precioVentaUnitario: this.precioVentaUnitario };
    this.svc.cerrarOrden(this.ordenId, body).subscribe({
      next: () => { this.loading = false; this.router.navigate(['/produccion', this.ordenId]); },
      error: () => { this.loading = false; }
    });
  }
}
