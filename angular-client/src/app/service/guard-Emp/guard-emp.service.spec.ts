import { TestBed } from '@angular/core/testing';

import { GuardEmpService } from './guard-emp.service';

describe('GuardEmpService', () => {
  let service: GuardEmpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GuardEmpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
