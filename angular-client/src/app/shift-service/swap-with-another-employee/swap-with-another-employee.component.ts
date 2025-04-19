import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-swap-with-another-employee',
  standalone:false,
  templateUrl: './swap-with-another-employee.component.html',
  styleUrls: ['./swap-with-another-employee.component.css']
})
export class SwapWithAnotherEmployeeComponent implements OnInit {
  swappedShifts: Shift[] = [];
  pagedSwappedShifts: Shift[] = [];
  employeeId!: number;
  errorMessage: string = '';
  noRecordFound = false;
  loading: boolean = false;

  // Pagination properties
  pageSize = 10; // Adjust as needed
  currentPage = 1;
  totalSwappedShifts = 0;
  totalPages = 0;
  pages: number[] = [];
  maxPagesToShow = 5;

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService
  ) {}

  ngOnInit(): void {
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.employeeId = parseInt(empId, 10);
      this.loadSwappedShifts();
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }
  }

  loadSwappedShifts(): void {
    this.loading = true;
    this.noRecordFound = false;
    this.errorMessage = '';
    this.shiftService.viewEmployeeSwappedWithAnotherEmployee(this.employeeId).subscribe({
      next: (data) => {
        this.swappedShifts = data;
        this.totalSwappedShifts = this.swappedShifts.length;
        this.setPagination();
        this.changePage(1); // Load the first page
        this.loading = false;
        if (this.swappedShifts.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching swapped shifts:', error);
        this.errorMessage = 'Failed to load shifts swapped with another employee.';
        this.loading = false;
        this.noRecordFound = true;
      }
    });
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.totalSwappedShifts / this.pageSize);
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
      const endIndex = Math.min(startIndex + this.pageSize, this.totalSwappedShifts);
      this.pagedSwappedShifts = this.swappedShifts.slice(startIndex, endIndex);
      this.updateVisiblePaginationPages();
    }
  }

  isEllipsis(pageNumber: number): boolean {
    return pageNumber < 0;
  }
}