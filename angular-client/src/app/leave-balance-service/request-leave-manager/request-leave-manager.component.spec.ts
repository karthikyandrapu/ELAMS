import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestLeaveManagerComponent } from './request-leave-manager.component';

describe('RequestLeaveManagerComponent', () => {
  let component: RequestLeaveManagerComponent;
  let fixture: ComponentFixture<RequestLeaveManagerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RequestLeaveManagerComponent]
    });
    fixture = TestBed.createComponent(RequestLeaveManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
