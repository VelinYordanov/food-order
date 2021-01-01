import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { YearlyGraphComponent } from './yearly-graph.component';

describe('YearlyGraphComponent', () => {
  let component: YearlyGraphComponent;
  let fixture: ComponentFixture<YearlyGraphComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ YearlyGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(YearlyGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
