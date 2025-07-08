import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FetchAllReportsComponent } from './fetch-all-reports.component';

describe('FetchAllReportsComponent', () => {
  let component: FetchAllReportsComponent;
  let fixture: ComponentFixture<FetchAllReportsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FetchAllReportsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FetchAllReportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
