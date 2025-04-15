import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
@Component({
  selector: 'app-leave-home',
  standalone:false,
  templateUrl: './leave-home.component.html',
  styleUrls: ['./leave-home.component.css']
})
export class LeaveHomeComponent implements OnInit {
  employeeId: number | null = null;
    leaveRequests: any[] = [];
    errorMessage: string = '';
    days!: number;
  
    constructor(private http: HttpClient, private authService: AuthenticationService) {}
  
    ngOnInit(): void {
      const empId = this.authService.getLoggedInEmpId();
      this.employeeId = empId ? parseInt(empId, 10) : null;
  
      if (this.employeeId) {
        this.getLeaveRequestsByEmployeeId();
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
}
