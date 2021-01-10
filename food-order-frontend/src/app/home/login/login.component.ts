import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Subject, EMPTY } from 'rxjs';
import { LoginService } from '../services/login-service.service';
import { exhaustMap, catchError, tap } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { Router } from '@angular/router';
import { AlertService } from 'src/app/shared/services/alert.service';

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
    private router: Router,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
  ) { }

  ngOnInit(): void {
    this.loginFormSubmitsSubject
      .pipe(
        tap({
          next: _ => this.loginForm.disable()
        }),
        exhaustMap(
          data => {
            const login$ = data.isRestaurant ?
            this.loginService.loginRestaurant(data) :
            this.loginService.loginCustomer(data);

            return login$.pipe(catchError(error => { 
              this.loginForm.enable();
              this.alertService.displayMessage(error?.error?.description || 'An error occurred while logging in. Try again later.', 'error');
              return EMPTY;
            }))
          }
        ),
        tap({
          next: _ => this.loginForm.enable()
        }),
      ).subscribe(result => {
          this.authenticationService.login(result.token);
          if (this.loginForm.get('isRestaurant').value) {
            this.router.navigate(['restaurant-profile']);
          } else {
            this.router.navigate(['customer-profile']);
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
