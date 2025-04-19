import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetLeaveRequestByEmployeeidComponent } from './get-leave-request-by-employeeid.component';

describe('GetLeaveRequestByEmployeeidComponent', () => {
  let component: GetLeaveRequestByEmployeeidComponent;
  let fixture: ComponentFixture<GetLeaveRequestByEmployeeidComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GetLeaveRequestByEmployeeidComponent]
    });
    fixture = TestBed.createComponent(GetLeaveRequestByEmployeeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
