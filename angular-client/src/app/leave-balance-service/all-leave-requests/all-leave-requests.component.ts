import { Component, Inject, OnInit } from '@angular/core';
import { LeaveRequestService } from '../../service/leave-request-service/leave-request.service';
import { AuthenticationService } from 'src/app/service/auth/auth.service'; // Import your authentication service

@Component({
  selector: 'app-all-leave-requests',
  standalone: false,
  templateUrl: './all-leave-requests.component.html',
  styleUrls: ['./all-leave-requests.component.css']
})
export class AllLeaveRequestsComponent implements OnInit {
  leaveRequests: any[] = [];
  managerId: number | null = null; // Store manager ID

  constructor(
    @Inject(LeaveRequestService) private leaveService: LeaveRequestService,
    private authService: AuthenticationService // Inject authentication service
  ) {}

  ngOnInit() {
    this.getManagerIdAndFetchRequests();
  }

  getManagerIdAndFetchRequests() {
    const empId = this.authService.getLoggedInEmpId();
    this.managerId = empId ? parseInt(empId, 10) : null;

    if (this.managerId) {
      this.fetchAllLeaveRequests();
    } else {
      alert('Manager ID not found. Please log in again.');
    }
  }

  fetchAllLeaveRequests() {
    if (this.managerId !== null) {
      this.leaveService.AllLeaveRequests(this.managerId).subscribe({
        next: (response: any) => {
          console.log('API Success:', response);
          this.leaveRequests = response;
        },
        error: (err: any) => {
          console.error('API Error:', err);
          alert('Failed to fetch leave requests. Please try again later.');
        }
      });
    }
  }
}