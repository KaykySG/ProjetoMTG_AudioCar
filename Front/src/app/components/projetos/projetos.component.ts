import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TabViewModule } from 'primeng/tabview';
import { TabPanel } from 'primeng/tabview';
import { FormsModule } from '@angular/forms';
import { Project } from './project';

// Importações para o ConfirmDialog e mensagens
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, ConfirmEventType } from 'primeng/api';
import { MessageService } from 'primeng/api'; // Para exibir mensagens (opcional, pode ser ToastModule)
import { ToastModule } from 'primeng/toast'; // Para notificações toast (geralmente melhor que MessagesModule)


@Component({
  selector: 'app-projetos',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    TabViewModule,
    TabPanel,
    FormsModule,
    CurrencyPipe,
    ConfirmDialogModule, // Importa o módulo de diálogo de confirmação
    ToastModule // Importa o módulo de toast para feedback
  ],
  templateUrl: './projetos.component.html',
  styleUrl: './projetos.component.css',
  providers: [ConfirmationService, MessageService] // Adicione os serviços aqui
})
export class ProjetosComponent implements OnInit {
  projects = signal<Project[]>([]);
  selectedTab: string = 'Builds';

  // Injetando ConfirmationService e MessageService
  constructor(private confirmationService: ConfirmationService, private messageService: MessageService) {}

  ngOnInit() {
    this.loadProjects();
  }

  loadProjects() {
    const dummyProjects: Project[] = [
      {
        id: '1',
        name: 'Build Intel X AMD',
        price: 4220.79,
        savedDate: 'há 9 meses',
      },
      {
        id: '2',
        name: 'Build Full AMD',
        price: 4179.22,
        savedDate: 'ano passado',
      },
      {
        id: '3',
        name: 'Build Intel',
        price: 4286.47,
        savedDate: 'ano passado',
      },
      {
        id: '4',
        name: 'Build AMD',
        price: 5529.22,
        savedDate: 'ano passado',
      },
      {
        id: '5',
        name: 'Projeto PC GAMER 2024',
        price: 4331.27,
        savedDate: 'ano passado',
      },
    ];
    this.projects.set(dummyProjects);
  }

  // Função modificada para editar com confirmação
  confirmEdit(project: Project) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja editar o projeto "${project.name}"?`,
      header: 'Confirmar Edição',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      accept: () => {
        // Ação de edição confirmada
        this.messageService.add({severity:'success', summary:'Confirmado', detail:`Projeto "${project.name}" editado com sucesso!`});
        this.editProject(project); // Chama a lógica real de edição
      },
      reject: (type: ConfirmEventType) => {
        switch(type) {
          case ConfirmEventType.REJECT:
            this.messageService.add({severity:'error', summary:'Rejeitado', detail:'Edição cancelada.'});
            break;
          case ConfirmEventType.CANCEL:
            this.messageService.add({severity:'warn', summary:'Cancelado', detail:'Edição cancelada.'});
            break;
        }
      }
    });
  }

  // Função de edição real (que será chamada após a confirmação)
  editProject(project: Project) {
    console.log('Lógica de edição para:', project.name);
    // Aqui você implementaria a lógica real de navegação para a tela de edição,
    // ou abrir um modal de edição, etc.
  }

  // Função modificada para excluir com confirmação
  confirmDelete(project: Project) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja excluir o projeto "${project.name}"? Esta ação não pode ser desfeita.`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-info-circle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      acceptButtonStyleClass: "p-button-danger", // Estilo para o botão de aceitar
      accept: () => {
        // Ação de exclusão confirmada
        this.messageService.add({severity:'success', summary:'Deletado', detail:`Projeto "${project.name}" excluído com sucesso!`});
        this.deleteProject(project); // Chama a lógica real de exclusão
      },
      reject: (type: ConfirmEventType) => {
        switch(type) {
          case ConfirmEventType.REJECT:
            this.messageService.add({severity:'error', summary:'Rejeitado', detail:'Exclusão cancelada.'});
            break;
          case ConfirmEventType.CANCEL:
            this.messageService.add({severity:'warn', summary:'Cancelado', detail:'Exclusão cancelada.'});
            break;
        }
      }
    });
  }

  // Função de exclusão real (que será chamada após a confirmação)
  deleteProject(project: Project) {
    console.log('Lógica de exclusão real para:', project.name);
    this.projects.set(this.projects().filter(p => p.id !== project.id));
  }

  viewProjectDetails(project: Project) {
    console.log('Visualizar detalhes do projeto:', project.name, 'ID:', project.id);
    // Lógica para navegar para a página de detalhes
  }
}
