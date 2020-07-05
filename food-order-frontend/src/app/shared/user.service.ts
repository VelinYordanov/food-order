import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { User } from './models/user';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private user = new BehaviorSubject<User>(null);
  user$ = this.user.asObservable();

  constructor(private storageService: StorageService) { }

  updateUser(user: User) {
    this.storageService.setItem('user', user);
    this.user.next(user);
  }
}
