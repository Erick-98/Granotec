import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../../../material.module';
import { MatTableDataSource } from '@angular/material/table';

interface KardexRow {
  fecha: string;
  almacen: string;
  movimiento: string;
  tipoOperacion: string;
  nombreComercial: string;
  codigo: string;
  lote: string;
  op: string;
  fechaProd: string;
  fechaVcto: string;
  presentacion: string;
}

@Component({
  selector: 'app-icons',
  standalone: true, // importante para standalone
  imports: [CommonModule, MaterialModule], // ✅ ya puedes usar mat-card, mat-table, etc.
  templateUrl: './icons.component.html',
  styleUrls: ['./icons.component.scss']
})
export class AppIconsComponent {
  displayedColumns = [
    'fecha','almacen','movimiento','tipoOperacion',
    'nombreComercial','codigo','lote','op',
    'fechaProd','fechaVcto','presentacion'
  ];

  dataSource = new MatTableDataSource<KardexRow>([
  {
    fecha: '13/01/2022',
    almacen: 'GENERAL',
    movimiento: 'Salidas',
    tipoOperacion: 'PRODUCCIÓN',
    nombreComercial: 'MONOGLICERIDO DMG',
    codigo: 'EMUPU0003PE',
    lote: '20220806',
    op: '2023-011',
    fechaProd: '06/08/2022',
    fechaVcto: '05/08/2024',
    presentacion: '25 kg'
  },
  {
    fecha: '13/01/2022',
    almacen: 'GENERAL',
    movimiento: 'Salidas',
    tipoOperacion: 'PRODUCCIÓN',
    nombreComercial: 'ESTEAROIL LACTILATO DE SODIO - ALPAMEX',
    codigo: 'EMUPU0002PE',
    lote: 'DYLAR28',
    op: '2023-011',
    fechaProd: '25/07/2021',
    fechaVcto: '24/01/2023',
    presentacion: '25 kg'
  },
  {
    fecha: '18/02/2022',
    almacen: 'GENERAL',
    movimiento: 'Ingresos',
    tipoOperacion: 'COMPRA',
    nombreComercial: 'LECITINA DE SOYA GRANOTEC',
    codigo: 'ADISOY001',
    lote: '2022L12',
    op: 'COMP-1478',
    fechaProd: '18/02/2022',
    fechaVcto: '18/02/2024',
    presentacion: '20 kg'
  },
  {
    fecha: '07/03/2022',
    almacen: 'GENERAL',
    movimiento: 'Salidas',
    tipoOperacion: 'PRODUCCIÓN',
    nombreComercial: 'ACIDO ESTEÁRICO 1800',
    codigo: 'ACEST001PE',
    lote: 'ST2022-09',
    op: '2023-018',
    fechaProd: '07/03/2022',
    fechaVcto: '07/03/2024',
    presentacion: '25 kg'
  },
  {
    fecha: '15/03/2022',
    almacen: 'GENERAL',
    movimiento: 'Ingresos',
    tipoOperacion: 'DEVOLUCIÓN',
    nombreComercial: 'MONOGLICERIDO DMG',
    codigo: 'EMUPU0003PE',
    lote: '20220806',
    op: '2023-011',
    fechaProd: '06/08/2022',
    fechaVcto: '05/08/2024',
    presentacion: '25 kg'
  },
  {
    fecha: '20/04/2022',
    almacen: 'GENERAL',
    movimiento: 'Salidas',
    tipoOperacion: 'PRODUCCIÓN',
    nombreComercial: 'GLICEROL MONOESTEARATO',
    codigo: 'EMUGLI001',
    lote: '2022-GMS24',
    op: '2023-021',
    fechaProd: '20/04/2022',
    fechaVcto: '20/04/2024',
    presentacion: '25 kg'
  },
  {
    fecha: '28/04/2022',
    almacen: 'GENERAL',
    movimiento: 'Salidas',
    tipoOperacion: 'PRODUCCIÓN',
    nombreComercial: 'SORBITOL EN POLVO',
    codigo: 'ADISOR001PE',
    lote: 'SORP22-09',
    op: '2023-023',
    fechaProd: '28/04/2022',
    fechaVcto: '28/04/2024',
    presentacion: '25 kg'
  },
  {
    fecha: '10/05/2022',
    almacen: 'GENERAL',
    movimiento: 'Ingresos',
    tipoOperacion: 'COMPRA',
    nombreComercial: 'ACIDO CÍTRICO ANHIDRO',
    codigo: 'ADICIT001PE',
    lote: 'CIT-202205',
    op: 'COMP-1650',
    fechaProd: '10/05/2022',
    fechaVcto: '10/05/2025',
    presentacion: '25 kg'
  }
]);

applyFilter(value: string) {
    this.dataSource.filter = value.trim().toLowerCase();
  }

}
