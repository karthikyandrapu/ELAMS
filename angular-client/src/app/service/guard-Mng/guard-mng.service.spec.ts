import { TestBed } from '@angular/core/testing';

import { GuardMngService } from './guard-mng.service';

describe('GuardMngService', () => {
  let service: GuardMngService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GuardMngService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
