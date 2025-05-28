import { Component, type OnInit } from "@angular/core"
import { CommonModule, CurrencyPipe, SlicePipe } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { DataViewModule } from "primeng/dataview"
import { ButtonModule } from "primeng/button"
import { SelectButtonModule } from "primeng/selectbutton"
import { TagModule } from "primeng/tag"

interface Product {
  id: string
  name: string
  description: string
  price: number
  image: string
  category: string
  inventoryStatus: "INSTOCK" | "LOWSTOCK" | "OUTOFSTOCK"
  rating: number
}

interface LayoutOption {
  label: string
  value: "list" | "grid"
  icon: string
}

@Component({
  selector: "app-loja",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DataViewModule,
    ButtonModule,
    SelectButtonModule,
    TagModule,
    CurrencyPipe
  ],
  templateUrl: "./loja.component.html",
  styleUrls: ["./loja.component.css"],
})
export class LojaComponent implements OnInit {
  products: Product[] = []
  layout: "list" | "grid" = "grid"
  options: LayoutOption[] = []

  constructor() {
    this.options = [
      { value: "list", label: "Lista", icon: "pi pi-bars" },
      { value: "grid", label: "Grade", icon: "pi pi-table" },
    ]
  }

  ngOnInit() {
    // Inicializar os produtos
    this.loadProducts()
  }

  loadProducts() {
    this.products = [
      {
        id: "1",
        name: "Amplificador Premium X",
        description: "Amplificador de alta potência para graves profundos e som cristalino em qualquer volume.",
        price: 1299.99,
        image: "assets/images/amplifier.jpg",
        category: "Áudio Automotivo",
        inventoryStatus: "INSTOCK",
        rating: 5,
      },
      {
        id: "2",
        name: 'Subwoofer Destroyer 12"',
        description: "Subwoofer de 12 polegadas com potência de 1000W RMS para graves impactantes e definidos.",
        price: 899.99,
        image: "assets/images/subwoofer.jpg",
        category: "Áudio Automotivo",
        inventoryStatus: "LOWSTOCK",
        rating: 4,
      },
      {
        id: "3",
        name: "Central Multimídia ConnectPRO",
        description: "Central multimídia com tela touch de 9 polegadas, GPS integrado e espelhamento sem fio.",
        price: 1599.99,
        image: "assets/images/multimedia.jpg",
        category: "Eletrônicos Veiculares",
        inventoryStatus: "INSTOCK",
        rating: 5,
      },
      {
        id: "4",
        name: "Kit Alto-falantes Acoustic Hi-Fi",
        description: "Kit com 4 alto-falantes (2 woofers, 2 tweeters) para qualidade sonora superior e imersiva.",
        price: 799.99,
        image: "assets/images/speakers.jpg",
        category: "Áudio Automotivo",
        inventoryStatus: "OUTOFSTOCK",
        rating: 4,
      },
      {
        id: "5",
        name: "Câmera de Ré Vision HD",
        description: "Câmera de ré com visão noturna e ângulo amplo para manobras seguras.",
        price: 249.9,
        image: "/placeholder.svg?height=200&width=300",
        category: "Acessórios Veiculares",
        inventoryStatus: "INSTOCK",
        rating: 3,
      },
      {
        id: "6",
        name: "Módulo de Potência Digital",
        description: "Módulo de potência digital com 4 canais e controle remoto para ajuste fino do som.",
        price: 1899.99,
        image: "/placeholder.svg?height=200&width=300",
        category: "Áudio Automotivo",
        inventoryStatus: "INSTOCK",
        rating: 5,
      },
    ]
  }

  getSeverity(product: Product): "success" | "warning" | "danger" | undefined {
    switch (product.inventoryStatus) {
      case "INSTOCK":
        return "success"
      case "LOWSTOCK":
        return "warning"
      case "OUTOFSTOCK":
        return "danger"
      default:
        return undefined
    }
  }

  trackByProductId(index: number, product: Product): string {
    return product.id
  }
}
