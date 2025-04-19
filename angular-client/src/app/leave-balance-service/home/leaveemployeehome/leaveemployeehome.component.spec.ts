import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaveemployeehomeComponent } from './leaveemployeehome.component';

describe('LeaveemployeehomeComponent', () => {
  let component: LeaveemployeehomeComponent;
  let fixture: ComponentFixture<LeaveemployeehomeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LeaveemployeehomeComponent]
    });
    fixture = TestBed.createComponent(LeaveemployeehomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
