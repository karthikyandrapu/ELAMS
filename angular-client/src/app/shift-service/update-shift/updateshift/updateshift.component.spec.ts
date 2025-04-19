import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateshiftComponent } from './updateshift.component';

describe('UpdateshiftComponent', () => {
  let component: UpdateshiftComponent;
  let fixture: ComponentFixture<UpdateshiftComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateshiftComponent]
    });
    fixture = TestBed.createComponent(UpdateshiftComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
