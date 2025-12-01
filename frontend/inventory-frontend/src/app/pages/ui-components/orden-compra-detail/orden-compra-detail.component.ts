import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MaterialModule } from 'src/app/material.module';
import { TablerIconsModule } from 'angular-tabler-icons';
import { CompraService } from 'src/app/core/services/compra.service';
import { CompraResponse } from 'src/app/core/models/compra-response.model';

@Component({
  selector: 'app-orden-compra-detail',
  standalone: true,
  imports: [CommonModule, MaterialModule, TablerIconsModule, RouterLink],
  templateUrl: './orden-compra-detail.component.html',
  styleUrls: ['./orden-compra-detail.component.scss']
})
export class OrdenCompraDetailComponent implements OnInit {
  compra: CompraResponse | null = null;
  loading = true;
  vatPercentage: number = 18; // IGV 18%

  // Calculados
  subTotal: number = 0;
  vatAmount: number = 0;
  grandTotal: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private compraService: CompraService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadCompra(+id);
    } else {
      this.router.navigate(['/compras/orden-compra-list']);
    }
  }

  loadCompra(id: number): void {
    this.loading = true;
    this.compraService.getById(id).subscribe({
      next: (data) => {
        this.compra = data;
        this.calculateTotals();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar la compra:', err);
        this.loading = false;
        this.router.navigate(['/compras/orden-compra-list']);
      }
    });
  }

  calculateTotals(): void {
    if (!this.compra || !this.compra.detalles) {
      this.subTotal = 0;
      this.vatAmount = 0;
      this.grandTotal = 0;
      return;
    }

    this.subTotal = this.compra.detalles.reduce((sum, detalle) => {
      return sum + (detalle.subtotal || 0);
    }, 0);

    this.vatAmount = this.subTotal * (this.vatPercentage / 100);
    this.grandTotal = this.subTotal + this.vatAmount;
  }

  goBack(): void {
    this.router.navigate(['/compras/orden-compra-list']);
  }

  editCompra(): void {
    if (this.compra && this.compra.id) {
      this.router.navigate(['/compras/orden-compra', this.compra.id]);
    }
  }

  getEstadoClase(estado: string | undefined): string {
    const e = (estado || '').toLowerCase();
    if (e.includes('aprob') || e.includes('delivered') || e.includes('completado')) return 'pill pill-success';
    if (e.includes('pend') || e.includes('pending')) return 'pill pill-warning';
    if (e.includes('ship') || e.includes('enviado')) return 'pill pill-info';
    return 'pill pill-default';
  }
}
