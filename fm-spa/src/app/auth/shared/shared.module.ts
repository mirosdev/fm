import {ModuleWithProviders, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReactiveFormsModule} from '@angular/forms';
import {AuthFormComponent} from './components/auth-form/auth-form.component';
import {MyAuthService} from './services/auth/my-auth.service';
import {HttpClientModule} from '@angular/common/http';
import {AuthGuard} from './guards/auth.guard';
import {AuthServiceConfig, GoogleLoginProvider, SocialLoginModule} from 'ng4-social-login';

const authServiceConfig = new AuthServiceConfig([
  {
    id: GoogleLoginProvider.PROVIDER_ID,
    provider: new GoogleLoginProvider('1080385479010-u51f147a5msiq2ib3hm15sgb2r1i27t9.apps.googleusercontent.com')
  }
], true);

export function provideConfig() {
  return authServiceConfig;
}

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    SocialLoginModule
  ],
  declarations: [
    AuthFormComponent
  ],
  exports: [
    AuthFormComponent
  ]
})
export class SharedModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: SharedModule,
      providers: [
        MyAuthService,
        AuthGuard,
        {provide: AuthServiceConfig, useFactory: provideConfig}
      ]
    };
  }
}
