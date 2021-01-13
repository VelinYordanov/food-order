import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { EMPTY, Subject } from 'rxjs';
import { catchError, exhaustMap, finalize, tap } from 'rxjs/operators';
import { CustomerService } from 'src/app/customers/services/customer.service';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { CustomerRegisterDto } from '../models/customer-register-dto';

@Component({
  selector: 'app-register-customer',
  templateUrl: './register-customer.component.html',
  styleUrls: ['./register-customer.component.scss']
})
export class RegisterCustomerComponent implements OnInit {
  registerForm: FormGroup;

  readonly minEmailLength = 5;
  readonly maxEmailLength = 100;
  readonly minPasswordLength = 5;
  readonly maxPasswordLength = 50;
  readonly minNameLength = 3;
  readonly maxNameLength = 100;

  private registerFormSubmitsSubject = new Subject<CustomerRegisterDto>();

  constructor(
    private formBuilder: FormBuilder,
    private customerSerice: CustomerService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private router: Router) { }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      email: [null, [Validators.required, Validators.email, Validators.minLength(this.minEmailLength), Validators.maxLength(this.maxEmailLength)]],
      password: [null, [Validators.required, Validators.minLength(this.minPasswordLength), Validators.maxLength(this.maxPasswordLength)]],
      name: [null, [Validators.required, Validators.minLength(this.minNameLength), Validators.maxLength(this.maxNameLength)]],
      phoneNumber: [null, [Validators.required, Validators.pattern(/^[0-9]+$/)]]
    });

    this.registerFormSubmitsSubject
      .pipe(
        tap({
          next: _ => this.registerForm.disable()
        }),
        exhaustMap(customer => this.customerSerice.registerCustomer(customer)
        .pipe(
          catchError(error => {
            this.alertService.displayMessage(error?.error?.description || 'An error occurred while registering the user. Try again laer.', 'error');
            return EMPTY;
          }),
          finalize(() => this.registerForm.enable())
        )),
      ).subscribe(result => {
          this.authenticationService.login(result.token);
            this.router.navigate(['customer','profile']);
      });
  }

  submit() {
    if (this.registerForm.valid) {
      this.registerFormSubmitsSubject.next(this.registerForm.value);
    }
  }
}
