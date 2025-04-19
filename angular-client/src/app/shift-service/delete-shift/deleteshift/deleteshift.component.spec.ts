import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteshiftComponent } from './deleteshift.component';

describe('DeleteshiftComponent', () => {
  let component: DeleteshiftComponent;
  let fixture: ComponentFixture<DeleteshiftComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DeleteshiftComponent]
    });
    fixture = TestBed.createComponent(DeleteshiftComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
