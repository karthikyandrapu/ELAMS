import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftServiceManagerComponent } from './shift-service-manager.component';

describe('ShiftServiceManagerComponent', () => {
  let component: ShiftServiceManagerComponent;
  let fixture: ComponentFixture<ShiftServiceManagerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShiftServiceManagerComponent]
    });
    fixture = TestBed.createComponent(ShiftServiceManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
