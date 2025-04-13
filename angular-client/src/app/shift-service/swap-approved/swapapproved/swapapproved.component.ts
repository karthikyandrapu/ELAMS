import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-swapapproved',
  standalone: false,
  templateUrl: './swapapproved.component.html',
  styleUrls: ['./swapapproved.component.css'],
})
export class SwapapprovedComponent implements OnInit {
  approvedSwaps: Shift[] = [];
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
      this.loadApprovedSwaps();
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }
  }

  loadApprovedSwaps(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.shiftService.viewEmployeeApprovedSwapRequests(this.employeeId).subscribe({
      next: (data) => {
        this.approvedSwaps = data;
        if (this.approvedSwaps.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error) => {
        console.error('Error fetching approved swap requests:', error);
        this.errorMessage = 'Failed to load approved swap requests.';
        this.noRecordFound = true;
      },
    });
  }
}