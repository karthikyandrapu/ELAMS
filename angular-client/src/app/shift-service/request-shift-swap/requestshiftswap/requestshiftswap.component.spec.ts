import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestshiftswapComponent } from './requestshiftswap.component';

describe('RequestshiftswapComponent', () => {
  let component: RequestshiftswapComponent;
  let fixture: ComponentFixture<RequestshiftswapComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RequestshiftswapComponent]
    });
    fixture = TestBed.createComponent(RequestshiftswapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
