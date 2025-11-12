import { Routes } from '@angular/router';
import { AlmacenComponent } from '../almacen/almacen.component';
import { ProveedorComponent } from '../proveedor/proveedor.component';
import { ClienteComponent } from '../cliente/cliente.component';
import { ProductoComponent } from '../producto/producto.component';

export const MantenimientoRoutes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'almacen',
        component: AlmacenComponent,
        data: {
          title: 'Almacén',
          urls: [
            { title: 'Mantenimiento', url: '/mantenimiento' },
            { title: 'Almacén' }
          ]
        }
      },
      {
        path: 'proveedor',
        component: ProveedorComponent,
        data: {
          title: 'Proveedor',
          urls: [
            { title: 'Mantenimiento', url: '/mantenimiento' },
            { title: 'Proveedor' }
          ]
        }
      },
      {
        path: 'cliente',
        component: ClienteComponent,
        data: {
          title: 'Cliente',
          urls: [
            { title: 'Mantenimiento', url: '/mantenimiento' },
            { title: 'Cliente' }
          ]
        }
      },
      {
        path: 'producto',
        component: ProductoComponent,
        data: {
          title: 'Producto',
          urls: [
            { title: 'Mantenimiento', url: '/mantenimiento' },
            { title: 'Producto' }
          ]
        }
      }
    ]
  }
];
