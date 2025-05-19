import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { DataViewModule } from 'primeng/dataview';
import { ChipModule } from 'primeng/chip';
import { CardModule } from 'primeng/card';


@Component({
  selector: 'app-loja',
  templateUrl: './loja.component.html',
  imports: [
    CommonModule,
    DataViewModule,
    CardModule,
    ButtonModule,
    ChipModule

  ],
  styleUrls: ['./loja.component.css']
})
export class LojaComponent {
  products = [
    {
      id: '1',
      name: 'Amplificador Premium',
      description: 'Amplificador de alta potência para graves profundos',
      price: 1299.99,
      image: 'assets/images/amplificador.jpg'
    }
  ];
  constructor() {
    console.log('Dados dos produtos:', this.products);
  }
}
