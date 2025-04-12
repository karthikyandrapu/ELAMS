import { TestBed } from '@angular/core/testing';

import { ShiftserviceService } from './shift.service';

describe('ShiftserviceService', () => {
  let service: ShiftserviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ShiftserviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
