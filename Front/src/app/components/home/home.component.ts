import { Component, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { OverlayPanel } from 'primeng/overlaypanel';
import { TagModule } from 'primeng/tag';
import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';


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
    ButtonModule,
    ProgressBarModule,
    CommonModule,
    CardModule,
    OverlayPanelModule,
    TagModule
  ],
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, AfterViewInit {
  @ViewChild('op') overlayPanel: OverlayPanel | undefined;

  scene!: THREE.Scene;
  camera!: THREE.PerspectiveCamera;
  renderer!: THREE.WebGLRenderer;
  controls!: OrbitControls;

  audioComponents: string[] = ['Amplificador', 'Alto-falante', 'Subwoofer', 'Crossovers'];

  statusIndicators = [
    { label: 'Potência de Grave', value: 75 },
    { label: 'Consumo de Energia', value: 50 },
    { label: 'Custo Financeiro', value: 25 }
  ];

  selectedComponentType: string | null = null;
  filteredStoreItems: StoreItem[] = [];
  storeItems: StoreItem[] = [];

  ngOnInit() {
    this.loadStoreItems();
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

  loadStoreItems() {
    this.storeItems = [
      {
        id: 'amp-1',
        name: 'Amplificador Profissional 2000W',
        type: 'Amplificador',
        price: 1299.99,
        imageUrl: '/assets/images/amplificador.jpg',
        status: 'Em Estoque',
        rating: 4.8,
        description: 'Amplificador de potência profissional com 2000W RMS, ideal para eventos e shows...'
      },
      {
        id: 'amp-2',
        name: 'Amplificador Compacto 800W',
        type: 'Amplificador',
        price: 799.99,
        imageUrl: 'assets/stetsom_ir400.4.png',
        status: 'Estoque Baixo',
        rating: 4.2,
        description: 'Amplificador compacto com 800W RMS, perfeito para pequenos eventos e estúdios. Design leve...'
      },
      {
        id: 'amp-3',
        name: 'Amplificador Valvulvado Vintage',
        type: 'Amplificador',
        price: 2499.99,
        imageUrl: 'assets/stetsom_ir400.4.png',
        status: 'Sem Estoque',
        rating: 4.9,
        description: 'Amplificador valvulado com som vintage e caloroso. Ideal para guitarristas que buscam...'
      },
      {
        id: 'speaker-1',
        name: 'Alto-falante Coaxial 6.5"',
        type: 'Alto-falante',
        price: 350.00,
        imageUrl: 'assets/speaker_example.png',
        status: 'Em Estoque',
        rating: 4.5,
        description: 'Alto-falante coaxial de alta fidelidade para portas...'
      },
      {
        id: 'sub-1',
        name: 'Subwoofer Ativo 12"',
        type: 'Subwoofer',
        price: 950.00,
        imageUrl: 'assets/subwoofer_example.png',
        status: 'Em Estoque',
        rating: 4.7,
        description: 'Subwoofer potente para graves profundos e impactantes.'
      }
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
    this.filteredStoreItems = this.storeItems.filter(item => item.type === componentType);
    if (this.overlayPanel) {
      this.overlayPanel.toggle(event);
    }
  }

  selectStoreItem(item: StoreItem) {
    console.log('Item selecionado:', item);
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
