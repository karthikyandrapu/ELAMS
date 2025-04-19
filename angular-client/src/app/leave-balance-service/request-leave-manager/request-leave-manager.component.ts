import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LeaveRequestService } from '../../service/leave-request-service/leave-request.service';
import { AuthenticationService } from 'src/app/service/auth/auth.service';

interface LeaveBalanceCheckDTO {
  employeeId: number | null;
  leaveType: string;
  days: number;
}

interface EmployeeDetailsDTO {
  id: number | null;
  managerId: number | null;
  leaveType: string;
  startDate: string;
  endDate: string;
}
@Component({
  selector: 'app-request-leave-manager',
  templateUrl: './request-leave-manager.component.html',
  styleUrls: ['./request-leave-manager.component.css']
})
export class RequestLeaveManagerComponent implements OnInit {
  employee: any;
  errorMessage: string = '';
  leaveRequest: EmployeeDetailsDTO = {
    id: null,
    managerId: null,
    leaveType: '',
    startDate: '',
    endDate: ''
  };
  isLeaveBalanceSufficient: boolean | null = null;
  isSubmitting: boolean = false; // To prevent multiple submissions

  constructor(
    private leaveService: LeaveRequestService,
    private authService: AuthenticationService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    const empId = this.authService.getLoggedInEmpId();
    this.leaveRequest.id = empId ? parseInt(empId, 10) : null;
    if (!this.leaveRequest.id) {
      console.error('Employee ID not found in session storage.');
      alert('Employee ID not found. Please log in again.');
      return;
    }
    this.getManagerId();
  }

  getManagerId() {
    this.http
      .get<any>(`http://localhost:8086/employees/${this.leaveRequest.id}`)
      .subscribe(
        (data: any) => {
          console.log('Employee data:', data);
          
          this.employee = data;
          console.log('Employee data fetched successfully:', this.employee);
          this.errorMessage = '';
          this.leaveRequest.managerId = this.employee.managerId;
          console.log('Manager ID:', this.leaveRequest.managerId);
        },
        (error: any) => {
          this.employee = null;
          this.errorMessage = 'Error fetching employee details.';
          console.error('Error fetching employee details:', error);
        }
      );
  }

  calculateDays(startDate: string, endDate: string): number {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const timeDiff = Math.abs(end.getTime() - start.getTime());
    const days = Math.ceil(timeDiff / (1000 * 3600 * 24)) + 1;
    return days;
  }

  checkLeaveBalance(callback: (isSufficient: boolean) => void) {
    if (this.leaveRequest.id && this.leaveRequest.leaveType && this.leaveRequest.startDate && this.leaveRequest.endDate) {
      const days = this.calculateDays(this.leaveRequest.startDate, this.leaveRequest.endDate);
      const leaveBalanceCheckDTO: LeaveBalanceCheckDTO = {
        employeeId: this.leaveRequest.id,
        leaveType: this.leaveRequest.leaveType,
        days: days
      };
      this.isLeaveBalanceSufficient = null;

      this.http
        .post<boolean>('http://localhost:8084/api/leave-balance/sufficient', leaveBalanceCheckDTO)
        .subscribe(
          (data: boolean) => {
            console.log('Leave balance check response:', data);
            this.isLeaveBalanceSufficient = data;
            callback(data);
          },
          (error: any) => {
            console.error('Error checking leave balance:', error);
            this.isLeaveBalanceSufficient = false;
            alert('Error checking leave balance. Please try again.');
            callback(false);
          }
        );
    } else {
      this.isLeaveBalanceSufficient = null;
      callback(false); // Treat as insufficient if required fields are missing
    }
  }

  submitRequest() {
    if (this.isSubmitting) {
      return; // Prevent multiple submissions
    }

    if (!this.leaveRequest.managerId) {
      alert('Manager ID is not available. Please ensure you are logged in correctly.');
      return;
    }
    if (!this.leaveRequest.leaveType) {
      alert('Please select a Leave Type.');
      return;
    }
    if (!this.leaveRequest.startDate) {
      alert('Please select a Start Date.');
      return;
    }
    if (!this.leaveRequest.endDate) {
      alert('Please select an End Date.');
      return;
    }

    if (new Date(this.leaveRequest.startDate) > new Date(this.leaveRequest.endDate)) {
      alert('Start date cannot be greater than the end date.');
      return;
    }

    this.checkLeaveBalance((isSufficient) => {
      if (isSufficient) {
        this.isSubmitting = true;
        try {
          console.log('Submitting leave request:', this.leaveRequest);
          this.leaveService.requestLeave(this.leaveRequest).subscribe({
            next: (response: any) => {
              alert('Leave request submitted successfully!');
              console.log('Response:', response);
              this.leaveRequest = { id: null, managerId: null, leaveType: '', startDate: '', endDate: '' };
              this.isLeaveBalanceSufficient = null;
              this.isSubmitting = false;
              this.getManagerId();
            },
            error: (err: any) => {
              console.error('Error occurred:', err);
              alert(`Error: ${err.error?.message || err.message || 'An unexpected error occurred.'}`);
              this.isLeaveBalanceSufficient = null;
              this.isSubmitting = false;
            }
          });
        } catch (e) {
          console.error('Unexpected error:', e);
          alert('Unexpected error occurred. Check the console for details.');
          this.isLeaveBalanceSufficient = null;
          this.isSubmitting = false;
        }
      } else {
        alert('Insufficient leave balance for the selected leave type and duration.');
      }
    });
  }

  onLeaveTypeChange() {
    this.isLeaveBalanceSufficient = null;
  }

  onStartDateChange() {
    this.isLeaveBalanceSufficient = null;
  }

  onEndDateChange() {
    this.isLeaveBalanceSufficient = null;
  }
}