import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';

@Component({
  selector: 'app-countattendance',
  templateUrl: './countattendance.component.html',
  styleUrls: ['./countattendance.component.css'],
  standalone: false
})
export class CountattendanceComponent implements OnInit{
  managerId: number | null = null;
  userRole: string = "";
  countSelectedEmployeeId!: number;
  countResult: number | null = null;
  errorMessage: string = 'ops';
  startDate: string = '';
  endDate: string = '';

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit(): void {
    this.managerId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || '';
    // this.loadOwnAttendance();
    // this.loadTodayAttendance();
  }

  countAttendance(): void {
    if (this.countSelectedEmployeeId && this.startDate && this.endDate) {
      const formattedStartDate = `${this.startDate}T10:00:00`;
      const formattedEndDate = `${this.endDate}T10:00:00`;
  
      this.attendanceService.countEmployeeAttendance(
        this.countSelectedEmployeeId,
        formattedStartDate, // Use the formatted start date
        formattedEndDate,   // Use the formatted end date
        // ... any other parameters
      ).subscribe({
        next: (count) => {
          this.countResult = count;
        },
        error: (error) => {
          this.errorMessage = 'Failed to count attendance: ' + (error.error || error.message);
          console.log('Error Message:', this.errorMessage);
        }
      });
    } else {
      this.errorMessage = 'Please enter an Employee ID, Start Date, and End Date to count attendance.';
      console.log('Error Message:', this.errorMessage);
    }
  }
}
