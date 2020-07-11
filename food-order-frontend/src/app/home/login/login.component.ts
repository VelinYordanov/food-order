import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Subject, of, noop } from 'rxjs';
import { LoginService } from '../services/login-service.service';
import { exhaustMap, catchError, tap } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  minUsernameLength = 5;
  maxUsernameLength = 50;
  minPasswordLength = 5;
  maxPasswordLength = 50;

  private loginFormSubmitsSubject = new Subject<any>();

  constructor(
    private formBuilder: FormBuilder,
    private loginService: LoginService,
    private authenticationService: AuthenticationService,
  ) { }

  ngOnInit(): void {
    this.loginFormSubmitsSubject
      .pipe(
        tap({
          complete: () => this.loginForm.disable()
        }),
        exhaustMap(
          data => data.isRestaurant ?
            this.loginService.loginRestaurant(data).pipe(catchError(error => of(error))) :
            this.loginService.loginCustomer(data).pipe(catchError(error => of(error)))
        ),
        tap({
          complete: () => this.loginForm.enable()
        }),
      ).subscribe(result => {
        if (!result.error) {
          this.authenticationService.login(result.token);
        }
      });

    this.loginForm = this.formBuilder.group({
      "username": [null, [Validators.required, Validators.minLength(this.minUsernameLength), Validators.maxLength(this.maxUsernameLength)]],
      "password": [null, [Validators.required, Validators.minLength(this.minPasswordLength), Validators.maxLength(this.maxPasswordLength)]],
      "isRestaurant": [false],
    })
  }

  submit() {
    if (this.loginForm.valid) {
      this.loginFormSubmitsSubject.next(this.loginForm.value);
    }
  }
}
