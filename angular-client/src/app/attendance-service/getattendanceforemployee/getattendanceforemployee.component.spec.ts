import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetattendanceforemployeeComponent } from './getattendanceforemployee.component';

describe('GetattendanceforemployeeComponent', () => {
  let component: GetattendanceforemployeeComponent;
  let fixture: ComponentFixture<GetattendanceforemployeeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GetattendanceforemployeeComponent]
    });
    fixture = TestBed.createComponent(GetattendanceforemployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
