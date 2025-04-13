import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { Router } from '@angular/router'; // Import the Router

@Component({
  selector: 'app-upcomingshifts',
  standalone: false,
  templateUrl: './upcomingshifts.component.html',
  styleUrls: ['./upcomingshifts.component.css']
})
export class UpcomingshiftsComponent implements OnInit {
  upcomingShifts: Shift[] = []; // Array to hold the list of upcoming shifts
  employeeId!: number; // Employee ID fetched from session storage
  noRecordFound = false; // Flag to indicate if no records are found
  errorMessage: string = ''; // To store error messages

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService,
    private router: Router // Inject the Router
  ) {}

  ngOnInit(): void {
    // Fetch employeeId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.employeeId = parseInt(empId, 10); // Convert empId to number
      this.fetchUpcomingEmployeeShifts(); // Automatically fetch upcoming shifts on initialization
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }
  }

  fetchUpcomingEmployeeShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';

    // Call the service to fetch upcoming employee shifts
    this.shiftService.getUpcomingEmployeeShifts(this.employeeId).subscribe(
      (response: Shift[]) => {
        this.upcomingShifts = response;
        if (this.upcomingShifts.length === 0) {
          this.noRecordFound = true;
        }
      },
      (error) => {
        console.error('Error fetching upcoming employee shifts:', error);
        this.errorMessage = 'An error occurred while fetching upcoming shifts.';
        this.noRecordFound = true;
      }
    );
  }

  navigateToColleagues(shift: Shift): void {
    // Store the selected shift (if needed in the colleagues component)
    // You can use a service to share data between components if it's more complex
    // For a simple navigation, you might just pass the shiftId as a query parameter
    this.router.navigate(['/getcolleagueshifts'], { queryParams: { shiftId: shift.shiftId, shiftDate: shift.shiftDate } });
  }
}