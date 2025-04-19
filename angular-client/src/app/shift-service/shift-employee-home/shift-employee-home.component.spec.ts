import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftEmployeeHomeComponent } from './shift-employee-home.component';

describe('ShiftEmployeeHomeComponent', () => {
  let component: ShiftEmployeeHomeComponent;
  let fixture: ComponentFixture<ShiftEmployeeHomeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShiftEmployeeHomeComponent]
    });
    fixture = TestBed.createComponent(ShiftEmployeeHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
