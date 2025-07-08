import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeedashboardComponent } from './employeedashboard.component';

describe('EmployeedashboardComponent', () => {
  let component: EmployeedashboardComponent;
  let fixture: ComponentFixture<EmployeedashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EmployeedashboardComponent]
    });
    fixture = TestBed.createComponent(EmployeedashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
