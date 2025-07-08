// leave-status.component.ts
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http'; // Import HttpClient
import { AuthenticationService } from 'src/app/service/auth/auth.service';

@Component({
  selector: 'app-leave-status',
  standalone:false,
  templateUrl: './leave-status.component.html',
  styleUrls: ['./leave-status.component.css']
})
export class LeaveStatusComponent implements OnInit {
  selectedLeaveRequestId: number | null = null; // Selected leave request ID from dropdown
  employeeId: string | null = null; // Employee ID from session storage
  leaveStatus: any = null; // Holds the API response for the selected leave request
  leaveRequests: any[] = []; // Array to hold the leave request objects of the employee
  errorMessage: string = '';

  constructor(
    private http: HttpClient, // Inject HttpClient
    private authService: AuthenticationService
  ) {}

  ngOnInit(): void {

  
    this.employeeId = this.authService.getLoggedInEmpId();
    if (!this.employeeId) {
      console.error('Employee ID not found in session storage.');
      alert('Employee ID not found. Please log in again.');
      return;
    }
    this.getLeaveRequestsByEmployeeId(); // Call the method to fetch leave requests

  
  }

  getLeaveRequestsByEmployeeId() {
    this.http
      .get<any[]>(`http://localhost:8052/leave-requests/employee?employeeId=${this.employeeId}`)
      .subscribe(
        (data) => {
          this.leaveRequests = data;
          console.log('Leave requests fetched successfully:', this.leaveRequests);
          this.errorMessage = '';
        },
        (error) => {
          this.leaveRequests = []; // Clear previous requests on error
          this.leaveStatus = null; // Clear previous status on error
          this.errorMessage = 'Employee ID is invalid or has no leave requests.';
          console.error('Error fetching leave requests:', error);
        }
      );
  }

  getStatus() {
    if (!this.selectedLeaveRequestId) {
      alert('Please select a Leave Request ID.');
      return;
    }
    console.log('Fetching status for Leave Request ID:', this.selectedLeaveRequestId);
    console.log('Available leave requests:', this.leaveRequests);

    if (this.leaveRequests.length === 0) {
      console.warn('Leave requests are not loaded yet.');
      alert('Leave requests are not loaded. Please fetch leave requests first.');
      return;
    }
    this.leaveStatus = this.leaveRequests.find(
      (request) => request.leaveId == this.selectedLeaveRequestId
    );

    if (!this.leaveStatus) {
      console.warn('Selected Leave Request ID not found in the loaded list.');
      this.leaveStatus = null;
    }
  }
}