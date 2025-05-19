import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';

interface NavItem {
  label: string;
  icon: string;
  route?: string;
  children?: NavItem[];
}

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ButtonModule
  ]
})
export class LayoutComponent {
  // Itens de navegação para a barra lateral
  navItems: NavItem[] = [
    {
      label: 'Montagem',
      icon: 'pi pi-clock',
      children: [
        { label: 'Sub Montagem', icon: '' },
        { label: 'Sub Montagem', icon: '' },
        { label: 'Sub Montagem', icon: '' }
      ]
    },
    {
      label: 'Preinfinições',
      icon: 'pi pi-file',
      route: '/preinfinicoes'
    },
    {
      label: 'Projetos',
      icon: 'pi pi-folder',
      route: '/projetos'
    },
    {
      label: 'Loja',
      icon: 'pi pi-shopping-cart',
      route: '/loja'
    },
    {
      label: 'Configurações',
      icon: 'pi pi-cog',
      route: '/configuracoes'
    },
    {
      label: 'Home',
      icon: 'pi pi-home',
      route: '/home'
    }
  ];

  // Estado de expansão para o item Montagem
  expandedItem: string | null = 'Montagem'; // Começa expandido

  constructor(private router: Router) {}

  // Navegar para uma rota
  navigateTo(route: string | undefined): void {
    if (route) {
      this.router.navigate([route]);
    }
  }

  // Alternar a expansão de um item
  toggleExpand(label: string): void {
    this.expandedItem = this.expandedItem === label ? null : label;
  }

  // Verificar se um item está expandido
  isExpanded(label: string): boolean {
    return this.expandedItem === label;
  }

  // Verificar se uma rota está ativa
  isActive(route: string | undefined): boolean {
    if (!route) return false;
    return this.router.url.includes(route);
  }

  logout() {
    console.log('Logout');
  }
}
