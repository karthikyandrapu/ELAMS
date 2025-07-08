import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetattendancefortodayComponent } from './getattendancefortoday.component';

describe('GetattendancefortodayComponent', () => {
  let component: GetattendancefortodayComponent;
  let fixture: ComponentFixture<GetattendancefortodayComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GetattendancefortodayComponent]
    });
    fixture = TestBed.createComponent(GetattendancefortodayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
