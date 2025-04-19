import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewyourattedancebydateComponent } from './viewyourattedancebydate.component';

describe('ViewyourattedancebydateComponent', () => {
  let component: ViewyourattedancebydateComponent;
  let fixture: ComponentFixture<ViewyourattedancebydateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewyourattedancebydateComponent]
    });
    fixture = TestBed.createComponent(ViewyourattedancebydateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
