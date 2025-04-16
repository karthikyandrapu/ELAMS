import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core'; // Import OnInit
import { AuthenticationService } from 'src/app/service/auth/auth.service';

interface LeaveBalanceUpdateDTO {
  employeeId: number;
  leaveType: string;
  days: number;
}

@Component({
  selector: 'app-get-leave-request-by-managerid',
  standalone:false,
  templateUrl: './get-leave-request-by-managerid.component.html',
  styleUrls: ['./get-leave-request-by-managerid.component.css']
})
export class GetLeaveRequestByManageridComponent implements OnInit { // Implement OnInit
  managerId: string | null = null;
  leaveRequests: any[] = [];
  errorMessage: string = '';
  days!: number;

  constructor(
    private http: HttpClient, // Inject HttpClient
    private authService: AuthenticationService
  ) {}

  ngOnInit(): void {
    this.managerId = this.authService.getLoggedInEmpId();
    if (!this.managerId) {
      console.error('Manager ID not found in session storage.');
      alert('Manager ID not found. Please log in again.');
      return;
    }
    // this.getLeaveRequestsByManagerId(); // Fetch requests on initialization
  }

  getLeaveRequestsByManagerId() {
    this.http
      .get<any[]>(`http://localhost:8052/leave-requests/manager?managerId=${this.managerId}`)
      .subscribe(
        (data) => {
          this.leaveRequests = data;
          this.errorMessage = '';
        },
        (error) => {
          this.errorMessage = `No leave records found for Manager ID: ${this.managerId}`;
          this.leaveRequests = []; // Clear any previous data
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

  updateLeaveRequestStatus(request: any) {
    const leaveId = request.leaveId;
    const newStatus = request.status;
    const managerId = this.managerId;

    console.log(`Updating status for Leave ID: ${leaveId} to: ${newStatus} by Manager ID: ${managerId}`);

    this.http
      .put(
        `http://localhost:8052/leave-requests/${leaveId}/review/${managerId}?decision=${newStatus}`,
        {}
      )
      .subscribe(
        (response) => {
          console.log('Status updated successfully:', response);
          // Optionally, provide user feedback
          const index = this.leaveRequests.findIndex(req => req.leaveId === leaveId);
          if (index !== -1) {
            this.leaveRequests[index] = { ...this.leaveRequests[index], status: newStatus };
            if (newStatus === 'APPROVED') {
              this.updateLeaveBalance(request);
            }
          }
        },
        (error) => {
          console.error('Error updating status:', error);
          // Optionally, provide user feedback
        }
      );
  }

  updateLeaveBalance(request: any) {
    const leaveBalanceUpdateDTO: LeaveBalanceUpdateDTO = {
      employeeId: request.employeeId,
      leaveType: request.leaveType,
      days: this.calculateDays(request.startDate, request.endDate)
    };

    console.log('Updating leave balance with:', leaveBalanceUpdateDTO);

    this.http
      .put('http://localhost:8084/api/leave-balance/update', leaveBalanceUpdateDTO)
      .subscribe(
        (response) => {

          console.log('Leave balance updated successfully:', response);
          // Optionally, provide user feedback
        },
        (error) => {
          console.error('Error updating leave balance:', error);
          // Optionally, provide user feedback
        }
      );
  }
}