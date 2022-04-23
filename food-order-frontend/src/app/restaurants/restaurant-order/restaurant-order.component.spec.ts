import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RestaurantOrderComponent } from './restaurant-order.component';

describe('RestaurantOrderComponent', () => {
  let component: RestaurantOrderComponent;
  let fixture: ComponentFixture<RestaurantOrderComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantOrderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
