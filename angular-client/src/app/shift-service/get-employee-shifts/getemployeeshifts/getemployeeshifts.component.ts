import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-getemployeeshifts',
  standalone:false,
  templateUrl: './getemployeeshifts.component.html',
  styleUrls: ['./getemployeeshifts.component.css'],
})
export class GetemployeeshiftsComponent implements OnInit {
  shifts: Shift[] = []; // Array to hold the list of shifts
  employeeId!: number; // Employee ID fetched from session storage
  noRecordFound = false; // Flag to indicate if no records are found
  errorMessage: string = ''; // To store error messages

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService // Inject AuthenticationService
  ) {}

  ngOnInit(): void {
    // Fetch employeeId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.employeeId = parseInt(empId, 10); // Convert empId to number
      this.fetchEmployeeShifts(); // Automatically fetch shifts on initialization
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }
  }

  fetchEmployeeShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';

    // Call the service to fetch employee shifts
    this.shiftService.getEmployeeShifts(this.employeeId).subscribe(
      (response: Shift[]) => {
        this.shifts = response;
        if (this.shifts.length === 0) {
          this.noRecordFound = true;
        }
      },
      (error) => {
        console.error('Error fetching employee shifts:', error);
        this.errorMessage = 'An error occurred while fetching shifts.';
        this.noRecordFound = true;
      }
    );
  }
}
