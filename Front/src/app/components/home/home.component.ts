import { Component, ViewChild, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ButtonModule } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { CardModule } from 'primeng/card';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { OverlayPanel } from 'primeng/overlaypanel';
import { TagModule } from 'primeng/tag';

import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { TableModule } from 'primeng/table';

import { modulosService } from '../../services/modulos.service';


interface StoreItem {
  id: string;
  name: string;
  type: string;
  price: number;
  imageUrl: string;
  status: 'Em Estoque' | 'Estoque Baixo' | 'Sem Estoque';
  rating: number;
  description: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  imports: [
    CommonModule,
    ButtonModule,
    ProgressBarModule,
    CardModule,
    OverlayPanelModule,
    TagModule,
    ConfirmDialogModule,
    ToastModule,
    DialogModule,
    TableModule
  ],
  styleUrls: ['./home.component.css'],
  providers: [ConfirmationService, MessageService]
})
export class HomeComponent implements OnInit {
  @ViewChild('op') overlayPanel: OverlayPanel | undefined;

  displaySaveConfirmation: boolean = false;
  displaySelectedProductsDialog: boolean = false;

  selectedProducts: any[] = [];

  cols: any[] = [];

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

  selectedComponentType: string | null = null;
  filteredStoreItems: StoreItem[] = [];
  storeItems: StoreItem[] = [];

  constructor(
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private modulosService: modulosService
  ) {}

  ngOnInit() {
    this.loadStoreItems();

    this.cols = [
      { field: 'name', header: 'Nome do Produto' },
      { field: 'type', header: 'Tipo' },
      { field: 'price', header: 'Preço' },
    ];
  }

  loadStoreItems() {
    this.modulosService.obtermodulos().subscribe({
      next: (response: StoreItem[]) => {
        this.storeItems = response;
      },
      error: (err) => {
        console.error('Erro ao carregar dados da API', err);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao carregar os produtos da API.' });
      }
    });
  }

  previousCar() {
    console.log('Navigate to previous car');
  }

  nextCar() {
    console.log('Navigate to next car');
  }

  advance() {
    this.confirmSaveProject();
  }

  confirmSaveProject() {
    this.confirmationService.confirm({
      message: 'Deseja salvar as configurações do seu projeto atual?',
      header: 'Confirmar Salvar Projeto',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Salvar',
      rejectLabel: 'Não Salvar',
      accept: () => {
        this.messageService.add({ severity: 'success', summary: 'Salvo', detail: 'Seu projeto foi salvo com sucesso!' });
        this.showSelectedProducts();
      },
      reject: () => {
        this.messageService.add({ severity: 'info', summary: 'Não Salvo', detail: 'Projeto não foi salvo.' });
      }
    });
  }

  showSelectedProducts() {
    this.displaySelectedProductsDialog = true;
  }

  toggleOverlayPanel(event: Event, componentType: string) {
    this.selectedComponentType = componentType;
    this.filteredStoreItems = this.storeItems.filter(item => item.type === componentType);

    if (this.overlayPanel) {
      this.overlayPanel.toggle(event);
    }
  }

  selectStoreItem(item: StoreItem) {
    console.log('Item selecionado:', item);
    this.selectedProducts.push(item);
    this.messageService.add({severity:'success', summary:'Adicionado!', detail:`${item.name} foi adicionado à sua lista.`});

    if (this.overlayPanel) {
      this.overlayPanel.hide();
    }
  }

  getStatusSeverity(status: string): string {
    switch (status) {
      case 'Em Estoque': return 'success';
      case 'Estoque Baixo': return 'warn';
      case 'Sem Estoque': return 'danger';
      default: return 'info';
    }
  }
}
