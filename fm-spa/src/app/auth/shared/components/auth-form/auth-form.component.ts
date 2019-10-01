import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-auth-form',
  styleUrls: ['./auth-form.component.scss'],
  templateUrl: './auth-form.component.html'
})
export class AuthFormComponent implements OnInit{

  @Input() container: string;
  @Output() submitted = new EventEmitter<FormGroup>();
  form: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.registerContainer ? this.fb.group({
      email: ['', Validators.email],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    }) : this.fb.group({
      email: ['', Validators.email],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.form.valid) {
      this.submitted.emit(this.form);
    }
  }

  get passwordInvalid() {
    const control = this.form.get('password');
    return control.hasError('required') && control.touched;
  }

  get emailFormat() {
    const control = this.form.get('email');
    return control.hasError('email') && control.touched;
  }

  get registerContainer() {
    return this.container === 'register';
  }
}
