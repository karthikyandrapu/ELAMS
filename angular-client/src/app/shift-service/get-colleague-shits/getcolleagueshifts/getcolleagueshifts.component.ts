import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-getcolleagueshifts',
  standalone:false,
  templateUrl: './getcolleagueshifts.component.html',
  styleUrls: ['./getcolleagueshifts.component.css'],
})
export class GetcolleagueshiftsComponent implements OnInit {
  shifts: Shift[] = []; // Array to hold the list of shifts
  shiftDate: string = ''; // Date in YYYY-MM-DD format
  noRecordFound = false; // Flag to indicate if no records are found
  errorMessage: string = ''; // To store error messages
  submitted = false; // To track form submission
  private employeeId!: number; // Employee ID fetched from session storage (not shown in the form)

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService // Inject AuthenticationService
  ) {}

  ngOnInit(): void {
    // Fetch employeeId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.employeeId = parseInt(empId, 10); // Convert empId to number
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }
  }

  getColleagueShifts(): void {
    this.submitted = true;
    this.noRecordFound = false;
    this.errorMessage = '';

    // Validate the date format
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (!dateRegex.test(this.shiftDate)) {
      this.errorMessage = 'Invalid date format. Please use YYYY-MM-DD.';
      return;
    }

    // Call the service to fetch colleague shifts
    this.shiftService
      .getColleagueShifts(this.employeeId, this.shiftDate)
      .subscribe(
        (response: Shift[]) => {
          this.shifts = response;
          if (this.shifts.length === 0) {
            this.noRecordFound = true;
          }
        },
        (error) => {
          console.error('Error fetching colleague shifts:', error);
          this.errorMessage = 'An error occurred while fetching shifts.';
          this.noRecordFound = true;
        }
      );
  }
}
