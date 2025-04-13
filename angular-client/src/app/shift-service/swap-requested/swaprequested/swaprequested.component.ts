import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-swaprequested',
  standalone: false,
  templateUrl: './swaprequested.component.html',
  styleUrls: ['./swaprequested.component.css'],
})
export class SwaprequestedComponent implements OnInit {
  requestedSwaps: Shift[] = [];
  employeeId!: number;
  errorMessage: string = '';
  noRecordFound = false;

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService
  ) {}

  ngOnInit(): void {
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.employeeId = parseInt(empId, 10);
      this.loadRequestedSwaps();
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }
  }

  loadRequestedSwaps(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.shiftService.viewEmployeeSwapRequests(this.employeeId).subscribe({
      next: (data) => {
        this.requestedSwaps = data;
        if (this.requestedSwaps.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error) => {
        console.error('Error fetching swap requests:', error);
        this.errorMessage = 'Failed to load swap requests.';
        this.noRecordFound = true;
      },
    });
  }
}