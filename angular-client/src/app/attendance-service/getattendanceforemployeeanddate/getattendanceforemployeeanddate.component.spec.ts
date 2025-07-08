import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetattendanceforemployeeanddateComponent } from './getattendanceforemployeeanddate.component';

describe('GetattendanceforemployeeanddateComponent', () => {
  let component: GetattendanceforemployeeanddateComponent;
  let fixture: ComponentFixture<GetattendanceforemployeeanddateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GetattendanceforemployeeanddateComponent]
    });
    fixture = TestBed.createComponent(GetattendanceforemployeeanddateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
