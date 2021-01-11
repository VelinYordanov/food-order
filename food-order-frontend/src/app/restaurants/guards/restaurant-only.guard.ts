import { Injectable } from '@angular/core';
import { UrlTree, Router, CanLoad, Route, UrlSegment } from '@angular/router';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { AuthenticationService } from '../../shared/services/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class RestaurantOnlyGuard implements CanLoad {
  constructor(
    private authenticationService: AuthenticationService,
    private router: Router) {}

  canLoad(route: Route, segments: UrlSegment[]): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    return this.authenticationService.user$
    .pipe(
      first(),
      map(user => {
        if(!user) {
          return this.router.parseUrl('');
        }

        return user.authorities?.includes('ROLE_RESTAURANT');
      })
    )
  }
}
