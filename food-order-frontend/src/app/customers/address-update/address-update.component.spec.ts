import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AddressUpdateComponent } from './address-update.component';

describe('AddressUpdateComponent', () => {
  let component: AddressUpdateComponent;
  let fixture: ComponentFixture<AddressUpdateComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AddressUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddressUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
