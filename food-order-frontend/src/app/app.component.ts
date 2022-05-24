import { Component, OnInit } from '@angular/core';
import { StorageService } from './shared/services/storage.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Store } from '@ngrx/store';
import { updateUserAction } from './store/authentication/authentication.actions';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  constructor(
    private jwtService: JwtHelperService,
    private storageService: StorageService,
    private store: Store) { }

  ngOnInit(): void {
    const token = this.storageService.getItem('jwt-user');
    if (token && !this.jwtService.isTokenExpired(token)) {
      const user = this.jwtService.decodeToken(token);

      if (user) {
        this.store.dispatch(updateUserAction({ payload: user }));
        this.store.dispatch(updateUserAction({ payload: user }));
      }
    }
  }
}
