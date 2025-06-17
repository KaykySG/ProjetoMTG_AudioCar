import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

export interface BalancoAudioResponse {
  percentualGrave: number;
  percentualVoz: number;
  consumo: number;
}


import { RequisicaoCompatibilidade } from '../compatibilidade/compatibilidade.service';

@Injectable({
  providedIn: 'root'
})
export class BalancoAudioService {
  private url = `${environment.api}/configuracoes/balanco-audio`;

  constructor(private http: HttpClient) {}

  calcularBalanco(payload: RequisicaoCompatibilidade): Observable<BalancoAudioResponse> {
    const headers = new HttpHeaders({
      Authorization: `Basic a2F5a3k6MTIzMzIh`,
      accept: `application/json`,
    });

    return this.http.post<BalancoAudioResponse>(this.url, payload, { headers });
  }
}
