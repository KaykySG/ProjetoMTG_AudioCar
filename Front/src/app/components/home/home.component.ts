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

import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';

import { modulosService } from '../../services/modulos/modulos.service';
import { autofalantesService } from '../../services/autofalantes/autofalantes.service';
import { subwoofersService } from '../../services/subwoofers/subwoofers.service';
import { crossoversService } from '../../services/crossovers/crossovers.service';

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

  scene!: THREE.Scene;
  camera!: THREE.PerspectiveCamera;
  renderer!: THREE.WebGLRenderer;
  controls!: OrbitControls;

  displaySaveConfirmation: boolean = false;
  displaySelectedProductsDialog: boolean = false;

  selectedProducts: any[] = [];

  cols: any[] = [];

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
    private modulosService: modulosService,
    private altofalantesService: autofalantesService,
    private subwoofersService: subwoofersService,
    private crossoversService: crossoversService
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
    // 1. Carrega os módulos primeiro, como no seu código original
    this.modulosService.obtermodulos().subscribe({
      next: (response: any) => {
        const rawData = Array.isArray(response) ? response : response?.data;

        if (!Array.isArray(rawData)) {
          console.error('Formato inesperado da API', response);
          return;
        }

        // Lista base: apenas módulos
        this.storeItems = rawData.map((item: any) => ({
          id: item.id || '',
          name: item.descricao,
          type: 'Amplificador',
          price: item.preco || 0,
          imageUrl: item.imagemUrl || 'assets/images/default.png',
          description: item.descricao || `Potência: ${item.potencia || 'N/A'}`
        }));

        // 2. Agora adiciona Alto-falantes
        this.altofalantesService.obterAutofalantes().subscribe({
          next: (falantes: any) => {
            const data = Array.isArray(falantes) ? falantes : falantes?.data;

            if (!Array.isArray(data)) {
              console.error('Formato inesperado da API de alto-falantes:', falantes);
              return;
            }

            const altofalantesList = data.map((item: any) => ({
              id: item.id,
              name: item.modelo,
              type: 'Alto-falante',
              price: item.potenciaRmsW || 0,
              imageUrl: item.imagemUrl,
              description: item.descricao || ''
            }));

            this.storeItems = [...this.storeItems, ...altofalantesList];
          }
        });

        // 3. Subwoofers
        this.subwoofersService.obterSubwoofers().subscribe({
          next: (subs: any) => {
            const data = Array.isArray(subs) ? subs : subs?.data;

            if (!Array.isArray(data)) {
              console.error('Formato inesperado da API de subwoofers:', subs);
              return;
            }

            const subwooferList = data.map((item: any) => ({
              id: item.id,
              name: item.modelo,
              type: 'Subwoofer',
              price: item.potenciaRms || 0,
              imageUrl: item.imagemUrl,
              description: item.descricao || ''
            }));

            this.storeItems = [...this.storeItems, ...subwooferList];
          }
        });

        // 4. Crossovers
        this.crossoversService.obterCrossovers().subscribe({
          next: (cross: any) => {
            const data = Array.isArray(cross) ? cross : cross?.data;

            if (!Array.isArray(data)) {
              console.error('Formato inesperado da API de crossovers:', cross);
              return;
            }

            const crossoverList = data.map((item: any) => ({
              id: item.id,
              name: item.tipo,
              type: 'Crossovers',
              price: item.frequenciaCorte || 0,
              imageUrl: item.imagemUrl,
              description: item.descricao || ''
            }));

            this.storeItems = [...this.storeItems, ...crossoverList];
          }
        });
      },
      error: (err) => {
        console.error('Erro ao carregar dados da API', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Falha ao carregar os produtos da API.'
        });
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
