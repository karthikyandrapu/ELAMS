import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewmanagerswaprequestsComponent } from './viewmanagerswaprequests.component';

describe('ViewmanagerswaprequestsComponent', () => {
  let component: ViewmanagerswaprequestsComponent;
  let fixture: ComponentFixture<ViewmanagerswaprequestsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewmanagerswaprequestsComponent]
    });
    fixture = TestBed.createComponent(ViewmanagerswaprequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
