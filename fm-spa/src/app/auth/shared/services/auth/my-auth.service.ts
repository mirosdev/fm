import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {environment} from '../../../../../environments/environment';
import {UserModel} from '../../models/user.model';
import {Store} from '../../../../store';
import {JwtHelperService} from '@auth0/angular-jwt';
import {tap} from 'rxjs/operators';

export interface User {
  email: string;
  authenticated: boolean;
}

@Injectable()
export class MyAuthService {
  private jwtHelper = new JwtHelperService();
  private apiHostUrl = environment.apiHostUrl;
  private authResource = '/api/auth';
  constructor(private httpClient: HttpClient,
              private store: Store) {}

  createUser(email: string, password: string, confirmPassword: string) {
    const userCredentials = {
      email,
      password,
      confirmPassword
    };
    return this.httpClient.post<UserModel>(this.apiHostUrl + this.authResource + '/register', userCredentials);
  }

  loginUser(email: string, password: string) {
    const userCredentials = {
      email,
      password
    };
    return this.httpClient.post<{token: string}>(this.apiHostUrl + this.authResource + '/login', userCredentials)
      .pipe(
        tap(response => {
          this.setAuthStorage(response.token);
          this.setAuthState();
        }, () => {
          localStorage.removeItem('auth');
          this.setAuthState();
        })
      );
  }

  googleLogin(token: string) {
    const googleLoginRequest = {
      token
    };
    return this.httpClient.post<{token: string}>(this.apiHostUrl + this.authResource + '/google/login', googleLoginRequest)
      .pipe(
        tap(response => {
          this.setAuthStorage(response.token);
          this.setAuthState();
        }, () => {
          localStorage.removeItem('auth');
          this.setAuthState();
        })
      );
  }

  get authState() {
    return this.store.select<User>('user');
  }

  private setAuthStorage(token: string) {
    localStorage.removeItem('auth');
    localStorage.setItem('auth', token.startsWith('Bearer ') ? token.substring(7) : token);
  }

  setAuthState() {
    const token = localStorage.getItem('auth');
    if (token && !this.jwtHelper.isTokenExpired(token)) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      const user: User = {
        email : decodedToken.email,
        authenticated : true
      };
      this.store.set('user', user);
    } else {
      if (token) {
        localStorage.removeItem('auth');
      }
      this.store.set('user', null);
    }
  }

  logout() {
    localStorage.removeItem('auth');
    this.store.set('user', null);
  }
}
