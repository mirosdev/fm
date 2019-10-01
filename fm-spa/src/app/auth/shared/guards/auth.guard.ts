import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {MyAuthService, User} from '../services/auth/my-auth.service';
import {map} from 'rxjs/operators';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private router: Router,
              private authService: MyAuthService) {}

  canActivate() {
    return this.authService.authState
      .pipe(
        map((user: User) => {
          if (!user) {
            this.router.navigate(['/auth/login']);
          }
          return user.authenticated;
        })
      );
  }
}

