import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { OrdenListComponent } from './pages/orden-list/orden-list.component';
import { OrdenCreateComponent } from './pages/orden-create/orden-create.component';
import { OrdenDetalleComponent } from './pages/orden-detalle/orden-detalle.component';
import { OrdenConsumosComponent } from './pages/orden-consumos/orden-consumos';
import { OrdenLaboratorioComponent } from './pages/orden-laboratorio/orden-laboratorio';
import { OrdenCerrarComponent } from './pages/orden-cerrar/orden-cerrar';

const routes: Routes = [
  { path: '', component: OrdenListComponent },                     // A
  { path: 'crear', component: OrdenCreateComponent },              // B
  { path: ':id', component: OrdenDetalleComponent },               // C
  { path: ':id/consumos', component: OrdenConsumosComponent },     // D
  { path: ':id/laboratorio', component: OrdenLaboratorioComponent }, // E
  { path: ':id/cerrar', component: OrdenCerrarComponent },         // F
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProduccionRoutingModule { }
