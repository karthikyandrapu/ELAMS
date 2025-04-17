import { Component, OnInit } from '@angular/core';
import { Attendance } from 'src/app/model/attendance-model/Attendance';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';

@Component({
  selector: 'app-clockin',
  
  templateUrl: './clockin.component.html',
  styleUrls: ['./clockin.component.css'],
  standalone: false
})
export class ClockinComponent implements OnInit{

  userId: number | null = null;
  userRole: string = "";
  attendanceRecords: Attendance[] = [];
  clockInMessage: string = ''
  errorMessage: string = '';
  attendanceRecordBody: any = [];

  showClockInMessage: boolean = false;
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

  clockIn(): void {
    if (this.userId) {
      this.attendanceService.clockIn(this.userId).subscribe({
        next: (message) => {
          this.clockInMessage = JSON.parse(message).message;
          this.showClockInMessage = true
          this.errorMessage = '';
          this.loadOwnAttendance(); // Refresh attendance records
        },
        error: (error) => {
          console.error('Clock in failed:', error);
          this.clockInMessage = '';
          if (error.status === 409) {
            this.errorMessage = 'Already Clocked In';
            this.showErrorMessage =true;
          } else {
            this.errorMessage = 'Failed to clock in: ' + (error.error?.message || error.message || 'An unexpected error occurred.');
          }
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
            // this.errorMessage = 'Failed to load attendance records.';
            this.attendanceRecords = [];
          }
        });
    } else {
      this.errorMessage = 'Employee ID or role not found.';
    }
  }

}
