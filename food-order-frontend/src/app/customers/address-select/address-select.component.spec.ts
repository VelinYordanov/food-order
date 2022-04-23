import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AddressSelectComponent } from './address-select.component';

describe('AddressSelectComponent', () => {
  let component: AddressSelectComponent;
  let fixture: ComponentFixture<AddressSelectComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AddressSelectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddressSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
