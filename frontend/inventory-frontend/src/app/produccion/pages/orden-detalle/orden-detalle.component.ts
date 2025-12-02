import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProduccionService } from '../../services/produccion.service';

@Component({
  selector: 'app-orden-detalle',
  standalone: false,
  templateUrl: './orden-detalle.component.html',
  styleUrls: ['./orden-detalle.component.scss']
})
export class OrdenDetalleComponent implements OnInit {
  orden: any = null;
  loading = false;

  constructor(private route: ActivatedRoute, private svc: ProduccionService, private router: Router) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!isNaN(id)) this.load(id);
  }

  load(id: number) {
    this.loading = true;
    this.svc.obtenerOrden(id).subscribe({
      next: res => { this.orden = res; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  irConsumos() { this.router.navigate(['produccion', this.orden.id, 'consumos']); }
  irLaboratorio() { this.router.navigate(['produccion', this.orden.id, 'laboratorio']); }
  irCerrar() { this.router.navigate(['produccion', this.orden.id, 'cerrar']); }
}
