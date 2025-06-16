import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {environment} from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class modulosService {
  private url = `${environment.api}/modulos`;

  constructor(private http: HttpClient) { }

  obtermodulos(modulos: any): Observable<any> {
    return this.http.post(this.url, modulos);
  }
}
