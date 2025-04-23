import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-viewmanagershifts',
  standalone: false,
  templateUrl: './viewmanagershifts.component.html',
  styleUrls: ['./viewmanagershifts.component.css'],
})
export class ViewmanagershiftsComponent implements OnInit {
  shifts: Shift[] = []; 
  pagedShifts: Shift[] = []; 
  managerId!: number;
  noRecordFound = false; 
  errorMessage: string = ''; 
  deleteSuccessMessage: string = '';
  deleteErrorMessage: string = '';

  // Pagination properties
  pageSize = 10; 
  currentPage = 1;
  totalShifts = 0;
  totalPages = 0;
  pages: number[] = [];
  maxPagesToShow = 5;

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService, 
    private router: Router
  ) {}

  ngOnInit(): void {
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10);
      this.fetchManagerShifts();
    } else {
      console.error('Manager ID not found in session storage.');
      this.errorMessage = 'Manager ID not found. Please log in again.';
    }
  }

  fetchManagerShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.deleteSuccessMessage = '';
    this.deleteErrorMessage = '';

    this.shiftService.viewManagerShifts(this.managerId).subscribe({
      next: (response: Shift[]) => {
        this.shifts = response;
        this.totalShifts = this.shifts.length;
        this.setPagination();
        this.changePage(1); // Load the first page
        if (this.shifts.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching manager shifts:', error);
        this.errorMessage = 'An error occurred while fetching shifts.';
        this.noRecordFound = true;
      },
    });
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.totalShifts / this.pageSize);
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
      const endIndex = Math.min(startIndex + this.pageSize, this.totalShifts);
      this.pagedShifts = this.shifts.slice(startIndex, endIndex);
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
        this.shifts = this.shifts.filter((shift) => shift.shiftId !== shiftId);
        this.totalShifts--; // Update total count
        this.setPagination();
        this.changePage(this.currentPage); // Stay on the current page if possible
        this.deleteSuccessMessage = `Shift with ID ${shiftId} deleted successfully.`;
      },
      error: (error: HttpErrorResponse) => {
        console.error(`Error deleting shift with ID ${shiftId}:`, error);
        this.deleteErrorMessage = error.error
          ? typeof error.error === 'string'
            ? error.error
            : JSON.stringify(error.error)
          : 'Failed to delete the shift.';
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