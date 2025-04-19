import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RejectshiftswapComponent } from './rejectshiftswap.component';

describe('RejectshiftswapComponent', () => {
  let component: RejectshiftswapComponent;
  let fixture: ComponentFixture<RejectshiftswapComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RejectshiftswapComponent]
    });
    fixture = TestBed.createComponent(RejectshiftswapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
