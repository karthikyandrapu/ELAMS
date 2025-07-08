import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service';

@Component({
  selector: 'app-countattendance',
  templateUrl: './countattendance.component.html',
  styleUrls: ['./countattendance.component.css'],
  standalone: false
})
export class CountattendanceComponent implements OnInit {
  managerId!: number;
  userRole: string = "";
  userId: string = '';
  countSelectedEmployeeId!: number;
  countResult: number | null = null;
  errorMessage: string = '';
  startDate: string = '';
  endDate: string = '';
  isCounting: boolean = false;
  employeeIds: number[] = [];

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService,
    private employeeService: EmployeeserviceService
  ) { }

  ngOnInit(): void {
    this.userId = this.authService.getLoggedInEmpId() || '';
    this.userRole = sessionStorage.getItem('role') || '';
    
    // If the user is an employee, set their ID and disable changing it
    if (this.userRole.toUpperCase() === 'EMPLOYEE') {
      this.countSelectedEmployeeId = parseInt(this.userId, 10);
    }
    
    // If the user is a manager, get their manager ID
    if (this.userRole.toUpperCase() === 'MANAGER') {
      this.managerId = parseInt(this.userId, 10);
    }

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

  countAttendance(): void {
    // For employees, always use their own ID
    const employeeIdToCount = this.userRole.toUpperCase() === 'EMPLOYEE' 
      ? parseInt(this.userId, 10) 
      : this.countSelectedEmployeeId;

    if (!employeeIdToCount) {
      this.errorMessage = 'Please select an employee';
      return;
    }

    if (!this.startDate || !this.endDate) {
      this.errorMessage = 'Please select both start and end dates';
      return;
    }

    if (new Date(this.startDate) > new Date(this.endDate)) {
      this.errorMessage = 'Start date cannot be after end date';
      return;
    }

    this.isCounting = true;
    this.countResult = null;
    this.errorMessage = '';

    const formattedStartDate = `${this.startDate}T00:00:00`;
    const formattedEndDate = `${this.endDate}T23:59:59`;

    this.attendanceService.countEmployeeAttendance(
      employeeIdToCount,
      formattedStartDate,
      formattedEndDate,
    ).subscribe({
      next: (count) => {
        this.countResult = count;
        this.isCounting = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to count attendance: ' + (error.error?.message || error.message || 'Unknown error');
        this.isCounting = false;
        console.error('Error counting attendance:', error);
      }
    });
  }
}