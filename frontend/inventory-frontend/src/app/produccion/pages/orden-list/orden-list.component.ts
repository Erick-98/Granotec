import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProduccionService } from '../../services/produccion.service';

@Component({
  selector: 'app-orden-list',
  standalone: false,
  templateUrl: './orden-list.component.html',
  styleUrls: ['./orden-list.component.scss']
})
export class OrdenListComponent implements OnInit {
  ordenes: any[] = [];
  loading = false;

  constructor(private svc: ProduccionService, private router: Router) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.loading = true;
    this.svc.listarOrdenes().subscribe({
      next: (res: any[]) => { this.ordenes = res || []; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  ver(id: number) {
    this.router.navigate(['/produccion', id]);
  }

  crear() {
    this.router.navigate(['/produccion/crear']);
  }
}
