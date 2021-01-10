import { TestBed } from '@angular/core/testing';

import { CustomerOnlyGuard } from './customer-only.guard';

describe('CustomerOnlyGuard', () => {
  let guard: CustomerOnlyGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(CustomerOnlyGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
