import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CountattendanceComponent } from './countattendance.component';

describe('CountattendanceComponent', () => {
  let component: CountattendanceComponent;
  let fixture: ComponentFixture<CountattendanceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CountattendanceComponent]
    });
    fixture = TestBed.createComponent(CountattendanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
