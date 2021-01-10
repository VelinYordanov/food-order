import { TestBed } from '@angular/core/testing';

import { RestaurantOnlyGuard } from './restaurant-only.guard';

describe('RestaurantOnlyGuard', () => {
  let guard: RestaurantOnlyGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(RestaurantOnlyGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
