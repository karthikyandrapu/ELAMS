import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Injectable } from '@angular/core';

@Component({
  selector: 'app-get-leave-request-by-employeeid',
  standalone:false,
  templateUrl: './get-leave-request-by-employeeid.component.html',
  styleUrls: ['./get-leave-request-by-employeeid.component.css']
})
export class GetLeaveRequestByEmployeeidComponent {
  employeeId: number = 0;
  leaveRequests: any[] = [];
  errorMessage: string = '';
  days!: number;
  

  constructor(private http: HttpClient) {}

  getLeaveRequestsByEmployeeId() {
    this.http
      .get<any[]>(`http://localhost:8052/leave-requests/employee?employeeId=${this.employeeId}`)
      .subscribe(
        (data) => {
          this.leaveRequests = data;
          this.errorMessage = '';
        },
        (error) => {
          this.errorMessage = 'Employee ID is invalid or has no leave requests.';
        }
      );
  }

  calculateDays(startDate: string, endDate: string): number {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const timeDiff = Math.abs(end.getTime() - start.getTime());
    this.days = Math.ceil(timeDiff / (1000 * 3600 * 24)) + 1; // +1 to include both start and end dates
    return this.days;
  }

}
