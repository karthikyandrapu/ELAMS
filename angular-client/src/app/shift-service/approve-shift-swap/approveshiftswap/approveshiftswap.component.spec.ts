import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApproveshiftswapComponent } from './approveshiftswap.component';

describe('ApproveshiftswapComponent', () => {
  let component: ApproveshiftswapComponent;
  let fixture: ComponentFixture<ApproveshiftswapComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ApproveshiftswapComponent]
    });
    fixture = TestBed.createComponent(ApproveshiftswapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
