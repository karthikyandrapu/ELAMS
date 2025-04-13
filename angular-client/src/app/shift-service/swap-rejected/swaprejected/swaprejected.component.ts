import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-swaprejected',
  standalone: false,
  templateUrl: './swaprejected.component.html',
  styleUrls: ['./swaprejected.component.css'],
})
export class SwaprejectedComponent implements OnInit {
  rejectedSwaps: Shift[] = [];
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
      this.loadRejectedSwaps();
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }
  }

  loadRejectedSwaps(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.shiftService.viewEmployeeRejectedSwapRequests(this.employeeId).subscribe({
      next: (data) => {
        this.rejectedSwaps = data;
        if (this.rejectedSwaps.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error) => {
        console.error('Error fetching rejected swap requests:', error);
        this.errorMessage = 'Failed to load rejected swap requests.';
        this.noRecordFound = true;
      },
    });
  }
}