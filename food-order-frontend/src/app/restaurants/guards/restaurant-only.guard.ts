import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthenticationService } from '../../shared/services/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class RestaurantOnlyGuard implements CanActivate {
  constructor(
    private authenticationService: AuthenticationService,
    private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.authenticationService.user$
    .pipe(
      map(user => {
        if(!user) {
          return this.router.parseUrl('');
        }

        return user.authorities?.includes('ROLE_RESTAURANT');
      })
    )
  }
}
