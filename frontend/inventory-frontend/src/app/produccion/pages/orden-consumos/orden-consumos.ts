import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProduccionService } from '../../services/produccion.service';

@Component({
  selector: 'app-orden-consumos',
  standalone: false,
  templateUrl: './orden-consumos.html',
  styleUrls: ['./orden-consumos.scss']
})
export class OrdenConsumosComponent {
  model: any = { insumoId: null, cantidad: null, stockLoteOrigenId: null };
  ordenId!: number;
  loading = false;

  constructor(private route: ActivatedRoute, private svc: ProduccionService, private router: Router) {
    this.ordenId = Number(this.route.snapshot.paramMap.get('id'));
  }

  agregar() {
    if (!this.ordenId) return;
    this.loading = true;
    this.svc.registrarConsumo(this.ordenId, this.model).subscribe({
      next: () => { this.loading = false; this.router.navigate(['/produccion', this.ordenId]); },
      error: () => { this.loading = false; }
    });
  }

  cancelar() { this.router.navigate(['/produccion', this.ordenId]); }
}
