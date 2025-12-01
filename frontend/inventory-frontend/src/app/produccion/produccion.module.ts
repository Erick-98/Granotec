import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

import { ProduccionRoutingModule } from './produccion-routing.module';

import { OrdenListComponent } from './pages/orden-list/orden-list.component';
import { OrdenCreateComponent } from './pages/orden-create/orden-create.component';
import { OrdenDetalleComponent } from './pages/orden-detalle/orden-detalle.component';
import { OrdenConsumosComponent } from './pages/orden-consumos/orden-consumos';
import { OrdenLaboratorioComponent } from './pages/orden-laboratorio/orden-laboratorio';
import { OrdenCerrarComponent } from './pages/orden-cerrar/orden-cerrar';

@NgModule({
  declarations: [
    OrdenListComponent,
    OrdenCreateComponent,
    OrdenDetalleComponent,
    OrdenConsumosComponent,
    OrdenLaboratorioComponent,
    OrdenCerrarComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    ProduccionRoutingModule
  ]
})
export class ProduccionModule { }
