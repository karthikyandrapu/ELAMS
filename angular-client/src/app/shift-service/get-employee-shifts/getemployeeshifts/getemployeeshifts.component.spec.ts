import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetemployeeshiftsComponent } from './getemployeeshifts.component';

describe('GetemployeeshiftsComponent', () => {
  let component: GetemployeeshiftsComponent;
  let fixture: ComponentFixture<GetemployeeshiftsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GetemployeeshiftsComponent]
    });
    fixture = TestBed.createComponent(GetemployeeshiftsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
