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
import { FormsModule } from '@angular/forms';

import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';

import { modulosService } from '../../services/modulos/modulos.service';
import { altofalantesService } from '../../services/altofalantes/altofalantes.service';
import { subwoofersService } from '../../services/subwoofers/subwoofers.service';
import { crossoversService } from '../../services/crossovers/crossovers.service';
import { configuracoesService } from '../../services/configuracoes/configuracoes.service';
import { CompatibilidadeService, RequisicaoCompatibilidade } from '../../services/compatibilidade/compatibilidade.service';

interface StoreItem {
  id: string;
  name: string;
  type: string;
  price: number;
  imageUrl: string;
  description: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true,
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
    TableModule,
    FormsModule
  ],
  providers: [ConfirmationService, MessageService]
})
export class HomeComponent implements OnInit {
  @ViewChild('op') overlayPanel: OverlayPanel | undefined;

  scene!: THREE.Scene;
  camera!: THREE.PerspectiveCamera;
  renderer!: THREE.WebGLRenderer;
  controls!: OrbitControls;

  displaySaveConfirmation: boolean = false;
  displaySelectedProductsDialog: boolean = false;
  selectedProducts: StoreItem[] = [];
  itemQuantidades: { [id: string]: number } = {};

  cols: any[] = [];
  orcamentoTotal: number = 0;
  nomeProjeto: string = '';
  mostrarInputNomeProjeto: boolean = false;

  audioComponents: string[] = ['Amplificador', 'Alto-falante', 'Subwoofer', 'Crossovers'];

  statusIndicators = [
    { label: 'Potência de Grave', value: 75 },
    { label: 'Consumo de Energia', value: 50 },
    { label: 'Custo Financeiro', value: 25 }
  ];

  selectedComponentType: string | null = null;
  filteredStoreItems: StoreItem[] = [];
  storeItems: StoreItem[] = [];

  selectedComponentsByType: { [key: string]: StoreItem } = {};

  constructor(
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private modulosService: modulosService,
    private altofalantesService: altofalantesService,
    private subwoofersService: subwoofersService,
    private crossoversService: crossoversService,
    private configuracoesService: configuracoesService,
    private compatibilidadeService: CompatibilidadeService
  ) {}

  ngOnInit() {
    this.loadStoreItems();
    this.cols = [
      { field: 'name', header: 'Nome do Produto' },
      { field: 'type', header: 'Tipo' },
      { field: 'price', header: 'Preço' },
    ];
  }

  ngAfterViewInit(): void {
    this.initThreeJS();
    this.animate();
  }

