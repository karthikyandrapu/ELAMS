import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-viewmanagerownshifts',
  standalone: false,
  templateUrl: './viewmanagerownshifts.component.html',
  styleUrls: ['./viewmanagerownshifts.component.css'],
})
export class ViewmanagerownshiftsComponent implements OnInit {
  shifts: Shift[] = []; // Array to hold the list of all shifts
  pagedShifts: Shift[] = []; // Array for shifts on the current page
  managerId!: number; // Manager ID fetched from session storage
  noRecordFound = false; // Flag to indicate if no records are found
  errorMessage: string = ''; // To store error messages

  // Pagination properties
  pageSize = 10; // Adjust as needed
  currentPage = 1;
  totalShifts = 0;
  totalPages = 0;
  pages: number[] = [];
  maxPagesToShow = 5;

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService // Inject AuthenticationService
  ) {}

  ngOnInit(): void {
    // Fetch managerId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10); // Convert empId to number
      this.fetchManagerOwnShifts(); // Automatically fetch shifts on initialization
    } else {
      console.error('Manager ID not found in session storage.');
      this.errorMessage = 'Manager ID not found. Please log in again.';
    }
  }

  fetchManagerOwnShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';

    // Call the service to fetch manager's own shifts
    this.shiftService.viewManagerOwnShifts(this.managerId).subscribe({
      next: (response: Shift[]) => {
        this.shifts = response;
        this.totalShifts = this.shifts.length;
        this.setPagination();
        this.changePage(1); // Load the first page
        if (this.shifts.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error) => {
        console.error("Error fetching manager's own shifts:", error);
        this.errorMessage = 'An error occurred while fetching shifts.';
        this.noRecordFound = true;
      }
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
}