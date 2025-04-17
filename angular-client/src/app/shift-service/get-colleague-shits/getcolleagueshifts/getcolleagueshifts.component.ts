import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { ActivatedRoute } from '@angular/router'; // Import ActivatedRoute

@Component({
  selector: 'app-getcolleagueshifts',
  standalone: false,
  templateUrl: './getcolleagueshifts.component.html',
  styleUrls: ['./getcolleagueshifts.component.css'],
})
export class GetcolleagueshiftsComponent implements OnInit {
  shifts: Shift[] = []; // Array to hold the list of shifts
  shiftId: number | null = null; // To store the shift ID for context
  shiftDate: string = ''; // Date in YYYY-MM-DD format
  noRecordFound = false; // Flag to indicate if no records are found
  errorMessage: string = ''; // To store error messages
  successMessage: string = ''; // To store success messages for swap requests
  private employeeId!: number; // Employee ID fetched from session storage

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService,
    private route: ActivatedRoute // Inject ActivatedRoute
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

    // Retrieve query parameters
    this.route.queryParams.subscribe(params => {
      this.shiftId = params['shiftId'];
      this.shiftDate = params['shiftDate'] || '';
      // Fetch colleagues immediately when the component loads with shift details
      if (this.shiftDate) {
        this.getColleagueShifts();
      }
    });
  }

  getColleagueShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.successMessage = ''; // Clear any previous success message

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
          this.errorMessage = 'An error occurred while fetching colleagues for the selected shift date.';
          this.noRecordFound = true;
        }
      );
  }

  requestSwap(colleagueId: number): void {
    this.errorMessage = '';
    this.successMessage = ''; // Clear any previous success message
    if (this.shiftId) {
      this.shiftService.requestShiftSwap(this.employeeId, this.shiftId, colleagueId)
        .subscribe(
          (response) => {
            console.log('Swap request successful:', response);
            this.successMessage = 'Shift swap request sent successfully!';
            // Optionally, you might want to refresh the colleague shifts list here
            // this.getColleagueShifts();
          },
          (error) => {
            console.error('Error requesting swap:', error);
            this.errorMessage = 'Failed to request shift swap.';
          }
        );
    } else {
      this.errorMessage = 'Selected shift ID is missing. Please navigate from the upcoming shifts table.';
    }
  }
}