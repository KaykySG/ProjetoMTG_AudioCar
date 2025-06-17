import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class configuracoesService {
  private url = `${environment.api}/configuracoes`;
  private componentsBaseUrl = `${environment.api}`;

  constructor(private http: HttpClient) {}

  salvarConfiguracoes(payload: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Basic a2F5a3k6MTIzMzIx`
    });
    return this.http.post(this.url, payload, { headers });
  }

  getConfiguracoes(): Observable<any[]> {
    const headers = new HttpHeaders({
      Authorization: `Basic a2F5a3k6MTIzMzIx`
    });
    return this.http.get<any[]>(this.url, { headers });
  }

  getIdsPorConfiguracao(configId: string): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: `Basic a2F5a3k6MTIzMzIx`
    });
    return this.http.get<any>(`${this.url}/${configId}`, { headers });
  }

  getComponentById(type: string, id: string): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: `Basic a2F5a3k6MTIzMzIx`
    });
    return this.http.get<any>(`${this.componentsBaseUrl}/${type}/${id}`, { headers });
  }

  getComponentesByIds(type: string, ids: string[]): Observable<any[]> {
    return forkJoin(ids.map(id => this.getComponentById(type, id)));
  }
}
