import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class crossoversService {
  private url = `${environment.api}/crossovers`;

  constructor(private http: HttpClient) {}

  obterCrossovers(): Observable<any> {
    const username = 'kayky';
    const password = '123321';
    const basicAuth = btoa(`${username}:${password}`);

    const headers = new HttpHeaders({

      Authorization: `Basic a2F5a3k6MTIzMzIx`,
      accept:`application/json`,

    });

    return this.http.get(this.url, { headers });
  }
}
