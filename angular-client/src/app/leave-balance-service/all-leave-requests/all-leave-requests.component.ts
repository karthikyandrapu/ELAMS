import { Component, Inject, OnInit } from '@angular/core';
import { LeaveRequestService } from '../../service/leave-request-service/leave-request.service';

@Component({
  selector: 'app-all-leave-requests',
  standalone:false,
  templateUrl: './all-leave-requests.component.html',
  styleUrls: ['./all-leave-requests.component.css']
})
export class AllLeaveRequestsComponent implements OnInit {
  leaveRequests: any[] = []; // Array to store all leave requests

  constructor(@Inject(LeaveRequestService) private leaveService: LeaveRequestService) {}

  // Lifecycle method to fetch leave requests when the component initializes
  ngOnInit() {
    this.fetchAllLeaveRequests();
  }

  // Method to fetch all leave requests from the backend
  fetchAllLeaveRequests() {
    this.leaveService.AllLeaveRequests().subscribe({
      next: (response: any) => {
        console.log('API Success:', response); // Success response
        this.leaveRequests = response;
      },
      error: (err: any) => {
        console.error('API Error:', err); // Error response
        alert('Failed to fetch leave requests. Please try again later.');
      }
    });
  }
  
}
