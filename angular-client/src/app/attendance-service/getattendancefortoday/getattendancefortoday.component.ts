import { Component, Input } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';

@Component({
  selector: 'app-getattendancefortoday',
  templateUrl: './getattendancefortoday.component.html',
  styleUrls: ['./getattendancefortoday.component.css'],
  standalone: false
  
})
export class GetattendancefortodayComponent {

  todayAttendanceRecords:any = [];
  errorMessage: string = 'ops';
  userRole: string = "";
  employeeId: number | null = null;
  // @Input({required: true}) inputId!: number;

  

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit(): void {
    this.employeeId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || '';
    // this.loadOwnAttendance();
    // this.loadTodayAttendance();
  }

  loadTodayAttendance(): void {
    this.attendanceService.getTodayAttendance('MANAGER', this.employeeId).subscribe({
      next: (data) => {
        // console.log(data);
        this.todayAttendanceRecords = Object.values(data)[1];
        // console.log(this.todayAttendanceRecords);
        
      },
      error: (error) => {
        this.errorMessage = 'Failed to load today\'s attendance: ' + (error.error || error.message);
        console.log('Error Message:', this.errorMessage);
      }
    });
  }
}
