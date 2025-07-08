import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaveHomeManagerComponent } from './leave-home-manager.component';

describe('LeaveHomeManagerComponent', () => {
  let component: LeaveHomeManagerComponent;
  let fixture: ComponentFixture<LeaveHomeManagerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LeaveHomeManagerComponent]
    });
    fixture = TestBed.createComponent(LeaveHomeManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
