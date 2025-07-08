import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeavemanagerhomeComponent } from './leavemanagerhome.component';

describe('LeavemanagerhomeComponent', () => {
  let component: LeavemanagerhomeComponent;
  let fixture: ComponentFixture<LeavemanagerhomeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LeavemanagerhomeComponent]
    });
    fixture = TestBed.createComponent(LeavemanagerhomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
