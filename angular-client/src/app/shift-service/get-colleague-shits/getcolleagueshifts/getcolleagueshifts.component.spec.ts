import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetcolleagueshiftsComponent } from './getcolleagueshifts.component';

describe('GetcolleagueshiftsComponent', () => {
  let component: GetcolleagueshiftsComponent;
  let fixture: ComponentFixture<GetcolleagueshiftsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GetcolleagueshiftsComponent]
    });
    fixture = TestBed.createComponent(GetcolleagueshiftsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
