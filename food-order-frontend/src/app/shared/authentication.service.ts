import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { User } from './models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private user = new BehaviorSubject<User>(null);
  user$ = this.user.asObservable();

  updateUser(user: User) {
    this.user.next(user);
  }
}
