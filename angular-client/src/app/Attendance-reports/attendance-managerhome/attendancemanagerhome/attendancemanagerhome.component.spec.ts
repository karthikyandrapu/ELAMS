import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttendancemanagerhomeComponent } from './attendancemanagerhome.component';

describe('AttendancemanagerhomeComponent', () => {
  let component: AttendancemanagerhomeComponent;
  let fixture: ComponentFixture<AttendancemanagerhomeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AttendancemanagerhomeComponent]
    });
    fixture = TestBed.createComponent(AttendancemanagerhomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
