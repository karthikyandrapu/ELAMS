// add-leave-balance.component.ts
import { Component, OnInit } from '@angular/core';
import { LeaveRequestService } from 'src/app/service/leave-balance/leave-balance-service.service';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service';

@Component({
  selector: 'app-add-leave-balance',
  templateUrl: './add-leave-balance.component.html',
  styleUrls: ['./add-leave-balance.component.css']
})
export class AddLeaveBalanceComponent implements OnInit {
  managerId: number | null = null;
  employeeIds: number[] = [];
  selectedEmployeeId: number = 0;
  leaveType: string = '';
  daysToAdd: number = 0;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private leaveRequestService: LeaveRequestService,
    private authService: AuthenticationService,
    private employeeService: EmployeeserviceService
  ) {}

  ngOnInit(): void {
    const empId = this.authService.getLoggedInEmpId();
    this.managerId = empId ? parseInt(empId, 10) : null;

    if (this.managerId) {
      this.employeeService.findAllEmployeeIdsByManagerId(this.managerId).subscribe(
        (ids) => {
          this.employeeIds = ids;
          if (this.employeeIds.length > 0) {
            this.selectedEmployeeId = this.employeeIds[0]; // Select the first employee by default
          }
        },
        (error) => {
          this.errorMessage = 'Failed to fetch employee IDs.';
          console.error('Error fetching employee IDs:', error);
        }
      );
    } else {
      this.errorMessage = 'Manager ID not found. Please log in again.';
      console.error('Manager ID not found in session storage.');
    }
  }

  addLeaveBalance() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.selectedEmployeeId || !this.leaveType || !this.daysToAdd) {
      this.errorMessage = 'Please fill in all fields.';
      return;
    }

    console.log('Adding leave balance for employee:', this.selectedEmployeeId);
    console.log('Leave type:', this.leaveType);

    this.leaveRequestService.getLeaveBalance(this.selectedEmployeeId, this.leaveType).subscribe(
      (currentBalance: number) => {
        console.log('Current balance:', currentBalance);
        const newBalance: number = currentBalance + this.daysToAdd;

        this.leaveRequestService.createLeaveBalance(this.selectedEmployeeId, this.leaveType, newBalance).subscribe(
          (updatedLeaveBalance: LeaveBalanceResponse) => {
            this.successMessage = `Leave balance updated successfully. New balance: ${updatedLeaveBalance.balance}`;
            this.daysToAdd = 0; // Reset the daysToAdd field
          },
          (error: any) => {
            this.errorMessage = 'Failed to update leave balance.';
            console.error('Update error:', error);
          }
        );
      },
      (error: any) => {
        this.errorMessage = 'Failed to retrieve current leave balance.';
        console.error('Get balance error:', error);
      }
    );
  }

}

interface LeaveBalanceResponse {
  balance: number;
}