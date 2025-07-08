import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftmanagerhomeComponent } from './shiftmanagerhome.component';

describe('ShiftmanagerhomeComponent', () => {
  let component: ShiftmanagerhomeComponent;
  let fixture: ComponentFixture<ShiftmanagerhomeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShiftmanagerhomeComponent]
    });
    fixture = TestBed.createComponent(ShiftmanagerhomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
