import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RestaurantRegisterComponent } from './restaurant-register.component';

describe('RestaurantRegisterComponent', () => {
  let component: RestaurantRegisterComponent;
  let fixture: ComponentFixture<RestaurantRegisterComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantRegisterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
