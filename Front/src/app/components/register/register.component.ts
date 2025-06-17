import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    CardModule,
    InputTextModule,
    PasswordModule,
    ButtonModule
  ]
})
export class RegisterComponent {
  fullName: string = '';
  email: string = '';
  username: string = '';
  password: string = '';
  confirmPassword: string = '';

  constructor(private router: Router, private http: HttpClient) {}

  register() {
    // Validação de campos obrigatórios
    if (!this.fullName || !this.email || !this.username || !this.password || !this.confirmPassword) {
      alert('Preencha todos os campos antes de continuar.');
      return;
    }

    // Validação de senha
    if (this.password !== this.confirmPassword) {
      alert('As senhas não coincidem!');
      return;
    }

    const payload = {
      nome: this.fullName,
      email: this.email,
      senhaHash: this.password,
      autenticado: true
    };

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic a2F5a3k6MTIzMzIx'
    });

    this.http.post('http://localhost:8080/api/usuarios', payload, { headers })
      .subscribe({
        next: () => {
          alert('Cadastro realizado com sucesso!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Erro ao cadastrar:', err);
          alert('Erro ao cadastrar usuário. Verifique os dados e tente novamente.');
        }
      });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
