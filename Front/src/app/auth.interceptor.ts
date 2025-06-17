import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // Evita adicionar o token na requisição de login
  if (req.url.includes('/api/usuarios/login')) {
    return next(req);
  }

  // Adiciona o token nas demais requisições
  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }

  return next(req);
};
