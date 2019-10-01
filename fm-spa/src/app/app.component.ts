import {Component, OnInit} from '@angular/core';
import {MyAuthService, User} from './auth/shared/services/auth/my-auth.service';
import {Store} from './store';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  user$: Observable<User>;
  constructor(private authService: MyAuthService,
              private store: Store, private router: Router) {}

  ngOnInit(): void {
    this.authService.setAuthState();
    this.user$ = this.store.select<User>('user');
  }

  onLogout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
