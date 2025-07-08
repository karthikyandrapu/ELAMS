import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { LeaveRequestService } from 'src/app/service/leave-balance/leave-balance-service.service'; // Assuming you have LeaveRequestService for leave balances
import { LeaveBalance } from 'src/app/model/leave-model/LeaveBalance'; // Assuming you have LeaveBalance model

@Component({
  selector: 'app-leave-home-manager',
  standalone: false,
  templateUrl: './leave-home-manager.component.html',
  styleUrls: ['./leave-home-manager.component.css']
})
export class LeaveHomeManagerComponent implements OnInit {
  employeeId: number | null = null;
  leaveRequests: any[] = [];
  errorMessage: string = '';
  days!: number;
  leaveBalances: LeaveBalance[] = []; // Add leaveBalances array

  constructor(private http: HttpClient, private authService: AuthenticationService, private leaveBalanceService: LeaveRequestService) { }

  ngOnInit(): void {
    const empId = this.authService.getLoggedInEmpId();
    this.employeeId = empId ? parseInt(empId, 10) : null;

    if (this.employeeId) {
      this.getLeaveRequestsByEmployeeId();
      this.getLeaveBalances(); // Fetch leave balances
    } else {
      this.errorMessage = 'Employee ID not found. Please log in again.';
      console.error('Employee ID not found in session storage.');
    }
  }

  getLeaveRequestsByEmployeeId() {
    if (this.employeeId !== null) {
      this.http
        .get<any[]>(`http://localhost:8052/leave-requests/employee?employeeId=${this.employeeId}`)
        .subscribe(
          (data) => {
            this.leaveRequests = data;
            this.errorMessage = '';
          },
          (error) => {
            this.errorMessage = 'Employee ID is invalid or has no leave requests.';
            this.leaveRequests = []; // Clear previous results on error
            console.error('Error fetching leave requests:', error);
          }
        );
    }
  }

  calculateDays(startDate: string, endDate: string): number {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const timeDiff = Math.abs(end.getTime() - start.getTime());
    this.days = Math.ceil(timeDiff / (1000 * 3600 * 24)) + 1; // +1 to include both start and end dates
    return this.days;
  }

  getLeaveBalances() {
    if (this.employeeId !== null) {
      this.leaveBalanceService.getLeaveBalancesOfEmployee(this.employeeId).subscribe(
        (data: LeaveBalance[]) => {
          this.leaveBalances = data;
        },
        (error: any) => {
          console.error('Error fetching leave balances:', error);
        }
      );
    }
  }
}