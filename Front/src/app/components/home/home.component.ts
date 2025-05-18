import { Component } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { CommonModule } from '@angular/common';
import { SidebarModule } from 'primeng/sidebar';
import { CardModule } from 'primeng/card';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  imports: [
    ButtonModule,
    ProgressBarModule,
    CommonModule,
    SidebarModule,
    CardModule
  ],
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  // Audio components for the right sidebar
  audioComponents: string[] = [
    'Amplificador',
    'Alto-falante',
    'Subwoofer',
    'Central multimídia',
    'Crossovers',
    'Tweeters',
    'Central multimídia',
    'Subwoofer'
  ];

  // Status indicators data
  statusIndicators = [
    { label: 'Potência de Grave', value: 75 },
    { label: 'Consumo de Energia', value: 50 },
    { label: 'Custo Financeiro', value: 25 }
  ];

  // Methods for navigation
  previousCar() {
    console.log('Navigate to previous car');
  }

  nextCar() {
    console.log('Navigate to next car');
  }

  advance() {
    console.log('Advance to next step');
  }
}
