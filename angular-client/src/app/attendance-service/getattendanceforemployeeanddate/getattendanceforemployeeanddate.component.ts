import { Component, OnInit } from '@angular/core';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service';

@Component({
  selector: 'app-getattendanceforemployeeanddate',
  templateUrl: './getattendanceforemployeeanddate.component.html',
  styleUrls: ['./getattendanceforemployeeanddate.component.css'],
  standalone: false
})
export class GetattendanceforemployeeanddateComponent implements OnInit {

  managerId!: number;
  userRole: string = "";
  selectedEmployeeId: number | null = null;
  selectedDate: string = '';
  dateAttendanceRecords: any = [];
  errorMessage: string = '';
  isFlipped: boolean = false;
  employeeIds: number[] = [];

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService,
    private employeeService: EmployeeserviceService
  ) { }

  ngOnInit(): void {
    this.managerId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || '';
    this.loadEmployeeIds();
  }

  loadEmployeeIds(): void {
    this.employeeService.findAllEmployeeIdsByManagerId(this.managerId).subscribe({
      next: (ids) => {
        console.log(ids);
        
        this.employeeIds = ids;
        console.log('Employee IDs loaded:', this.employeeIds);
      },
      error: (error) => {
        console.error('Error loading employee IDs:', error);
        this.errorMessage = 'Failed to load employee list. Please try again later.';
        this.employeeIds = [];
      }
    });
  }

  loadAttendanceAndFlip(): void {
    // Reset any previous error messages
    this.errorMessage = '';
    
    if (this.selectedEmployeeId && this.selectedDate) {
      this.loadAttendanceByDate();
    } else {
      this.errorMessage = 'Please enter an Employee ID and a Date.';
      console.log('Error Message:', this.errorMessage);
    }
  }

  loadAttendanceByDate(): void {
    if (this.selectedEmployeeId && this.selectedDate) {
      const formattedSelectedDate = `${this.selectedDate}T10:00:00`; // Append the desired time
  
      this.attendanceService.getEmployeeAttendanceByDate(
        this.selectedEmployeeId,
        formattedSelectedDate,
        'MANAGER',
        this.managerId
      ).subscribe({
        next: (data) => {
          console.log(data);
          this.errorMessage = '';
          this.dateAttendanceRecords = Object.values(data)[1];
          console.log(this.dateAttendanceRecords);
          
          // Flip the card when data is loaded
          this.isFlipped = true;
        },
        error: (error) => {
          this.errorMessage = 'Failed to load attendance for the date: ' + (error.error || error.message);
          console.log('Error Message:', this.errorMessage);
          
          // Still flip the card to show the error message
          this.isFlipped = true;
        }
      });
    } else {
      this.errorMessage = 'Please enter an Employee ID and a Date.';
      console.log('Error Message:', this.errorMessage);
    }
  }

  closeCard(): void {
    this.isFlipped = false;
    // Optionally clear data when closing
    this.dateAttendanceRecords = [];
    this.errorMessage = '';
  }
}