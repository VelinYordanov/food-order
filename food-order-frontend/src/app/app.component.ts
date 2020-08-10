import { Component, OnInit } from '@angular/core';
import { StorageService } from './shared/storage.service';
import { AuthenticationService } from './shared/authentication.service';
import { JwtHelperService } from '@auth0/angular-jwt';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  constructor(
    private jwtService: JwtHelperService,
    private storageService: StorageService,
    private userService: AuthenticationService) { }

  ngOnInit(): void {
    const token = this.storageService.getItem('jwt-user');
    if (token) {
      const user = this.jwtService.decodeToken(token);

      if (user) {
        this.userService.updateUser(user);
      }
    }
  }
}
