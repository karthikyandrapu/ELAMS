import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewmanagerownshiftsComponent } from './viewmanagerownshifts.component';

describe('ViewmanagerownshiftsComponent', () => {
  let component: ViewmanagerownshiftsComponent;
  let fixture: ComponentFixture<ViewmanagerownshiftsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewmanagerownshiftsComponent]
    });
    fixture = TestBed.createComponent(ViewmanagerownshiftsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
