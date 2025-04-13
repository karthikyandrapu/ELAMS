import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-viewmanagershifts',
  standalone: false,
  templateUrl: './viewmanagershifts.component.html',
  styleUrls: ['./viewmanagershifts.component.css'],
})
export class ViewmanagershiftsComponent implements OnInit {
  shifts: Shift[] = []; // Array to hold the list of shifts
  managerId!: number; // Manager ID fetched from session storage
  noRecordFound = false; // Flag to indicate if no records are found
  errorMessage: string = ''; // To store error messages
  deleteSuccessMessage: string = '';
  deleteErrorMessage: string = '';

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService, // Inject AuthenticationService
    private router: Router
  ) {}

  ngOnInit(): void {
    // Fetch managerId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10); // Convert empId to number
      this.fetchManagerShifts(); // Automatically fetch shifts on initialization
    } else {
      console.error('Manager ID not found in session storage.');
      this.errorMessage = 'Manager ID not found. Please log in again.';
    }
  }

  fetchManagerShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.deleteSuccessMessage = '';
    this.deleteErrorMessage = '';

    // Call the service to fetch manager's shifts
    this.shiftService.viewManagerShifts(this.managerId).subscribe(
      (response: Shift[]) => {
        this.shifts = response;
        if (this.shifts.length === 0) {
          this.noRecordFound = true;
        }
      },
      (error: HttpErrorResponse) => {
        console.error('Error fetching manager shifts:', error);
        this.errorMessage = 'An error occurred while fetching shifts.';
        this.noRecordFound = true;
      }
    );
  }

  deleteShift(shiftId: number): void {
    this.deleteSuccessMessage = '';
    this.deleteErrorMessage = '';
    this.shiftService.deleteShift(shiftId, this.managerId).subscribe({
      next: () => {
        console.log(`Shift with ID ${shiftId} deleted successfully.`);
        this.shifts = this.shifts.filter((shift) => shift.shiftId !== shiftId);
        this.deleteSuccessMessage = `Shift with ID ${shiftId} deleted successfully.`;
      },
      error: (error: HttpErrorResponse) => {
        console.error(`Error deleting shift with ID ${shiftId}:`, error);
        this.deleteErrorMessage = error.error
          ? typeof error.error === 'string'
            ? error.error
            : JSON.stringify(error.error)
          : 'Failed to delete the shift.';
      },
    });
  }

  updateShift(shift: Shift): void {
    this.router.navigate(['/update-shift'], {
      queryParams: {
        shiftId: shift.shiftId,
        shiftDate: shift.shiftDate,
        shiftTime: shift.shiftTime,
        employeeId: shift.employeeId,
      },
    });
  }
}
