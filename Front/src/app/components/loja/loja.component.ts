import { Component } from "@angular/core";
import type { Product } from "./product";
import { DataView } from "primeng/dataview";
import { ButtonModule } from "primeng/button";
import { SelectButton } from "primeng/selectbutton";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { signal } from "@angular/core";

@Component({
  selector: "app-loja",
  templateUrl: "./loja.component.html",
  standalone: true,
  styleUrls: ['./loja.component.css'],
  imports: [DataView, ButtonModule, CommonModule, SelectButton, FormsModule],
})
export class LojaComponent {
  layout: "list" | "grid" = "grid";
  products = signal<Product[]>([]);
  options: ("list" | "grid")[] = ["list", "grid"];

  ngOnInit() {
    const produtos: Product[] = [
      {
        id: "001",
        code: "ALLANLIMA",
        name: "Allan Lima Som Automotivo",
        description:
          "Especializada em projetos personalizados de som pancadão e trio elétrico. Destaque em campeonatos de SPL.",
        image: "AlanSom.PNG",
        price: 0,
        category: "Montadora de Som",
        quantity: 2,
        inventoryStatus: "INSTOCK",
        rating: 4.9
      },
      {
        id: "002",
        code: "CONECTION",
        name: "Conection Audio",
        description:
          "Projetos de som automotivo profissional com acabamento de alto nível. Atendimento em todo o Brasil.",
        image: "conection.PNG",
        price: 0,
        category: "Montadora de Som",
        quantity: 1,
        inventoryStatus: "LOWSTOCK",
        rating: 4.8
      },
      {
        id: "003",
        code: "LEOVOLKS",
        name: "Leovolks Sound Garage",
        description:
          "Montadora referência em Goiás, especializada em som interno com qualidade e fidelidade sonora.",
        image: "leovolks.PNG",
        price: 0,
        category: "Montadora de Som",
        quantity: 0,
        inventoryStatus: "OUTOFSTOCK",
        rating: 4.7
      },
      {
        id: "004",
        code: "RBR",
        name: "RBR Sound Custom",
        description:
          "Projetos exclusivos com integração de iluminação, som e acabamento de alto padrão.",
        image: "RBR.png",
        price: 0,
        category: "Montadora de Som",
        quantity: 3,
        inventoryStatus: "INSTOCK",
        rating: 4.9
      },
      {
        id: "005",
        code: "DBS",
        name: "DBS Audio Projects",
        description:
          "Equipe técnica focada em campeonatos e competições de SPL. Kit competição completo disponível.",
        image: "DBS.png",
        price: 0,
        category: "Montadora de Som",
        quantity: 2,
        inventoryStatus: "LOWSTOCK",
        rating: 5.0
      }
    ];

    this.products.set(produtos);
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case "INSTOCK":
        return "Em Estoque";
      case "LOWSTOCK":
        return "Estoque Baixo";
      case "OUTOFSTOCK":
        return "Sem Estoque";
      default:
        return status;
    }
  }
}
