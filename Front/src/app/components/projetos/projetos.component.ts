import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TabViewModule } from 'primeng/tabview';
import { TabPanel } from 'primeng/tabview';
import { FormsModule } from '@angular/forms';
import { Project } from './project';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, ConfirmEventType } from 'primeng/api';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { configuracoesService } from '../../services/configuracoes/configuracoes.service';
import { DialogModule } from 'primeng/dialog';


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
    ConfirmDialogModule,
    ToastModule,
    DialogModule
  ],
  templateUrl: './projetos.component.html',
  styleUrl: './projetos.component.css',
  providers: [ConfirmationService, MessageService]
})
export class ProjetosComponent implements OnInit {
  projetos = signal<Project[]>([]);
  selectedTab: string = 'Builds';

  displayDialog: boolean = false;
  selectedProject: Project | null = null;
  selectedProjectItems: any[] = [];
  expandedRows: { [key: string]: boolean } = {};

  constructor(
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private configuracoesService: configuracoesService
  ) {}

  onRowExpand(event: any) {
    const item = event.data;
    this.expandedRows[item.id] = true;
  }

  onRowCollapse(event: any) {
    const item = event.data;
    this.expandedRows[item.id] = false;
  }

  ngOnInit() {
    this.loadProjectsFromAPI();
  }

  loadProjectsFromAPI() {
    this.configuracoesService.getConfiguracoes().subscribe({
      next: (res) => {
        const mappedProjects: Project[] = res.map((p: any) => ({
          id: p.id,
          name: p.nomeConfiguracao,
          price: p.orcamentoTotal,
          savedDate: new Date().toLocaleDateString('pt-BR')
        }));
        this.projetos.set(mappedProjects);
      },
      error: (err) => {
        console.error('Erro ao carregar projetos da API:', err);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao carregar os projetos.' });
      }
    });
  }

  abrirDetalhesProjeto(project: Project) {
    this.selectedProject = project;
    this.displayDialog = true;
    this.selectedProjectItems = []; // limpa antes de recarregar

    this.configuracoesService.getIdsPorConfiguracao(project.id).subscribe({
      next: (config) => {
        const subs$ = this.configuracoesService.getComponentesByIds('subwoofers', config.subwoofers || []);
        const altos$ = this.configuracoesService.getComponentesByIds('altofalantes', config.altoFalantes || []);
        const mods$ = this.configuracoesService.getComponentesByIds('modulos', config.modulos || []);
        const cross$ = this.configuracoesService.getComponentesByIds('crossovers', config.crossovers || []);

        subs$.subscribe({
          next: (subs) => {
            altos$.subscribe({
              next: (altos) => {
                mods$.subscribe({
                  next: (mods) => {
                    cross$.subscribe({
                      next: (cross) => {
                        this.selectedProjectItems = [...subs, ...altos, ...mods, ...cross];
                      },
                      error: (e) => console.error('Erro ao carregar crossovers:', e)
                    });
                  },
                  error: (e) => console.error('Erro ao carregar módulos:', e)
                });
              },
              error: (e) => console.error('Erro ao carregar alto-falantes:', e)
            });
          },
          error: (e) => console.error('Erro ao carregar subwoofers:', e)
        });
      },
      error: (e) => console.error('Erro ao buscar configuração:', e)
    });
  }

  toggleRow(item: any) {
    this.expandedRows[item.id] = !this.expandedRows[item.id];
  }

  confirmEdit(project: Project) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja editar o projeto "${project.name}"?`,
      header: 'Confirmar Edição',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      accept: () => {
        this.messageService.add({ severity: 'success', summary: 'Confirmado', detail: `Projeto "${project.name}" editado com sucesso!` });
        this.editProject(project);
      },
      reject: (type: ConfirmEventType) => {
        if (type === ConfirmEventType.REJECT || type === ConfirmEventType.CANCEL) {
          this.messageService.add({ severity: 'warn', summary: 'Cancelado', detail: 'Edição cancelada.' });
        }
      }
    });
  }

  editProject(project: Project) {
    console.log('Lógica de edição para:', project.name);
  }

  confirmDelete(project: Project) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja excluir o projeto "${project.name}"? Esta ação não pode ser desfeita.`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-info-circle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.messageService.add({ severity: 'success', summary: 'Deletado', detail: `Projeto "${project.name}" excluído com sucesso!` });
        this.deleteProject(project);
      },
      reject: (type: ConfirmEventType) => {
        if (type === ConfirmEventType.REJECT || type === ConfirmEventType.CANCEL) {
          this.messageService.add({ severity: 'warn', summary: 'Cancelado', detail: 'Exclusão cancelada.' });
        }
      }
    });
  }

  deleteProject(project: Project) {
    this.projetos.set(this.projetos().filter(p => p.id !== project.id));
  }
}
