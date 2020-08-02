import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantAddFoodDialogComponent } from './restaurant-add-food-dialog.component';

describe('RestaurantAddFoodDialogComponent', () => {
  let component: RestaurantAddFoodDialogComponent;
  let fixture: ComponentFixture<RestaurantAddFoodDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantAddFoodDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantAddFoodDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
