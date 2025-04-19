import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteAllReportsByEmployeeIdComponent } from './delete-all-reports-by-employee-id.component';

describe('DeleteAllReportsByEmployeeIdComponent', () => {
  let component: DeleteAllReportsByEmployeeIdComponent;
  let fixture: ComponentFixture<DeleteAllReportsByEmployeeIdComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeleteAllReportsByEmployeeIdComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteAllReportsByEmployeeIdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
