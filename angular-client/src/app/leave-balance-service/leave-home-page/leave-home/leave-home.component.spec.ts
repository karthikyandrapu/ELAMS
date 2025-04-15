import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaveHomeComponent } from './leave-home.component';

describe('LeaveHomeComponent', () => {
  let component: LeaveHomeComponent;
  let fixture: ComponentFixture<LeaveHomeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LeaveHomeComponent]
    });
    fixture = TestBed.createComponent(LeaveHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
