import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttendanceManagerdashboardComponent } from './attendance-managerdashboard.component';

describe('AttendanceManagerdashboardComponent', () => {
  let component: AttendanceManagerdashboardComponent;
  let fixture: ComponentFixture<AttendanceManagerdashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AttendanceManagerdashboardComponent]
    });
    fixture = TestBed.createComponent(AttendanceManagerdashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
