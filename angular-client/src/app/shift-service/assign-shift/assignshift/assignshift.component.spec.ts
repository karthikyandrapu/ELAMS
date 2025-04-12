import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignshiftComponent } from './assignshift.component';

describe('AssignshiftComponent', () => {
  let component: AssignshiftComponent;
  let fixture: ComponentFixture<AssignshiftComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AssignshiftComponent]
    });
    fixture = TestBed.createComponent(AssignshiftComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
