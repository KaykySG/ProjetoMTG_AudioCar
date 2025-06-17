import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

export interface ValidacaoCompatibilidade {
  mensagem: string;
  sugestao: string;
  idSugestao: string;
}

export interface RequisicaoCompatibilidade {
  nome: string;
  veiculo: string;
  relatorioPdf: string;
  usuarioId: string;
  altoFalanteIds: string[];
  subwooferIds: string[];
  moduloIds: string[];
  crossoverIds: string[];
}

@Injectable({
  providedIn: 'root'
})
export class CompatibilidadeService {
  private url = `${environment.api}/configuracoes/validar`;

  constructor(private http: HttpClient) {}

  validarConfiguracao(dados: RequisicaoCompatibilidade): Observable<ValidacaoCompatibilidade[]> {
    const headers = new HttpHeaders({
      Authorization: `Basic a2F5a3k6MTIzMzIx`, // substitua se necessário
      accept: 'application/json'
    });

    return this.http.post<ValidacaoCompatibilidade[]>(this.url, dados, { headers });
  }
}
