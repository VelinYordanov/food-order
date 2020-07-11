import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { User } from './models/user';
import { StorageService } from './storage.service';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private user = new BehaviorSubject<User>(null);
  user$ = this.user.asObservable();

  constructor(
    private storageService: StorageService,
    private jwtService: JwtHelperService) { }

  updateUser(user: User) {
    this.user.next(user);
  }

  logout() {
    this.storageService.removeItem('jwt-user');
    this.user.next(null);
  }

  login(token:string) {
    const user = this.jwtService.decodeToken(token);
    this.storageService.setItem('jwt-user', token);
    this.updateUser(user);
  }
}
