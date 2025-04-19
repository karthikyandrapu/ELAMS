import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetLeaveRequestsManagerComponent } from './get-leave-requests-manager.component';

describe('GetLeaveRequestsManagerComponent', () => {
  let component: GetLeaveRequestsManagerComponent;
  let fixture: ComponentFixture<GetLeaveRequestsManagerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GetLeaveRequestsManagerComponent]
    });
    fixture = TestBed.createComponent(GetLeaveRequestsManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
