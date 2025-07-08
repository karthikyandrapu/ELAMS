import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaveEmployeeDashboardComponent } from './leave-employee-dashboard.component';

describe('LeaveEmployeeDashboardComponent', () => {
  let component: LeaveEmployeeDashboardComponent;
  let fixture: ComponentFixture<LeaveEmployeeDashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LeaveEmployeeDashboardComponent]
    });
    fixture = TestBed.createComponent(LeaveEmployeeDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
