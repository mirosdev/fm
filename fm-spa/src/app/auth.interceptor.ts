import {Injectable} from '@angular/core';
import {HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token: string = localStorage.getItem('auth');
    if (token) {
      req = req.clone({
        setHeaders: { Authorization: 'Bearer ' + token}
      });
    }
    return next.handle(req.clone());
  }
}
