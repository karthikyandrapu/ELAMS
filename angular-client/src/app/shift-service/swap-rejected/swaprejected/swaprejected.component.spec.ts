import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwaprejectedComponent } from './swaprejected.component';

describe('SwaprejectedComponent', () => {
  let component: SwaprejectedComponent;
  let fixture: ComponentFixture<SwaprejectedComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SwaprejectedComponent]
    });
    fixture = TestBed.createComponent(SwaprejectedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
