import {Component} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {MyAuthService} from '../../../shared/services/auth/my-auth.service';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  error: string;
  constructor(private authService: MyAuthService,
              private router: Router) {}

  registerUser(event: FormGroup) {
    const { email, password, confirmPassword } = event.value;
    this.authService.createUser(email, password, confirmPassword)
      .subscribe(() => {
        this.error = null;
        this.authService.loginUser(email, password)
          .subscribe(() => {
            this.router.navigate(['/']);
          }, error => {
            if (error instanceof HttpErrorResponse && error.status === 401) {
              this.error = error.error.message;
            }
          });
      }, error => {
        // Checks if var error is of regular type & it's message isn't default message
        if (error instanceof HttpErrorResponse && error.error.message !== '400 BAD_REQUEST') {
          this.error = error.error.message;
        }
      });
  }
}
