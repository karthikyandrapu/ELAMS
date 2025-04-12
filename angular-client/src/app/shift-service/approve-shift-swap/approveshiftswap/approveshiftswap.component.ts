import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { Shift } from 'src/app/model/shift-model/shift';

@Component({
  selector: 'app-approveshiftswap',
  templateUrl: './approveshiftswap.component.html',
  standalone:false,
  styleUrls: ['./approveshiftswap.component.css'],
})
export class ApproveshiftswapComponent implements OnInit {
  shiftId!: number; // Shift ID entered by the user
  swapWithEmployeeId!: number; // Employee ID to swap with, entered by the user
  managerId!: number; // Manager ID fetched from session storage
  errorMessage: string = ''; // To store error messages

  constructor(
    private authService: AuthenticationService,
    private shiftService: ShiftserviceService
  ) {}

  ngOnInit(): void {
    // Fetch managerId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10); // Convert empId to number
    } else {
      console.error('Manager ID not found in session storage.');
      this.errorMessage = 'Manager ID not found. Please log in again.';
    }
  }

  approveSwap(): void {
    if (!this.shiftId || !this.swapWithEmployeeId) {
      this.errorMessage =
        'Both Shift ID and Swap With Employee ID are required.';
      return;
    }

    // Call the service to approve the shift swap
    this.shiftService
      .approveShiftSwap(this.shiftId, this.managerId, this.swapWithEmployeeId)
      .subscribe(
        (response: Shift) => {
          console.log('Shift swap approved successfully:', response);
          this.errorMessage = ''; // Clear any previous error message
          alert('Shift swap approved successfully!');
        },
        (error) => {
          console.error('Error approving shift swap:', error);
          this.errorMessage =
            'An error occurred while approving the shift swap.';
        }
      );
  }
}
