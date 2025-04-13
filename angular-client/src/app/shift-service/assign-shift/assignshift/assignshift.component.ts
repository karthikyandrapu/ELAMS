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
  styleUrls: ['./assignshift.component.css']
})
export class AssignshiftComponent implements OnInit {
  assignShiftForm!: FormGroup;
  shift: Shift = new Shift();
  managerId!: number; // Dynamically fetched managerId
  employees: number[] = []; // Array to hold the list of employee IDs
  shiftTimeOptions: { display: string; value: string }[] = [
    { display: '9:00 AM - 6:00 PM', value: '09:00:00' },
    { display: '6:00 PM - 3:00 AM', value: '18:00:00' },
    { display: '3:00 AM - 12:00 PM', value: '03:00:00' }
  ];
  errorMessage: string = ''; // To store error messages
  successMessage: string = ''; // To store success messages

  constructor(
    private formBuilder: FormBuilder,
    private shiftService: ShiftserviceService,
    private empService: EmployeeserviceService,
    private authService: AuthenticationService
  ) {}

  ngOnInit() {
    // Fetch managerId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10); // Convert empId to number
    } else {
      console.error('Manager ID not found in session storage.');
    }

    // Fetch the list of employees
    this.fetchEmployeeIdsByManagerId();

    // Initialize the form
    this.assignShiftForm = this.formBuilder.group({
      employeeId: ['', [Validators.required]], // Dropdown for Employee ID
      shiftDate: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
      shiftTime: ['', [Validators.required]] // Dropdown for Shift Time
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
        this.errorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'Failed to load employees. Please try again.';
        this.successMessage = ''; // Clear any potential success message
      }
    });
  }

  assign() {
    this.shift.employeeId = this.assignShiftForm.value.employeeId; // Get selected employee ID
    this.shift.shiftDate = this.assignShiftForm.value.shiftDate;
    this.shift.shiftTime = this.assignShiftForm.value.shiftTime;

    // Call the service to assign the shift
    this.shiftService.assignShift(this.shift, this.managerId).subscribe({
      next: (response) => {
        console.log('Shift assigned successfully:', response);
        this.errorMessage = ''; // Clear any previous error message
        this.successMessage = 'Shift assigned successfully!'; // Set success message
        this.assignShiftForm.reset(); // Reset the form
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error assigning shift:', error);
        this.errorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'An unexpected error occurred while assigning the shift.';
        this.successMessage = ''; // Clear any potential success message
      }
    });
  }
}