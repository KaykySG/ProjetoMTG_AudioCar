import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [FormsModule,
    CommonModule,
    FormsModule,
    CardModule,
    InputTextModule,
    PasswordModule,
    ButtonModule],
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private router: Router) {}

  login() {
    console.log('Login attempt with:', this.username);
    // In a real app, this would call an authentication service
    // For now, just navigate to home
    this.router.navigate(['/home']);
  }
}
