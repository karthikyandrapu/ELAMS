import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FetchReportsByEmployeeIdComponent } from './fetch-reports-by-employee-id.component';

describe('FetchReportsByEmployeeIdComponent', () => {
  let component: FetchReportsByEmployeeIdComponent;
  let fixture: ComponentFixture<FetchReportsByEmployeeIdComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FetchReportsByEmployeeIdComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FetchReportsByEmployeeIdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
