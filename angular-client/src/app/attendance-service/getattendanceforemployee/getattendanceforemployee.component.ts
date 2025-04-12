import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service'; 

@Component({
  selector: 'app-getattendanceforemployee',
  templateUrl: './getattendanceforemployee.component.html',
  styleUrls: ['./getattendanceforemployee.component.css'],
  standalone: false
})
export class GetattendanceforemployeeComponent implements OnInit{
  todayAttendanceRecords:any = [];
  errorMessage: string = 'ops';
  managerId: number | null = null;
  userRole: string = "";
  employeeAttendanceRecords:any = [];
  viewSelectedEmployeeId!: number;
  
  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit(): void {
    this.managerId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || '';
    // this.loadTodayAttendance();
  }

  loadEmployeeAttendance(): void {
    if (this.viewSelectedEmployeeId) {
      this.attendanceService.getEmployeeAttendance(this.viewSelectedEmployeeId, 'MANAGER', this.managerId).subscribe({
        next: (data) => {
          console.log(data);
          
          this.employeeAttendanceRecords = Object.values(data)[1];
          console.log(this.todayAttendanceRecords);
          
        },
        error: (error) => {
          // this.errorMessage = 'Failed to load employee attendance  : ' + (error.error || error.message);
          console.log('Error Message:', this.errorMessage);
          this.employeeAttendanceRecords = [];
        }
      });
    } else {
      this.errorMessage = 'Please enter an Employee ID to view their attendance.';
      console.log('Error Message:', this.errorMessage);
    }
  }
}
