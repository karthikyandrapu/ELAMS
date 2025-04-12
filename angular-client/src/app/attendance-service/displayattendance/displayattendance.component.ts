import { Component } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';
import { Attendance } from 'src/app/model/attendance-model/Attendance';
@Component({
  selector: 'app-displayattendance',
  standalone:false,
  templateUrl: './displayattendance.component.html',
  styleUrls: ['./displayattendance.component.css']
})
export class DisplayattendanceComponent {
  userId: number | null = null;
  userRole: string = "";
  attendanceRecordBody: any = [];
  attendanceRecords: Attendance[] = [];
  errorMessage: string = '';

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit(): void {
    this.userId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || ''; // Get role from sessionStorage
    this.loadOwnAttendance();
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
            this.errorMessage = 'Failed to load attendance records.';
            this.attendanceRecords = [];
          }
        });
    } else {
      this.errorMessage = 'Employee ID or role not found.';
    }
  }
}
