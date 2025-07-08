import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalculateReportComponent } from './calculate-report.component';

describe('CalculateReportComponent', () => {
  let component: CalculateReportComponent;
  let fixture: ComponentFixture<CalculateReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CalculateReportComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalculateReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
