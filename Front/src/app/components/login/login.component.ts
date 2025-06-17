import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [
    FormsModule,
    CommonModule,
    CardModule,
    InputTextModule,
    PasswordModule,
    ButtonModule
  ],
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private router: Router, private http: HttpClient) {}

  login() {
    const loginData = {
      email: this.username,
      senha: this.password
    };

    this.http.post<any>('http://localhost:8080/api/usuarios/login', loginData)
      .subscribe({
        next: (response) => {
          console.log('Resposta:', response);

          // Teste de login sem token: apenas verifica se retornou e-mail
          if (response && response.email) {
            localStorage.setItem('usuarioLogado', JSON.stringify(response));
            this.router.navigate(['/home']);
          } else {
            alert('Login falhou: resposta inválida.');
          }
        },
        error: (err) => {
          console.error('Erro de login:', err);
          alert('Usuário ou senha inválidos!');
        }
      });
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}
