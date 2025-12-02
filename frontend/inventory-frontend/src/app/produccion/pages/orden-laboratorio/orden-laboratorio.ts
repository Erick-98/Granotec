import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProduccionService } from '../../services/produccion.service';

@Component({
  selector: 'app-orden-laboratorio',
  standalone: false,
  templateUrl: './orden-laboratorio.html',
  styleUrls: ['./orden-laboratorio.scss']
})
export class OrdenLaboratorioComponent {
  aprobado = true;
  observacion = '';
  ordenId!: number;
  loading = false;

  constructor(private route: ActivatedRoute, private svc: ProduccionService, private router: Router) {
    this.ordenId = Number(this.route.snapshot.paramMap.get('id'));
  }

  enviar() {
    this.loading = true;
    this.svc.aprobarLaboratorio(this.ordenId, { aprobado: this.aprobado, observacion: this.observacion }).subscribe({
      next: () => { this.loading = false; this.router.navigate(['/produccion', this.ordenId]); },
      error: () => { this.loading = false; }
    });
  }
}