  initThreeJS(): void {
    this.scene = new THREE.Scene();


    this.camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
    this.camera.position.set(0, 1.5, 5);

    this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true }); // permite fundo transparente
    this.renderer.setClearColor(0x000000, 0); // define fundo transparente
    this.renderer.setSize(window.innerWidth, window.innerHeight);

    const container = document.getElementById('car-3d-container');
    if (container) {
      container.appendChild(this.renderer.domElement);
    }

    const light = new THREE.HemisphereLight(0xffffff, 0x444444);
    this.scene.add(light);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
    directionalLight.position.set(3, 10, 10);
    this.scene.add(directionalLight);

    this.controls = new OrbitControls(this.camera, this.renderer.domElement);

    const loader = new GLTFLoader();
    loader.load(
      'assets/3Dmodels/1999_volkswagen_gol_2000_gti_g2.glb',
      (gltf) => {
        gltf.scene.scale.set(1, 1, 1);
        gltf.scene.position.set(0, 0, 0);
        gltf.scene.rotation.set(0, 135, 0);

        this.scene.add(gltf.scene);
        const ambientLight = new THREE.AmbientLight(0xffffff, 1.5);
        this.scene.add(ambientLight);

        // Luzes direcionais simulando um ambiente de estúdio
        const light1 = new THREE.DirectionalLight(0xffffff, 1);
        light1.position.set(10, 10, 10);
        this.scene.add(light1);

        const light2 = new THREE.DirectionalLight(0xffffff, 1);
        light2.position.set(-10, 10, -10);
        this.scene.add(light2);

        const light3 = new THREE.DirectionalLight(0xffffff, 0.6);
        light3.position.set(0, -10, 0); // iluminação de baixo
        this.scene.add(light3);

        const light4 = new THREE.DirectionalLight(0xffffff, 0.8);
        light4.position.set(0, 20, 0); // luz diretamente de cima
        this.scene.add(light4);

        // HemisphereLight (ilumina com cor do céu e do chão)
        const hemiLight = new THREE.HemisphereLight(0xffffff, 0xffffff, 1);
        hemiLight.position.set(0, 15, 0);
        this.scene.add(hemiLight);
      },
      undefined,
      (error: any) => {
        console.error('Erro ao carregar modelo:', error);
      }
    );
  }

  animate = () => {
    requestAnimationFrame(this.animate);
    this.controls.update();
    this.renderer.render(this.scene, this.camera);
  };

  previousCar() {}
  nextCar() {}
  advance() {
    this.mostrarInputNomeProjeto = true;
  }

  confirmarNomeProjeto() {
    if (!this.nomeProjeto.trim()) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Nome obrigatório',
        detail: 'Digite um nome para o projeto.'

      });
      return;
    }

    this.mostrarInputNomeProjeto = false;

    const subwooferIDs = this.getIdsByType('Subwoofer');
    const altofalantesIDs = this.getIdsByType('Alto-falante');
    const crossoverIDs = this.getIdsByType('Crossovers');
    const modulosIDs = this.getIdsByType('Amplificador');

    const projetoPayload: RequisicaoCompatibilidade = {
      nome: this.nomeProjeto,
      veiculo: 'Volkswagen Gol',
      relatorioPdf: 'Relatório da configuração em PDF',
      usuarioId: '4f181b66-e602-4b31-b361-badaf4b5541d',
      altoFalanteIds: altofalantesIDs,
      subwooferIds: subwooferIDs,
      moduloIds: modulosIDs,
      crossoverIds: crossoverIDs
    };

    this.compatibilidadeService.validarConfiguracao(projetoPayload).subscribe({
      next: (validacoes) => {
        const problemas = validacoes.filter(v => v.mensagem !== 'Todos os componentes estão compatíveis.');
        if (problemas.length > 0) {
          problemas.forEach(msg => {
            this.messageService.add({
              severity: 'warn',
              summary: 'Incompatibilidade',
              detail: `${msg.mensagem}${msg.sugestao ? ' | Sugestão: ' + msg.sugestao : ''}`,
              sticky: true
            });
          });
          return;
        }

        this.configuracoesService.salvarConfiguracoes(projetoPayload).subscribe({
          next: (res: any) => {
            this.orcamentoTotal = res.orcamentoTotal;
            this.messageService.add({
              severity: 'success',
              summary: 'Projeto salvo',
              detail: `O projeto "${this.nomeProjeto}" foi salvo com sucesso!`

            });
            this.displaySelectedProductsDialog = true;
          },
          error: (err: any) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erro',
              detail: 'Erro ao salvar o projeto.',
            });
          }
        });
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro de compatibilidade',
          detail: 'Falha ao validar compatibilidade.'
        });
      }
    });
  }

  loadStoreItems() {
    this.modulosService.obtermodulos().subscribe({
      next: (response: any) => {
        const base = (Array.isArray(response) ? response : response?.data) ?? [];
        this.storeItems = base.map((item: any) => ({
          id: item.id, name: item.descricao, type: 'Amplificador',
          price: item.preco, imageUrl: item.imagemUrl, description: item.descricao
        }));

        this.altofalantesService.obterAltofalantes().subscribe({
          next: (data: any) => {
            const af = Array.isArray(data) ? data : data?.data;
            const map = af?.map((item: any) => ({
              id: item.id, name: item.modelo, type: 'Alto-falante',
              price: item.preco, imageUrl: item.imagemUrl, description: item.descricao
            })) ?? [];
            this.storeItems = [...this.storeItems, ...map];
          }
        });

        this.subwoofersService.obterSubwoofers().subscribe({
          next: (data: any) => {
            const sw = Array.isArray(data) ? data : data?.data;
            const map = sw?.map((item: any) => ({
              id: item.id, name: item.modelo, type: 'Subwoofer',
              price: item.preco, imageUrl: item.imagemUrl, description: item.descricao
            })) ?? [];
            this.storeItems = [...this.storeItems, ...map];
          }
        });

        this.crossoversService.obterCrossovers().subscribe({
          next: (data: any) => {
            const cr = Array.isArray(data) ? data : data?.data;
            const map = cr?.map((item: any) => ({
              id: item.id, name: item.tipo, type: 'Crossovers',
              price: item.preco, imageUrl: item.imagemUrl, description: item.descricao
            })) ?? [];
            this.storeItems = [...this.storeItems, ...map];
          }
        });
      }
    });
  }

  toggleOverlayPanel(event: Event, componentType: string) {
    this.selectedComponentType = componentType;
    this.filteredStoreItems = this.storeItems.filter(item => item.type === componentType);
    this.overlayPanel?.toggle(event);
  }

  selectStoreItem(item: StoreItem, event?: Event) {
    if (event) event.stopPropagation();

    // Garante que os campos usados na tabela estejam presentes e padronizados
    const normalizedItem: StoreItem = {
      ...item,
      name: item.name,
      type: item.type || 'Desconhecido',
      price: item.price
    };

    // Incrementa a quantidade
    if (!this.itemQuantidades) this.itemQuantidades = {};
    this.itemQuantidades[normalizedItem.id] = (this.itemQuantidades[normalizedItem.id] || 0) + 1;

    // Adiciona o item apenas se ainda não estiver listado
    const existe = this.selectedProducts.find(p => p.id === normalizedItem.id);
    if (!existe) {
      this.selectedProducts.push(normalizedItem);
    }

    // Mensagem de sucesso
    this.messageService.add({
      severity: 'success',
      summary: 'Adicionado',
      detail: `${normalizedItem.name} foi adicionado à sua lista.`
    });

    if (this.overlayPanel) {
      this.overlayPanel.hide();
    }


    const subwooferIDs = this.getIdsByType('Subwoofer');
    const altoFalantesIDs = this.getIdsByType('Alto-falante');
    const crossoverIDs = this.getIdsByType('Crossovers');
    const modulosIDs = this.getIdsByType('Amplificador');


    const projetoPayload: RequisicaoCompatibilidade = {
      nome: 'Prévia de Compatibilidade',
      veiculo: 'Volkswagen Gol',
      relatorioPdf: 'preview.pdf',
      usuarioId: '4f181b66-e602-4b31-b361-badaf4b5541d',
      altoFalanteIds: altoFalantesIDs,
      subwooferIds: subwooferIDs,
      moduloIds: modulosIDs,
      crossoverIds: crossoverIDs
    };

    this.compatibilidadeService.validarConfiguracao(projetoPayload).subscribe({
      next: (res) => {
        const problemas = res.filter(p => p.mensagem !== 'Todos os componentes estão compatíveis.');
        problemas.forEach(p => {
          this.messageService.add({
            severity: 'warn',
            summary: 'Incompatibilidade',
            detail: `${p.mensagem}${p.sugestao ? ' | Sugestão: ' + p.sugestao : ''}`,
            sticky: true
          });
        });
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao validar compatibilidade.' });
      }
    });
  }

  getItemCount(id: string): number {
    return this.itemQuantidades?.[id] || 0;
  }

  removerQuantidade(item: StoreItem) {
    const qtdAtual = this.itemQuantidades[item.id] || 0;

    if (qtdAtual > 1) {
      this.itemQuantidades[item.id] -= 1;
    } else {
      delete this.itemQuantidades[item.id];
      this.selectedProducts = this.selectedProducts.filter(p => p.id !== item.id);
    }

    // Reconstruir os arrays de IDs com base nas quantidades
    const subwooferIDs = this.getIdsByType('Subwoofer');
    const altoFalantesIDs = this.getIdsByType('Alto-falante');
    const crossoverIDs = this.getIdsByType('Crossovers');
    const modulosIDs = this.getIdsByType('Amplificador');

    const projetoPayload: RequisicaoCompatibilidade = {
      nome: 'Prévia de Compatibilidade',
      veiculo: 'Volkswagen Gol',
      relatorioPdf: 'preview.pdf',
      usuarioId: '4f181b66-e602-4b31-b361-badaf4b5541d',
      altoFalanteIds: altoFalantesIDs,
      subwooferIds: subwooferIDs,
      moduloIds: modulosIDs,
      crossoverIds: crossoverIDs
    };

    this.compatibilidadeService.validarConfiguracao(projetoPayload).subscribe({
      next: (res) => {
        const problemas = res.filter(p => p.mensagem !== 'Todos os componentes estão compatíveis.');
        problemas.forEach(p => {
          this.messageService.add({
            severity: 'warn',
            summary: 'Incompatibilidade',
            detail: `${p.mensagem}${p.sugestao ? ' | Sugestão: ' + p.sugestao : ''}`,
            sticky: true
          });
        });
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Falha ao validar compatibilidade.'
        });
      }
    });
  }

  getIdsByType(type: string): string[] {
    return this.selectedProducts
      .filter(p => p.type === type)
      .flatMap(p => Array(this.itemQuantidades[p.id] || 1).fill(p.id));
  }

  limparNomeProjeto() {
    this.nomeProjeto = '';
    this.mostrarInputNomeProjeto = false;
  }

  limparProdutosSelecionados() {
    this.selectedProducts = [];
    this.nomeProjeto = '';
    this.itemQuantidades = {};
  }
}
