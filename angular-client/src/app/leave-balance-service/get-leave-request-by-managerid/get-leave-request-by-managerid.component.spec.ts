import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetLeaveRequestByManageridComponent } from './get-leave-request-by-managerid.component';

describe('GetLeaveRequestByManageridComponent', () => {
  let component: GetLeaveRequestByManageridComponent;
  let fixture: ComponentFixture<GetLeaveRequestByManageridComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GetLeaveRequestByManageridComponent]
    });
    fixture = TestBed.createComponent(GetLeaveRequestByManageridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
