import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddLeaveBalanceComponent } from './add-leave-balance.component';

describe('AddLeaveBalanceComponent', () => {
  let component: AddLeaveBalanceComponent;
  let fixture: ComponentFixture<AddLeaveBalanceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddLeaveBalanceComponent]
    });
    fixture = TestBed.createComponent(AddLeaveBalanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
