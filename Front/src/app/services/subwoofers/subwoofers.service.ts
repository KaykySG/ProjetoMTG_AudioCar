import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class subwoofersService {
  private url = `${environment.api}/subwoofers`;

  constructor(private http: HttpClient) {}

  obterSubwoofers(): Observable<any> {
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
