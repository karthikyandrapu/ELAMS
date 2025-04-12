import { Component, OnInit } from '@angular/core';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
@Component({
  selector: 'app-getattendanceforemployeeanddate',
  templateUrl: './getattendanceforemployeeanddate.component.html',
  styleUrls: ['./getattendanceforemployeeanddate.component.css'],
  standalone: false
})
export class GetattendanceforemployeeanddateComponent implements OnInit{

  managerId: number | null = null;
  userRole: string = "";
  selectedEmployeeId: number | null = null;
  selectedDate: string = '';
  dateAttendanceRecords: any = [];
  errorMessage: string = 'ops';
  
  
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

  loadAttendanceByDate(): void {
    if (this.selectedEmployeeId && this.selectedDate) {
      const formattedSelectedDate = `${this.selectedDate}T10:00:00`; // Append the desired time
  
      this.attendanceService.getEmployeeAttendanceByDate(
        this.selectedEmployeeId,
        formattedSelectedDate, // Pass the formatted date and time
        'MANAGER',
        this.managerId
      ).subscribe({
        next: (data) => {
          console.log(data);
          
          this.dateAttendanceRecords = Object.values(data)[1];
          console.log(this.dateAttendanceRecords);
          
        },
        error: (error) => {
          this.errorMessage = 'Failed to load attendance for the date: ' + (error.error || error.message);
          console.log('Error Message:', this.errorMessage);
        }
      });
    } else {
      this.errorMessage = 'Please enter an Employee ID and a Date.';
      console.log('Error Message:', this.errorMessage);
    }
  }
}
