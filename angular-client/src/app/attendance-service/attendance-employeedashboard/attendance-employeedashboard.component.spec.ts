import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttendanceEmployeedashboardComponent } from './attendance-employeedashboard.component';

describe('AttendanceEmployeedashboardComponent', () => {
  let component: AttendanceEmployeedashboardComponent;
  let fixture: ComponentFixture<AttendanceEmployeedashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AttendanceEmployeedashboardComponent]
    });
    fixture = TestBed.createComponent(AttendanceEmployeedashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
