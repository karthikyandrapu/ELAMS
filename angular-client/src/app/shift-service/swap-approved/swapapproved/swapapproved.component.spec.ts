import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwapapprovedComponent } from './swapapproved.component';

describe('SwapapprovedComponent', () => {
  let component: SwapapprovedComponent;
  let fixture: ComponentFixture<SwapapprovedComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SwapapprovedComponent]
    });
    fixture = TestBed.createComponent(SwapapprovedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
