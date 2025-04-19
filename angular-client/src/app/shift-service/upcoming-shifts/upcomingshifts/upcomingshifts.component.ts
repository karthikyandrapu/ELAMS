import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { Router } from '@angular/router'; // Import the Router

@Component({
  selector: 'app-upcomingshifts',
  standalone: false,
  templateUrl: './upcomingshifts.component.html',
  styleUrls: ['./upcomingshifts.component.css']
})
export class UpcomingshiftsComponent implements OnInit {
  upcomingShifts: Shift[] = []; // Array to hold the list of all upcoming shifts
  pagedUpcomingShifts: Shift[] = []; // Array for shifts on the current page
  employeeId!: number; // Employee ID fetched from session storage
  noRecordFound = false; // Flag to indicate if no records are found
  errorMessage: string = ''; // To store error messages

  // Pagination properties
  pageSize = 5; // Adjust as needed
  currentPage = 1;
  totalUpcomingShifts = 0;
  totalPages = 0;
  pages: number[] = [];
  maxPagesToShow = 5;

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService,
    private router: Router // Inject the Router
  ) {}

  ngOnInit(): void {
    // Fetch employeeId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.employeeId = parseInt(empId, 10); // Convert empId to number
      this.fetchUpcomingEmployeeShifts(); // Automatically fetch upcoming shifts on initialization
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }
  }

  fetchUpcomingEmployeeShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';

    // Call the service to fetch upcoming employee shifts
    this.shiftService.getUpcomingEmployeeShifts(this.employeeId).subscribe({
      next: (response: Shift[]) => {
        this.upcomingShifts = response;
        this.totalUpcomingShifts = this.upcomingShifts.length;
        this.setPagination();
        this.changePage(1); // Load the first page
        if (this.upcomingShifts.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error) => {
        console.error('Error fetching upcoming employee shifts:', error);
        this.errorMessage = 'An error occurred while fetching upcoming shifts.';
        this.noRecordFound = true;
      }
    });
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.totalUpcomingShifts / this.pageSize);
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
      const endIndex = Math.min(startIndex + this.pageSize, this.totalUpcomingShifts);
      this.pagedUpcomingShifts = this.upcomingShifts.slice(startIndex, endIndex);
      this.updateVisiblePaginationPages();
    }
  }

  isEllipsis(pageNumber: number): boolean {
    return pageNumber < 0;
  }

  navigateToColleagues(shift: Shift): void {
    this.router.navigate(['/getcolleagueshifts'], { queryParams: { shiftId: shift.shiftId, shiftDate: shift.shiftDate } });
  }
}