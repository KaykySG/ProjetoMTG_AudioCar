import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { PanelMenuModule } from 'primeng/panelmenu';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  imports: [
    CommonModule,
    RouterModule,
    ButtonModule,
    PanelMenuModule
  ],
  styleUrls: ['./layout.component.css']
})
export class LayoutComponent {
  // Navigation items for the left sidebar
  navItems: MenuItem[] = [
    {
      label: 'Montagem',
      icon: 'pi pi-clock',
      expanded: true,
      items: [
        { label: 'Sub Montagem', styleClass: 'sub-montagem-item' },
        { label: 'Sub Montagem', styleClass: 'sub-montagem-item' },
        { label: 'Sub Montagem', styleClass: 'sub-montagem-item' }
      ]
    },
    {
      label: 'Preinfinições',
      icon: 'pi pi-file',
      routerLink: '/preinfinicoes'
    },
    {
      label: 'Projetos',
      icon: 'pi pi-folder',
      routerLink: '/projetos'
    },
    {
      label: 'Loja',
      icon: 'pi pi-shopping-cart',
      routerLink: '/loja'
    },
    {
      label: 'Configurações',
      icon: 'pi pi-cog',
      routerLink: '/configuracoes'
    },
    {
      label: 'Home',
      icon: 'pi pi-home',
      routerLink: '/home'
    }
  ];

  logout() {
    console.log('Logout');
  }
}
