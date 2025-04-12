import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
 
@Component({
  selector: 'app-viewmanagershifts',
  standalone:false,
  templateUrl: './viewmanagershifts.component.html',
  styleUrls: ['./viewmanagershifts.component.css']
})
export class ViewmanagershiftsComponent implements OnInit {
  shifts: Shift[] = []; // Array to hold the list of shifts
  managerId!: number; // Manager ID fetched from session storage
  noRecordFound = false; // Flag to indicate if no records are found
  errorMessage: string = ''; // To store error messages
 
  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService // Inject AuthenticationService
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
 
    // Call the service to fetch manager's shifts
    this.shiftService.viewManagerShifts(this.managerId).subscribe(
      (response: Shift[]) => {
        this.shifts = response;
        if (this.shifts.length === 0) {
          this.noRecordFound = true;
        }
      },
      error => {
        console.error('Error fetching manager shifts:', error);
        this.errorMessage = 'An error occurred while fetching shifts.';
        this.noRecordFound = true;
      }
    );
  }
}
 