import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftServiceEmployeeComponent } from './shift-service-employee.component';

describe('ShiftServiceEmployeeComponent', () => {
  let component: ShiftServiceEmployeeComponent;
  let fixture: ComponentFixture<ShiftServiceEmployeeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShiftServiceEmployeeComponent]
    });
    fixture = TestBed.createComponent(ShiftServiceEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
