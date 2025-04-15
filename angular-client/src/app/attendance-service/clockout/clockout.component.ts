import { Component, OnInit } from '@angular/core';
import { Attendance } from 'src/app/model/attendance-model/Attendance';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
@Component({
  selector: 'app-clockout',
  templateUrl: './clockout.component.html',
  styleUrls: ['./clockout.component.css'],
  standalone: false
})
export class ClockoutComponent implements OnInit{
  userRole: string = "";
  userId: number | null = null; // Initialize as null
  attendanceRecords: Attendance[] = [];
  clockInMessage: string = '';
  clockOutMessage: string = '';
  errorMessage: string = '';
  attendanceRecordBody: any = [];

  showClockOutMessage: boolean = false;
  showErrorMessage: boolean = false;

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit(): void {
    this.userId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || '';
    this.loadOwnAttendance();
    // this.loadTodayAttendance();
  }

  clockOut(): void {
    if (this.userId) {
      this.attendanceService.clockOut(this.userId).subscribe({
        next: (message) => {
          this.clockOutMessage = JSON.parse(message).message;
          this.clockInMessage = '';
          this.errorMessage = '';
          this.showClockOutMessage = true;
          this.loadOwnAttendance(); // Refresh attendance records
        },
        error: (error) => {
          console.error('Clock out failed:', error);
          this.clockInMessage = '';
          this.clockOutMessage = '';
          this.showErrorMessage = true;
          this.errorMessage = 'Failed to clock out: ' + (error.error?.message || error.message || 'An unexpected error occurred.');
          // Handle specific clock-out errors if needed
        }
      });
    } else {
      this.errorMessage = 'Employee ID not found.';
    }
  }

  loadOwnAttendance(): void {
    if (this.userId && this.userRole) {
      this.attendanceService.getOwnAttendance(this.userId, this.userRole)
        .subscribe({
          next: (data) => {
            console.log('Attendance Data Received:', data); // **Check your browser's console**

              this.attendanceRecords = Object.values(data);
              this.attendanceRecordBody = this.attendanceRecords[1];
              console.log(this.attendanceRecordBody);
              
            console.log(this.attendanceRecords);
            
            this.errorMessage = '';
          },
          error: (error) => {
            console.error('Error loading attendance:', error);
            this.errorMessage = 'No Attendace record found';
            this.attendanceRecords = [];
          }
        });
    } else {
      this.errorMessage = 'Employee ID or role not found.';
    }
  }
}

