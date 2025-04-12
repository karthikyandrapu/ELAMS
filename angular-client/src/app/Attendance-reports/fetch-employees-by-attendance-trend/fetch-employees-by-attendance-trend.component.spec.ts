import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FetchEmployeesByAttendanceTrendComponent } from './fetch-employees-by-attendance-trend.component';

describe('FetchEmployeesByAttendanceTrendComponent', () => {
  let component: FetchEmployeesByAttendanceTrendComponent;
  let fixture: ComponentFixture<FetchEmployeesByAttendanceTrendComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FetchEmployeesByAttendanceTrendComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FetchEmployeesByAttendanceTrendComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
