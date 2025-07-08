// review-leave.component.ts
import { Component } from '@angular/core';
import { Injectable } from '@angular/core';
import { LeaveRequestService } from '../../service/leave-request-service/leave-request.service';

@Component({
  selector: 'app-review-leave',
  standalone:false,
  templateUrl: './review-leave.component.html',
  styleUrls: ['./review-leave.component.css']
})
export class ReviewLeaveComponent {
  leaveId: number = 0; // Leave request ID
  managerId: number = 0; // Manager ID
  decision: string = ''; // Manager's decision (approved/rejected)

  constructor(private leaveService: LeaveRequestService) {}

  reviewRequest() {
    try {
      if (!this.leaveId || !this.managerId || !this.decision) {
        alert('Please fill in all fields to proceed.');
        return;
      }

      this.leaveService.reviewLeaveRequest(this.leaveId, this.managerId, this.decision).subscribe({
        next: (response: any) => {
          alert('Leave request reviewed successfully!');
          console.log('Response:', response);
        },
        error: (err: any) => {
          alert('Failed to review leave request. Please try again.');
          console.error('Error:', err);
        }
      });
    } catch (error) {
      console.error('Unexpected error:', error);
      alert('An unexpected error occurred. Please try again later.');
    }
  }
}
