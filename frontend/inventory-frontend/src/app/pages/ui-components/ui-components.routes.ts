import { Routes } from '@angular/router';

// ui
import { BadgeListComponent } from './badge/badge-list.component';
import { AppChipsComponent } from './chips/chips.component';
import { AppListsComponent } from './lists/lists.component';
import { AppMenuComponent } from './menu/menu.component';
import { AppTooltipsComponent } from './tooltips/tooltips.component';
import { AppFormsComponent } from './forms/forms.component';
import { AppTablesComponent } from './tables/tables.component';
import { OrdenCompraFormComponent } from './orden-compra/orden-compra-form.component';
import { OrdenCompraListComponent } from './orden-compra-list/orden-compra-list.component';
export const UiComponentsRoutes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'badge',
        component: BadgeListComponent,
      },
      {
        path: 'chips',
        component: AppChipsComponent,
      },
      {
        path: 'lists',
        component: AppListsComponent,
      },
      {
        path: 'menu',
        component: AppMenuComponent,
      },
      {
        path: 'tooltips',
        component: AppTooltipsComponent,
      },
      {
        path: 'forms',
        component: AppFormsComponent,
      },
      {
        path: 'tables',
        component: AppTablesComponent,
      },
      {
        path: 'orden-compra',
        component: OrdenCompraFormComponent,
      },
      {
        path: 'orden-compra-list',
        component: OrdenCompraListComponent,
      },
    ],
  },
];
