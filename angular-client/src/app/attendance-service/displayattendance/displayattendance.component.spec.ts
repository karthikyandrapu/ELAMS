import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayattendanceComponent } from './displayattendance.component';

describe('DisplayattendanceComponent', () => {
  let component: DisplayattendanceComponent;
  let fixture: ComponentFixture<DisplayattendanceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DisplayattendanceComponent]
    });
    fixture = TestBed.createComponent(DisplayattendanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
