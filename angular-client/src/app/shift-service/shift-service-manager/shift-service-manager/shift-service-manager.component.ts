import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-shift-service-manager',
  standalone: false,
  templateUrl: './shift-service-manager.component.html',
  styleUrls: ['./shift-service-manager.component.css'],
})
export class ShiftServiceManagerComponent implements OnInit {
  swapRequestCount: number = 0; 
  shift: Shift = new Shift();
  shifts: Shift[] = [];
  totalEmployeeCount: number = 0;
  totalMangerOwnCount: number = 0;
  totalManagerShiftCount: number = 0;
  swapRequests: Shift[] = [];
  managerId!: number;
  noRecordFound = false;
  errorMessage: string = '';
  employees: number[] = [];
  approveSuccessMessage: string = '';
  approveErrorMessage: string = '';
  rejectSuccessMessage: string = '';
  rejectErrorMessage: string = '';

  constructor(
    private shiftService: ShiftserviceService,
    private empService: EmployeeserviceService,
    private authService: AuthenticationService
  ) {}
  ngOnInit(): void {
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10);
      this.loadManagerSwapRequests();
    } else {
      console.error('Manager ID not found in session storage.');
      this.errorMessage = 'Manager ID not found. Please log in again.';
    }
    this.fetchEmployeeIdsByManagerId();
    this.fetchManagerOwnShifts();
    this.fetchManagerShifts();
  }

  loadManagerSwapRequests(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.approveSuccessMessage = '';
    this.approveErrorMessage = '';
    this.rejectSuccessMessage = '';
    this.rejectErrorMessage = '';

    this.shiftService.getManagerSwapRequests(this.managerId).subscribe({
      next: (requests: Shift[]) => {
        this.swapRequests = requests;
        this.swapRequestCount = requests.length; 
        if (this.swapRequests.length === 0) {
          this.noRecordFound = true;
          this.swapRequestCount = 0; 
        }
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching manager swap requests:', error);
        this.errorMessage = 'Failed to load swap requests.';
        this.noRecordFound = true;
      },
    });
  }
  fetchManagerOwnShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';

    
    this.shiftService.viewManagerOwnShifts(this.managerId).subscribe(
      (response: Shift[]) => {
        this.shifts = response;
        this.totalMangerOwnCount = this.shifts.length;
        if (this.shifts.length === 0) {
          this.noRecordFound = true;
          this.totalMangerOwnCount = 0;
        }
      },
      (error) => {
        console.error("Error fetching manager's own shifts:", error);
        this.errorMessage = 'An error occurred while fetching shifts.';
        this.noRecordFound = true;
      }
    );
  }

  fetchManagerShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';

    this.shiftService.viewManagerShifts(this.managerId).subscribe(
      (response: Shift[]) => {
        this.shifts = response;
        this.totalManagerShiftCount = this.shifts.length;
        if (this.shifts.length === 0) {
          this.noRecordFound = true;
          this.totalManagerShiftCount = 0;

        }
      },
      (error: HttpErrorResponse) => {
        console.error('Error fetching manager shifts:', error);
        this.errorMessage = 'An error occurred while fetching shifts.';
        this.noRecordFound = true;
      }
    );
  }

  fetchEmployeeIdsByManagerId() {
    this.empService.findAllEmployeeIdsByManagerId(this.managerId).subscribe({
      next: (response: number[]) => {
        this.employees = response; // Populate the employees array with empId
        this.totalEmployeeCount = this.employees.length;
        console.log('Employees fetched successfully:', this.employees);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching employees:', error);
        this.totalEmployeeCount = 0;
        this.errorMessage = error.error
          ? typeof error.error === 'string'
            ? error.error
            : JSON.stringify(error.error)
          : 'Failed to load employees. Please try again.';
      },
    });
  }
}
