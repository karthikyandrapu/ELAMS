import { Component, OnInit } from '@angular/core';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { Shift } from 'src/app/model/shift-model/shift';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service';
import { Router } from '@angular/router'; 

@Component({
  selector: 'app-getemplyeeshiftsbyid',
  standalone: false,
  templateUrl: './getemplyeeshiftsbyid.component.html',
  styleUrls: ['./getemplyeeshiftsbyid.component.css'],
})
export class GetemplyeeshiftsbyidComponent implements OnInit {
  managerId!: number;
  employeeShifts: Shift[] | null = null;
  pagedEmployeeShifts: Shift[] = [];
  errorMessage: string = '';
  viewEmployeeShiftsForm!: FormGroup;
  employeesUnderManager: number[] = [];
  selectedEmployeeId: number | null = null;
  showEmployeeShiftsTable: boolean = false;
  deleteSuccessMessage: string = '';
  deleteErrorMessage: string = '';

  // Pagination properties
  pageSize = 10;
  currentPage = 1;
  totalEmployeeShifts = 0;
  totalPages = 0;
  pages: number[] = [];
  maxPagesToShow = 5;

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService,
    private formBuilder: FormBuilder,
    private employeeService: EmployeeserviceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Retrieve the logged-in manager's ID
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10);
      this.loadEmployeesUnderManager();
      this.initializeForm();
    } else {
      console.error('Manager ID not found in session storage.');
      this.errorMessage = 'Could not retrieve manager information.';
    }
  }

  initializeForm(): void {
    this.viewEmployeeShiftsForm = this.formBuilder.group({
      employeeId: ['', Validators.required],
    });
  }

  loadEmployeesUnderManager(): void {
    this.employeeService.findAllEmployeeIdsByManagerId(this.managerId).subscribe({
      next: (employeeIds) => {
        this.employeesUnderManager = employeeIds;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading employees under manager:', error);
        this.errorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'Failed to load employees.';
      },
    });
  }

  viewShiftsForEmployee(): void {
    if (this.viewEmployeeShiftsForm.valid) {
      this.selectedEmployeeId = this.viewEmployeeShiftsForm.get('employeeId')?.value;
      if (this.selectedEmployeeId) {
        this.loadEmployeeShiftsById(this.selectedEmployeeId);
      }
    } else {
      this.errorMessage = 'Please select an employee.';
      this.employeeShifts = null;
      this.showEmployeeShiftsTable = false;
    }
  }

  loadEmployeeShiftsById(employeeId: number): void {
    this.shiftService.viewManagerEmployeeShifts(this.managerId, employeeId).subscribe({
      next: (shifts) => {
        this.employeeShifts = shifts;
        this.totalEmployeeShifts = this.employeeShifts.length;
        this.setPagination();
        this.changePage(1); 
        this.errorMessage = '';
        this.showEmployeeShiftsTable = true;
        this.deleteSuccessMessage = '';
        this.deleteErrorMessage = '';
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching shifts for employee:', error);
        this.errorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'Failed to load shifts for the selected employee.';
        this.employeeShifts = null;
        this.showEmployeeShiftsTable = false;
        this.deleteSuccessMessage = '';
        this.deleteErrorMessage = '';
      },
    });
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.totalEmployeeShifts / this.pageSize);
    this.updateVisiblePaginationPages();
  }

  updateVisiblePaginationPages(): void {
    if (this.totalPages <= this.maxPagesToShow) {
      this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
    } else {
      const halfMaxPages = Math.floor(this.maxPagesToShow / 2);
      let startPage = Math.max(this.currentPage - halfMaxPages, 1);
      let endPage = Math.min(startPage + this.maxPagesToShow - 1, this.totalPages);

      if (endPage - startPage + 1 < this.maxPagesToShow) {
        startPage = Math.max(endPage - this.maxPagesToShow + 1, 1);
      }

      this.pages = [];
      this.pages.push(1);

      if (startPage > 2) {
        this.pages.push(-1); // Ellipsis
      }

      for (let i = Math.max(startPage, 2); i <= Math.min(endPage, this.totalPages - 1); i++) {
        this.pages.push(i);
      }

      if (endPage < this.totalPages - 1) {
        this.pages.push(-2); // Ellipsis
      }

      if (this.totalPages > 1) {
        this.pages.push(this.totalPages);
      }
    }
  }

  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      const startIndex = (this.currentPage - 1) * this.pageSize;
      const endIndex = Math.min(startIndex + this.pageSize, this.totalEmployeeShifts);
      this.pagedEmployeeShifts = this.employeeShifts?.slice(startIndex, endIndex) || [];
      this.updateVisiblePaginationPages();
    }
  }

  isEllipsis(pageNumber: number): boolean {
    return pageNumber < 0;
  }

  deleteShift(shiftId: number): void {
    this.deleteSuccessMessage = '';
    this.deleteErrorMessage = '';

    this.shiftService.deleteShift(shiftId, this.managerId).subscribe({
      next: () => {
        console.log(`Shift with ID ${shiftId} deleted successfully.`);
        this.employeeShifts = this.employeeShifts?.filter(shift => shift.shiftId !== shiftId) || [];
        this.totalEmployeeShifts--;
        this.setPagination();
        this.changePage(this.currentPage);
        this.deleteSuccessMessage = `Shift with ID ${shiftId} deleted successfully.`;
      },
      error: (error: HttpErrorResponse) => {
        console.error(`Error deleting shift with ID ${shiftId}:`, error);
        this.deleteErrorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'Failed to delete the shift.';
      },
    });
  }

  updateShift(shift: Shift): void {
    this.router.navigate(['/update-shift'], {
      queryParams: {
        shiftId: shift.shiftId,
        shiftDate: shift.shiftDate,
        shiftTime: shift.shiftTime,
        employeeId: shift.employeeId,
      },
    });
  }
}