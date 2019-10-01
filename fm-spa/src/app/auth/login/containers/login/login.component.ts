import {Component} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {MyAuthService} from '../../../shared/services/auth/my-auth.service';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {SocialUser, AuthService, GoogleLoginProvider} from 'ng4-social-login';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  error: string;
  constructor(private authService: MyAuthService,
              private router: Router,
              private socialAuthService: AuthService) {}

  loginUser(event: FormGroup) {
    const { email, password } = event.value;
    this.authService.loginUser(email, password)
      .subscribe(() => {
        this.router.navigate(['/']);
      }, error => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          this.error = error.error.message;
        }
      });
  }

  googleLogin() {
    this.socialAuthService.signIn(GoogleLoginProvider.PROVIDER_ID)
      .then((userData: SocialUser) => {
        this.authService.googleLogin(userData.token)
          .subscribe(() => {
            this.router.navigate(['/']);
          }, (error: HttpErrorResponse) => {
            if (error.status === 401 && error.error) {
              this.error = error.error;
            }
          });
    });
  }
}
