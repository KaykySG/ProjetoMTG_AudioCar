import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataViewModule } from 'primeng/dataview';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';


@Component({
  selector: 'app-loja',
  templateUrl: './loja.component.html',
  imports: [
    CommonModule,
    DataViewModule,
    CardModule,
    ButtonModule

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
      image: 'assets/images/amplifier.jpg'
    },
    {
      id: '2',
      name: 'Subwoofer 12"',
      description: 'Subwoofer de 12 polegadas com potência de 1000W',
      price: 899.99,
      image: 'assets/images/subwoofer.jpg'
    },
    {
      id: '3',
      name: 'Central Multimídia',
      description: 'Central multimídia com tela touch de 9 polegadas',
      price: 1599.99,
      image: 'assets/images/multimedia.jpg'
    },
    {
      id: '4',
      name: 'Kit Alto-falantes',
      description: 'Kit com 4 alto-falantes de alta qualidade',
      price: 799.99,
      image: 'assets/images/speakers.jpg'
    }
  ];
}
