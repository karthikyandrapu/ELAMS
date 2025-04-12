import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewemployeeshiftsComponent } from './viewemployeeshifts.component';

describe('ViewemployeeshiftsComponent', () => {
  let component: ViewemployeeshiftsComponent;
  let fixture: ComponentFixture<ViewemployeeshiftsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewemployeeshiftsComponent]
    });
    fixture = TestBed.createComponent(ViewemployeeshiftsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
