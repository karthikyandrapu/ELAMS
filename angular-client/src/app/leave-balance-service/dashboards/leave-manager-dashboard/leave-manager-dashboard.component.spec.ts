import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaveManagerDashboardComponent } from './leave-manager-dashboard.component';

describe('LeaveManagerDashboardComponent', () => {
  let component: LeaveManagerDashboardComponent;
  let fixture: ComponentFixture<LeaveManagerDashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LeaveManagerDashboardComponent]
    });
    fixture = TestBed.createComponent(LeaveManagerDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
