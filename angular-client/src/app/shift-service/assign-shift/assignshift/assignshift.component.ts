import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-assignshift',
  standalone: false,
  templateUrl: './assignshift.component.html',
  styleUrls: ['./assignshift.component.css'],
})
export class AssignshiftComponent implements OnInit {
  assignShiftForm!: FormGroup;
  shift: Shift = new Shift();
  managerId!: number; 
  employees: number[] = [];
  shiftTimeOptions: { display: string; value: string }[] = [
    { display: '9:00 AM - 6:00 PM', value: '09:00:00' },
    { display: '6:00 PM - 3:00 AM', value: '18:00:00' },
    { display: '3:00 AM - 12:00 PM', value: '03:00:00' },
  ];
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private shiftService: ShiftserviceService,
    private empService: EmployeeserviceService,
    private authService: AuthenticationService
  ) {}

  ngOnInit() {
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10); 
    } else {
      console.error('Manager ID not found in session storage.');
    } 
    // Fetch the list of employees
    this.fetchEmployeeIdsByManagerId(); 

    this.assignShiftForm = this.formBuilder.group({
      employeeId: ['', [Validators.required]],
      shiftDate: ['',[Validators.required]],
      shiftTime: ['', [Validators.required]], 
    });
  }

  fetchEmployeeIdsByManagerId() {
    this.empService.findAllEmployeeIdsByManagerId(this.managerId).subscribe({
      next: (response: number[]) => {
        this.employees = response; // Populate the employees array with empId
        console.log('Employees fetched successfully:', this.employees);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching employees:', error);
        this.errorMessage = error.error
          ? typeof error.error === 'string'
            ? error.error
            : JSON.stringify(error.error)
          : 'Failed to load employees. Please try again.';
        this.successMessage = ''; 
      },
    });
  }

  assign() {
    this.shift.employeeId = this.assignShiftForm.value.employeeId; 
    this.shift.shiftDate = this.assignShiftForm.value.shiftDate;
    this.shift.shiftTime = this.assignShiftForm.value.shiftTime; 

    this.shiftService.assignShift(this.shift, this.managerId).subscribe({
      next: (response) => {
        console.log('Shift assigned successfully:', response);
        this.errorMessage = ''; 
        this.successMessage = 'Shift assigned successfully!'
        this.assignShiftForm.reset(); 
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error assigning shift:', error);
        this.successMessage = '';
        if (
          error.error &&
          typeof error.error === 'object' &&
          error.error.shiftDate
        ) {
          // Backend returned a specific validation error for shiftDate
          this.errorMessage = error.error.shiftDate;
        } else if (error.error && typeof error.error === 'string') {
          // Backend returned a string error message
          this.errorMessage = error.error;
        } else if (error.message) {
          // Angular error message
          this.errorMessage = error.message;
        } else {
          // Generic error message
          this.errorMessage =
            'An unexpected error occurred while assigning the shift.';
        }
      },
    });
  }
}
