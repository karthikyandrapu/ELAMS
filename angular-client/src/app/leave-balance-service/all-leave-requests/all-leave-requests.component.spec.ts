import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllLeaveRequestsComponent } from './all-leave-requests.component';

describe('GetAllLeaveRequestsComponent', () => {
  let component: AllLeaveRequestsComponent;
  let fixture: ComponentFixture<AllLeaveRequestsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AllLeaveRequestsComponent]
    });
    fixture = TestBed.createComponent(AllLeaveRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
