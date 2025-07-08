import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-swaprequested',
  standalone: false,
  templateUrl: './swaprequested.component.html',
  styleUrls: ['./swaprequested.component.css'],
})
export class SwaprequestedComponent implements OnInit {
  requestedSwaps: Shift[] = [];
  pagedRequestedSwaps: Shift[] = [];
  employeeId!: number;
  errorMessage: string = '';
  noRecordFound = false;

  // Pagination properties
  pageSize = 10; 
  currentPage = 1;
  totalRequestedSwaps = 0;
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
      this.loadRequestedSwaps();
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }
  }

  loadRequestedSwaps(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.shiftService.viewEmployeeSwapRequests(this.employeeId).subscribe({
      next: (data) => {
        this.requestedSwaps = data;
        this.totalRequestedSwaps = this.requestedSwaps.length;
        this.setPagination();
        this.changePage(1); // Load the first page
        if (this.requestedSwaps.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error) => {
        console.error('Error fetching swap requests:', error);
        this.errorMessage = 'Failed to load swap requests.';
        this.noRecordFound = true;
      },
    });
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.totalRequestedSwaps / this.pageSize);
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
      const endIndex = Math.min(startIndex + this.pageSize, this.totalRequestedSwaps);
      this.pagedRequestedSwaps = this.requestedSwaps.slice(startIndex, endIndex);
      this.updateVisiblePaginationPages();
    }
  }

  isEllipsis(pageNumber: number): boolean {
    return pageNumber < 0;
  }
}