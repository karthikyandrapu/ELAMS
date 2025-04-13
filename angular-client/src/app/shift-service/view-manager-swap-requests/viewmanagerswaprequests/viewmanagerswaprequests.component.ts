import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-viewmanagerswaprequests',
  standalone:false,
  templateUrl: './viewmanagerswaprequests.component.html',
  styleUrls: ['./viewmanagerswaprequests.component.css']
})
export class ViewmanagerswaprequestsComponent implements OnInit {
  swapRequests: Shift[] = [];
  managerId!: number;
  noRecordFound = false;
  errorMessage: string = '';
  approveSuccessMessage: string = '';
  approveErrorMessage: string = '';
  rejectSuccessMessage: string = '';
  rejectErrorMessage: string = '';

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService
  ) {}

  ngOnInit(): void {
    // Fetch managerId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10);
      this.loadManagerSwapRequests();
    } else {
      console.error('Manager ID not found in session storage.');
      this.errorMessage = 'Manager ID not found. Please log in again.';
    }
  }

  loadManagerSwapRequests(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.approveSuccessMessage = '';
    this.approveErrorMessage = '';
    this.rejectSuccessMessage = '';
    this.rejectErrorMessage = '';

    this.shiftService.getManagerSwapRequests(this.managerId).subscribe({
      next: (requests: Shift[]) => {
        this.swapRequests = requests;
        if (this.swapRequests.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching manager swap requests:', error);
        this.errorMessage = 'Failed to load swap requests.';
        this.noRecordFound = true;
      }
    });
  }

  approveSwapRequest(shiftId: number, swapWithEmployeeId: number): void {
    this.approveSuccessMessage = '';
    this.approveErrorMessage = '';
    this.shiftService.approveShiftSwap(shiftId, this.managerId, swapWithEmployeeId)
      .subscribe({
        next: (response) => {
          console.log(`Swap request for shift ID ${shiftId} approved.`, response);
          this.approveSuccessMessage = `Swap request for shift ID ${shiftId} approved.`;
          // Optionally reload the swap requests to update the list
          this.loadManagerSwapRequests();
        },
        error: (error: HttpErrorResponse) => {
          console.error(`Error approving swap request for shift ID ${shiftId}:`, error);
          this.approveErrorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'Failed to approve swap request.';
        }
      });
  }

  rejectSwapRequest(shiftId: number): void {
    this.rejectSuccessMessage = '';
    this.rejectErrorMessage = '';
    this.shiftService.rejectShiftSwap(shiftId, this.managerId)
      .subscribe({
        next: (response) => {
          console.log(`Swap request for shift ID ${shiftId} rejected.`, response);
          this.rejectSuccessMessage = `Swap request for shift ID ${shiftId} rejected.`;
          // Optionally reload the swap requests to update the list
          this.loadManagerSwapRequests();
        },
        error: (error: HttpErrorResponse) => {
          console.error(`Error rejecting swap request for shift ID ${shiftId}:`, error);
          this.rejectErrorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'Failed to reject swap request.';
        }
      });
  }
}