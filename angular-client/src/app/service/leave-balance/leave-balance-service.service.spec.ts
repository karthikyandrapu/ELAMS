import { TestBed } from '@angular/core/testing';

import { LeaveBalanceServiceService } from './leave-balance-service.service';

describe('LeaveBalanceServiceService', () => {
  let service: LeaveBalanceServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LeaveBalanceServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
