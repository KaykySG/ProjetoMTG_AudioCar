import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

export interface BalancoAudioResponse {
  percentualGrave: number;
  percentualVoz: number;
  consumo: number;
}

@Injectable({
  providedIn: 'root'
})
export class BalancoAudioService {
  private url = `${environment.api}/configuracoes/balanco`;

  constructor(private http: HttpClient) {}

  calcularBalanco(payload: any): Observable<BalancoAudioResponse> {
    const headers = new HttpHeaders({
      Authorization: `Basic a2F5a3k6MTIzMzIx`,
      accept: `application/json`,
    });

    return this.http.post<BalancoAudioResponse>(this.url, payload, { headers });
  }
}
