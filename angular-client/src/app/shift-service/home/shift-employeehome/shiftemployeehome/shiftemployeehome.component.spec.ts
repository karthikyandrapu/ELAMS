import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftemployeehomeComponent } from './shiftemployeehome.component';

describe('ShiftemployeehomeComponent', () => {
  let component: ShiftemployeehomeComponent;
  let fixture: ComponentFixture<ShiftemployeehomeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShiftemployeehomeComponent]
    });
    fixture = TestBed.createComponent(ShiftemployeehomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
