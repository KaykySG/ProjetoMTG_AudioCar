import { Component, ViewChild, OnInit } from '@angular/core'; // Adicionado OnInit
import { ButtonModule } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { OverlayPanel } from 'primeng/overlaypanel';

// Adicionar importações para o estilo de cards (se necessário)
import { TagModule } from 'primeng/tag'; // Para "Em Estoque", "Estoque Baixo"

// Interface para os itens da loja
interface StoreItem {
  id: string;
  name: string;
  type: string; // Ex: 'Amplificador', 'Alto-falante', 'Subwoofer'
  price: number;
  imageUrl: string;
  status: 'Em Estoque' | 'Estoque Baixo' | 'Sem Estoque';
  rating: number;
  description: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  // Certifique-se de que esses módulos estejam importados aqui também, se usados no template
  imports: [
    ButtonModule,
    ProgressBarModule,
    CommonModule,
    CardModule,
    OverlayPanelModule,
    TagModule // Adicionado TagModule
  ],
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit { // Implementa OnInit
  @ViewChild('op') overlayPanel: OverlayPanel | undefined;

  audioComponents: string[] = [
    'Amplificador',
    'Alto-falante',
    'Subwoofer',
    'Crossovers',
  ];

  statusIndicators = [
    { label: 'Potência de Grave', value: 75 },
    { label: 'Consumo de Energia', value: 50 },
    { label: 'Custo Financeiro', value: 25 }
  ];

  selectedComponentType: string | null = null; // Renomeado para evitar conflito com 'componentName'
  filteredStoreItems: StoreItem[] = []; // Itens que serão mostrados no OverlayPanel

  // Dados de exemplo da loja
  storeItems: StoreItem[] = [];

  ngOnInit() {
    this.loadStoreItems();
  }

  loadStoreItems() {
    // Populando com dados baseados na imagem image_5ef717.png
    this.storeItems = [
      {
        id: 'amp-1',
        name: 'Amplificador Profissional 2000W',
        type: 'Amplificador',
        price: 1299.99,
        imageUrl: '/assets/images/amplificador.jpg', // Substitua pelo caminho real da sua imagem
        status: 'Em Estoque',
        rating: 4.8,
        description: 'Amplificador de potência profissional com 2000W RMS, ideal para eventos e shows...'
      },
      {
        id: 'amp-2',
        name: 'Amplificador Compacto 800W',
        type: 'Amplificador',
        price: 799.99,
        imageUrl: 'assets/stetsom_ir400.4.png', // Substitua pelo caminho real da sua imagem
        status: 'Estoque Baixo',
        rating: 4.2,
        description: 'Amplificador compacto com 800W RMS, perfeito para pequenos eventos e estúdios. Design leve...'
      },
      {
        id: 'amp-3',
        name: 'Amplificador Valvulvado Vintage',
        type: 'Amplificador',
        price: 2499.99,
        imageUrl: 'assets/stetsom_ir400.4.png', // Substitua pelo caminho real da sua imagem
        status: 'Sem Estoque',
        rating: 4.9,
        description: 'Amplificador valvulado com som vintage e caloroso. Ideal para guitarristas que buscam...'
      },
      {
        id: 'speaker-1',
        name: 'Alto-falante Coaxial 6.5"',
        type: 'Alto-falante',
        price: 350.00,
        imageUrl: 'assets/speaker_example.png', // Substitua pelo caminho real
        status: 'Em Estoque',
        rating: 4.5,
        description: 'Alto-falante coaxial de alta fidelidade para portas...'
      },
      {
        id: 'sub-1',
        name: 'Subwoofer Ativo 12"',
        type: 'Subwoofer',
        price: 950.00,
        imageUrl: 'assets/subwoofer_example.png', // Substitua pelo caminho real
        status: 'Em Estoque',
        rating: 4.7,
        description: 'Subwoofer potente para graves profundos e impactantes.'
      }
      // Adicione mais itens conforme necessário para outros tipos de componentes
    ];
  }

  previousCar() {
    console.log('Navigate to previous car');
  }

  nextCar() {
    console.log('Navigate to next car');
  }

  advance() {
    console.log('Advance to next step');
  }

  toggleOverlayPanel(event: Event, componentType: string) {
    this.selectedComponentType = componentType;
    // Filtra os itens da loja com base no tipo de componente selecionado
    this.filteredStoreItems = this.storeItems.filter(item => item.type === componentType);

    if (this.overlayPanel) {
      this.overlayPanel.toggle(event);
    }
  }

  // Novo método para selecionar um item dentro do pop-up
  selectStoreItem(item: StoreItem) {
    console.log('Item selecionado:', item);
    // Aqui você pode adicionar a lógica para adicionar o item ao carro,
    // fechar o pop-up, atualizar o status, etc.
    if (this.overlayPanel) {
      this.overlayPanel.hide(); // Oculta o painel após a seleção
    }
    // Exemplo: this.messageService.add({severity:'info', summary:'Item Adicionado', detail:`${item.name} adicionado ao seu setup!`});
  }

  // Helper para obter a classe de cor do tag de status
  getStatusSeverity(status: string): string {
    switch (status) {
      case 'Em Estoque':
        return 'success';
      case 'Estoque Baixo':
        return 'warn';
      case 'Sem Estoque':
        return 'danger';
      default:
        return 'info';
    }
  }
}
