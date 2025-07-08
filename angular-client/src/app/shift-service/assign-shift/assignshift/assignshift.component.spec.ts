import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { AssignshiftComponent } from './assignshift.component';
import { HttpErrorResponse } from '@angular/common/http';
import { ShiftStatus } from 'src/app/model/shift-model/shiftStatus';

describe('AssignshiftComponent', () => {
  let component: AssignshiftComponent;
  let fixture: ComponentFixture<AssignshiftComponent>;
  let formBuilder: FormBuilder;
  let shiftService: jasmine.SpyObj<ShiftserviceService>;
  let empService: jasmine.SpyObj<EmployeeserviceService>;
  let authService: jasmine.SpyObj<AuthenticationService>;

  beforeEach(() => {
    // Create mock services
    shiftService = jasmine.createSpyObj('ShiftserviceService', ['assignShift']);
    empService = jasmine.createSpyObj('EmployeeserviceService', ['findAllEmployeeIdsByManagerId']);
    authService = jasmine.createSpyObj('AuthenticationService', ['getLoggedInEmpId']);

    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [AssignshiftComponent],
      providers: [
        FormBuilder,
        { provide: ShiftserviceService, useValue: shiftService },
        { provide: EmployeeserviceService, useValue: empService },
        { provide: AuthenticationService, useValue: authService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AssignshiftComponent);
    component = fixture.componentInstance;
    formBuilder = TestBed.inject(FormBuilder);
    fixture.detectChanges(); // Trigger ngOnInit
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should fetch managerId from AuthService and call fetchEmployeeIdsByManagerId', () => {
      authService.getLoggedInEmpId.and.returnValue('123');
      empService.findAllEmployeeIdsByManagerId.and.returnValue(of([1, 2, 3]));

      component.ngOnInit();

      expect(component.managerId).toBe(123);
      expect(empService.findAllEmployeeIdsByManagerId).toHaveBeenCalledWith(123);
      expect(component.assignShiftForm).toBeDefined();
      expect(component.assignShiftForm.controls['employeeId']).toBeDefined();
      expect(component.assignShiftForm.controls['shiftDate']).toBeDefined();
      expect(component.assignShiftForm.controls['shiftTime']).toBeDefined();
    });

    it('should log error if managerId is not found', () => {
      const consoleSpy = spyOn(console, 'error');
      authService.getLoggedInEmpId.and.returnValue(null);

      component.ngOnInit();

      expect(consoleSpy).toHaveBeenCalledWith('Manager ID not found in session storage.');
    });
  });

  describe('fetchEmployeeIdsByManagerId', () => {
    it('should populate employees array on successful API call', () => {
      const mockEmployeeIds = [4, 5, 6];
      empService.findAllEmployeeIdsByManagerId.and.returnValue(of(mockEmployeeIds));

      component.managerId = 123;
      component.fetchEmployeeIdsByManagerId();

      expect(component.employees).toEqual(mockEmployeeIds);
      expect(component.errorMessage).toBe('');
    });

    it('should set errorMessage on API error', () => {
      const errorResponse = new HttpErrorResponse({
        error: 'Failed to fetch employees',
        status: 404,
      });
      empService.findAllEmployeeIdsByManagerId.and.returnValue(throwError(() => errorResponse));

      component.managerId = 123;
      component.fetchEmployeeIdsByManagerId();

      expect(component.errorMessage).toBe('Failed to fetch employees');
      expect(component.successMessage).toBe('');
    });

    it('should handle non-string error response', () => {
      const errorResponse = new HttpErrorResponse({
        error: { message: 'Failed to fetch' },
        status: 500,
      });
      empService.findAllEmployeeIdsByManagerId.and.returnValue(throwError(() => errorResponse));

      component.managerId = 123;
      component.fetchEmployeeIdsByManagerId();

      expect(component.errorMessage).toBe(JSON.stringify({ message: 'Failed to fetch' }));
      expect(component.successMessage).toBe('');
    });
  });

  describe('assign', () => {
    beforeEach(() => {
      component.assignShiftForm = formBuilder.group({
        employeeId: [1, Validators.required],
        shiftDate: ['2025-04-26', [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
        shiftTime: ['09:00:00', Validators.required],
      });
      component.managerId = 123;
    });

    it('should call shiftService.assignShift with correct data on valid form submission', () => {
      const mockResponse: Shift = {
        shiftId: 0, // Add shiftId to match the Shift type
        employeeId: 1,
        shiftDate: '2025-04-26',
        shiftTime: '09:00:00',
        shiftStatus: {
          shiftStatusId: 0,
          shiftId: 0,
          status: '',
          requestedSwapEmployeeId: 0
        }
      };
      shiftService.assignShift.and.returnValue(of(mockResponse));

      component.assign();

      const expectedShift: Shift = {
        shiftId: 0, // Add shiftId to match the Shift type
        employeeId: 1,
        shiftDate: '2025-04-26',
        shiftTime: '09:00:00',
        shiftStatus: {
          shiftStatusId: 0,
          shiftId: 0,
          status: '',
          requestedSwapEmployeeId: 0
        }
      };
      expect(shiftService.assignShift).toHaveBeenCalledWith(expectedShift, 123);
      expect(component.errorMessage).toBe('');
      expect(component.successMessage).toBe('Shift assigned successfully!');
      expect(component.assignShiftForm.pristine).toBeTrue(); // Check if form is reset
    });

    it('should set errorMessage on API error', () => {
      const errorResponse = new HttpErrorResponse({
        error: 'Failed to assign shift',
        status: 400,
      });
      shiftService.assignShift.and.returnValue(throwError(() => errorResponse));

      component.assign();

      expect(component.errorMessage).toBe('Failed to assign shift');
      expect(component.successMessage).toBe('');
    });

    it('should handle non-string error response during assignment', () => {
      const errorResponse = new HttpErrorResponse({
        error: { message: 'Assignment failed' },
        status: 500,
      });
      shiftService.assignShift.and.returnValue(throwError(() => errorResponse));

      component.assign();

      expect(component.errorMessage).toBe(JSON.stringify({ message: 'Assignment failed' }));
      expect(component.successMessage).toBe('');
    });
  });

  describe('form validity', () => {
    beforeEach(() => {
      component.ngOnInit(); // Ensure form is initialized
    });

    it('should be invalid when the form is empty', () => {
      expect(component.assignShiftForm.invalid).toBeTruthy();
    });

    it('should be invalid if employeeId is empty', () => {
      component.assignShiftForm.controls['employeeId'].setValue('');
      expect(component.assignShiftForm.controls['employeeId'].invalid).toBeTruthy();
    });

    it('should be invalid if shiftDate is empty', () => {
      component.assignShiftForm.controls['shiftDate'].setValue('');
      expect(component.assignShiftForm.controls['shiftDate'].invalid).toBeTruthy();
    });

    it('should be invalid if shiftDate format is incorrect', () => {
      component.assignShiftForm.controls['shiftDate'].setValue('2025/04/26');
      expect(component.assignShiftForm.controls['shiftDate'].invalid).toBeTruthy();
    });

    it('should be invalid if shiftTime is empty', () => {
      component.assignShiftForm.controls['shiftTime'].setValue('');
      expect(component.assignShiftForm.controls['shiftTime'].invalid).toBeTruthy();
    });

    it('should be valid when all required fields are filled correctly', () => {
      component.assignShiftForm.controls['employeeId'].setValue(1);
      component.assignShiftForm.controls['shiftDate'].setValue('2025-04-26');
      component.assignShiftForm.controls['shiftTime'].setValue('09:00:00');
      expect(component.assignShiftForm.valid).toBeTruthy();
    });
  });
});