import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FetchAttendanceTrendsComponent } from './fetch-attendance-trends.component';

describe('FetchAttendanceTrendsComponent', () => {
  let component: FetchAttendanceTrendsComponent;
  let fixture: ComponentFixture<FetchAttendanceTrendsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FetchAttendanceTrendsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FetchAttendanceTrendsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
