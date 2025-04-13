import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwaprequestedComponent } from './swaprequested.component';

describe('SwaprequestedComponent', () => {
  let component: SwaprequestedComponent;
  let fixture: ComponentFixture<SwaprequestedComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SwaprequestedComponent]
    });
    fixture = TestBed.createComponent(SwaprequestedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
