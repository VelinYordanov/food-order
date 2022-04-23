import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RestaurantDetailsComponent } from './restaurant-details.component';

describe('RestaurantDetailsComponent', () => {
  let component: RestaurantDetailsComponent;
  let fixture: ComponentFixture<RestaurantDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
