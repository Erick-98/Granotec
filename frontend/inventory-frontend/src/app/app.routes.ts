import { Routes } from '@angular/router';
import { BlankComponent } from './layouts/blank/blank.component';
import { FullComponent } from './layouts/full/full.component';
import { authGuard } from './core/guards/auth.guard';
import { KardexComponent } from './pages/kardex/kardex/kardex.component';
// mantenimiento section is lazy-loaded from its own routes file

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/authentication/login',
    pathMatch: 'full',
  },
  {
    path: '',
    component: FullComponent,
    canActivate: [authGuard], // Proteger todo el layout autenticado
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./pages/pages.routes').then((m) => m.PagesRoutes),
      },
      {
        path: 'mantenimiento',
        loadChildren: () => 
          import('./pages/mantenimiento/mantenimiento.routes').then(
            (m) => m.MantenimientoRoutes)
      },
      {
        path: 'inventario',
        children: [
          {
            path: 'kardex',
            component: KardexComponent
          }
        ]
      },
      {
        path: 'ui-components',
        loadChildren: () =>
          import('./pages/ui-components/ui-components.routes').then(
            (m) => m.UiComponentsRoutes
          ),
      },
      {
        path: 'extra',
        loadChildren: () =>
          import('./pages/extra/extra.routes').then((m) => m.ExtraRoutes),
      },{
        path: 'produccion',
        loadChildren: () =>
          import('./produccion/produccion.module').then(m => m.ProduccionModule),
      },
      {
        path: 'compras',
        children: [
          {
            path: 'orden-compra-list',
            loadComponent: () =>
              import('./pages/ui-components/orden-compra-list/orden-compra-list.component').then(
                (m) => m.OrdenCompraListComponent
              ),
          },
          {
            path: 'orden-compra',
            loadComponent: () =>
              import('./pages/ui-components/orden-compra/orden-compra-form.component').then(
                (m) => m.OrdenCompraFormComponent
              ),
          },
          {
            path: 'orden-compra/view/:id',
            loadComponent: () =>
              import('./pages/ui-components/orden-compra-detail/orden-compra-detail.component').then(
                (m) => m.OrdenCompraDetailComponent
              ),
          },
          {
            path: 'orden-compra/:id',
            loadComponent: () =>
              import('./pages/ui-components/orden-compra/orden-compra-form.component').then(
                (m) => m.OrdenCompraFormComponent
              ),
          },
          {
            path: '',
            redirectTo: 'orden-compra-list',
            pathMatch: 'full',
          }
        ]
      }
    ],
  },
  {
    path: '',
    component: BlankComponent,
    children: [
      {
        path: 'authentication',
        loadChildren: () =>
          import('./pages/authentication/authentication.routes').then(
            (m) => m.AuthenticationRoutes
          ),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'authentication/login',
  },
];
