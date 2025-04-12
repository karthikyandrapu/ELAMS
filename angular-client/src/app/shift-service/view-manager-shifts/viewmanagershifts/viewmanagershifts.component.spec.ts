import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewmanagershiftsComponent } from './viewmanagershifts.component';

describe('ViewmanagershiftsComponent', () => {
  let component: ViewmanagershiftsComponent;
  let fixture: ComponentFixture<ViewmanagershiftsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewmanagershiftsComponent]
    });
    fixture = TestBed.createComponent(ViewmanagershiftsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
