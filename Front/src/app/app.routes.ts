import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent)
      },
      {
        path: 'loja',
        loadComponent: () => import('./components/loja/loja.component').then(m => m.LojaComponent)
      },
      {
        path: 'projetos',
        loadComponent: () => import('./components/projetos/projetos.component').then(m => m.ProjetosComponent)
      },
      {
        path: 'preinfinicoes',
        loadComponent: () => import('./components/predefinicoes/preinfinicoes.component').then(m => m.PreinfinicoesComponent)
      },
      {
        path: 'configuracoes',
        loadComponent: () => import('./components/configuracoes/configuracoes.component').then(m => m.ConfiguracoesComponent)
      }
    ]
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
