import { Component } from "@angular/core"
import type { Product } from "./product"
import { DataView } from "primeng/dataview"
import { ButtonModule } from "primeng/button"
import { SelectButton } from "primeng/selectbutton"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { signal } from "@angular/core"

@Component({
  selector: "app-loja",
  templateUrl: "./loja.component.html",
  standalone: true,
  styleUrls: ['./loja.component.css'],
  imports: [DataView, ButtonModule, CommonModule, SelectButton, FormsModule],
})
export class LojaComponent {
  layout: "list" | "grid" = "grid"
  products = signal<Product[]>([])
  options: ("list" | "grid")[] = ["list", "grid"]

  ngOnInit() {
    // Produtos de exemplo
    const produtos: Product[] = [
      {
        id: "1000",
        code: "AMP001",
        name: "Amplificador Profissional 2000W",
        description:
          "Amplificador de potência profissional com 2000W RMS, ideal para eventos e shows. Tecnologia avançada com proteção contra sobrecarga.",
        image: "amplificador.jpg",
        price: 1299.99,
        category: "Amplificadores",
        quantity: 5,
        inventoryStatus: "INSTOCK",
        rating: 4.8,
      },
      {
        id: "1001",
        code: "AMP002",
        name: "Amplificador Compacto 800W",
        description:
          "Amplificador compacto com 800W RMS, perfeito para pequenos eventos e estúdios. Design leve e portátil.",
        image: "amplificador-compacto.jpg",
        price: 799.99,
        category: "Amplificadores",
        quantity: 3,
        inventoryStatus: "LOWSTOCK",
        rating: 4.2,
      },
      {
        id: "1002",
        code: "AMP003",
        name: "Amplificador Valvulado Vintage",
        description:
          "Amplificador valvulado com som vintage e caloroso. Ideal para guitarristas que buscam timbre clássico.",
        image: "amplificador-valvulado.jpg",
        price: 2499.99,
        category: "Amplificadores",
        quantity: 0,
        inventoryStatus: "OUTOFSTOCK",
        rating: 4.9,
      },
      {
        id: "1003",
        code: "AMP004",
        name: "Amplificador Multicanal 1500W",
        description: "Amplificador multicanal com 1500W RMS, 4 entradas independentes e equalizador integrado.",
        image: "amplificador-multicanal.jpg",
        price: 1899.99,
        category: "Amplificadores",
        quantity: 7,
        inventoryStatus: "INSTOCK",
        rating: 4.5,
      },
      {
        id: "1004",
        code: "AMP005",
        name: "Amplificador Digital 1200W",
        description:
          "Amplificador digital de última geração com 1200W RMS, controle via aplicativo e conectividade Bluetooth.",
        image: "amplificador-digital.jpg",
        price: 1599.99,
        category: "Amplificadores",
        quantity: 2,
        inventoryStatus: "LOWSTOCK",
        rating: 4.7,
      },
      {
        id: "1005",
        code: "AMP006",
        name: "Amplificador para Contrabaixo 500W",
        description:
          "Amplificador específico para contrabaixo com 500W RMS, equalizador de 5 bandas e compressor integrado.",
        image: "amplificador-baixo.jpg",
        price: 1099.99,
        category: "Amplificadores",
        quantity: 4,
        inventoryStatus: "INSTOCK",
        rating: 4.6,
      },
    ]

    this.products.set(produtos)
  }

  getSeverity(product: Product) {
    switch (product.inventoryStatus) {
      case "INSTOCK":
        return "success"
      case "LOWSTOCK":
        return "warn"
      case "OUTOFSTOCK":
        return "danger"
      default:
        return null
    }
  }

  getStatusLabel(status: string) {
    switch (status) {
      case "INSTOCK":
        return "Em Estoque"
      case "LOWSTOCK":
        return "Estoque Baixo"
      case "OUTOFSTOCK":
        return "Sem Estoque"
      default:
        return status
    }
  }
}
