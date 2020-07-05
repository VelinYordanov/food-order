import { Component, OnInit } from '@angular/core';
import { StorageService } from './shared/storage.service';
import { UserService } from './shared/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  constructor(
    private storageService: StorageService,
    private userService: UserService) { }

  ngOnInit(): void {
    const user = JSON.parse(this.storageService.getItem('user'));

    if(user) {
      this.userService.updateUser(user);
    }
  }
}
