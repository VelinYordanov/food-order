import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AddressCreateComponent } from './address-create.component';

describe('AddressCreateComponent', () => {
  let component: AddressCreateComponent;
  let fixture: ComponentFixture<AddressCreateComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AddressCreateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddressCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
