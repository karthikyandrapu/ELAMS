import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwapWithAnotherEmployeeComponent } from './swap-with-another-employee.component';

describe('SwapWithAnotherEmployeeComponent', () => {
  let component: SwapWithAnotherEmployeeComponent;
  let fixture: ComponentFixture<SwapWithAnotherEmployeeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SwapWithAnotherEmployeeComponent]
    });
    fixture = TestBed.createComponent(SwapWithAnotherEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
