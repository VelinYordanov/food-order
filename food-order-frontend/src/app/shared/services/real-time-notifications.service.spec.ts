import { TestBed } from '@angular/core/testing';

import { RealTimeNotificationsService } from './real-time-notifications.service';

describe('RealTimeNotificationsService', () => {
  let service: RealTimeNotificationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RealTimeNotificationsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
