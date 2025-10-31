import { Routes } from '@angular/router';
import { StarterComponent } from './starter/starter.component';
import { ProfileComponent } from './profile/profile.component';
import { AccountComponent } from './account/account.component';

export const PagesRoutes: Routes = [
  {
    path: '',
    component: StarterComponent,
    data: {
      title: 'Starter',
      urls: [
        { title: 'Dashboard', url: '/dashboard' },
        { title: 'Starter' },
      ],
    },
  },
  {
    path: 'perfil',
    component: ProfileComponent,
    data: {
      title: 'Mi Perfil',
      urls: [
        { title: 'Dashboard', url: '/dashboard' },
        { title: 'Mi Perfil' },
      ],
    },
  },
  {
    path: 'cuenta',
    component: AccountComponent,
    data: {
      title: 'Mi Cuenta',
      urls: [
        { title: 'Dashboard', url: '/dashboard' },
        { title: 'Mi Cuenta' },
      ],
    },
  },
];
