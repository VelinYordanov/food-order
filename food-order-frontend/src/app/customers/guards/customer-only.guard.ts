import { Injectable } from '@angular/core';
import { UrlTree, Router, CanLoad, Route, UrlSegment } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { loggedInUserSelector } from 'src/app/shared/store/authentication/authentication.selectors';

@Injectable({
  providedIn: 'root'
})
export class CustomerOnlyGuard implements CanLoad {
  constructor(
    private store: Store,
    private router: Router) { }

  canLoad(route: Route, segments: UrlSegment[]): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    return this.store.select(loggedInUserSelector)
      .pipe(
        first(),
        map(user => {
          if (!user) {
            return this.router.parseUrl('');
          }

          return user.authorities?.includes('ROLE_CUSTOMER');
        })
      )
  }
}
