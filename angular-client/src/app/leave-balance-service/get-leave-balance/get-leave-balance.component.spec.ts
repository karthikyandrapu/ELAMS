import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetLeaveBalanceComponent } from './get-leave-balance.component';

describe('GetLeaveBalanceComponent', () => {
  let component: GetLeaveBalanceComponent;
  let fixture: ComponentFixture<GetLeaveBalanceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GetLeaveBalanceComponent]
    });
    fixture = TestBed.createComponent(GetLeaveBalanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
