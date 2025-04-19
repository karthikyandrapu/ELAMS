import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { ActivatedRoute } from '@angular/router'; // Import ActivatedRoute

@Component({
  selector: 'app-getcolleagueshifts',
  standalone: false,
  templateUrl: './getcolleagueshifts.component.html',
  styleUrls: ['./getcolleagueshifts.component.css'],
})
export class GetcolleagueshiftsComponent implements OnInit {
  shifts: Shift[] = []; // Array to hold the list of all shifts
  pagedShifts: Shift[] = []; // Array for shifts on the current page
  shiftId: number | null = null; // To store the shift ID for context
  shiftDate: string = ''; // Date in YYYY-MM-DD format
  noRecordFound = false; // Flag to indicate if no records are found
  errorMessage: string = ''; // To store error messages
  successMessage: string = ''; // To store success messages for swap requests
  private employeeId!: number; // Employee ID fetched from session storage

  // Pagination properties
  pageSize = 10; // Adjust as needed
  currentPage = 1;
  totalShifts = 0;
  totalPages = 0;
  pages: number[] = [];
  maxPagesToShow = 5;

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService,
    private route: ActivatedRoute // Inject ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Fetch employeeId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.employeeId = parseInt(empId, 10); // Convert empId to number
    } else {
      console.error('Employee ID not found in session storage.');
      this.errorMessage = 'Employee ID not found. Please log in again.';
    }

    // Retrieve query parameters
    this.route.queryParams.subscribe(params => {
      this.shiftId = params['shiftId'];
      this.shiftDate = params['shiftDate'] || '';
      // Fetch colleagues immediately when the component loads with shift details
      if (this.shiftDate) {
        this.getColleagueShifts();
      }
    });
  }

  getColleagueShifts(): void {
    this.noRecordFound = false;
    this.errorMessage = '';
    this.successMessage = ''; // Clear any previous success message

    // Call the service to fetch colleague shifts
    this.shiftService
      .getColleagueShifts(this.employeeId, this.shiftDate)
      .subscribe({
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
          console.error('Error fetching colleague shifts:', error);
          this.errorMessage = 'An error occurred while fetching colleagues for the selected shift date.';
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

  requestSwap(colleagueId: number): void {
    this.errorMessage = '';
    this.successMessage = ''; // Clear any previous success message
    if (this.shiftId) {
      this.shiftService.requestShiftSwap(this.employeeId, this.shiftId, colleagueId)
        .subscribe({
          next: (response) => {
            console.log('Swap request successful:', response);
            this.successMessage = 'Shift swap request sent successfully!';
            // Optionally, you might want to refresh the colleague shifts list here
            // this.getColleagueShifts();
          },
          error: (error) => {
            console.error('Error requesting swap:', error);
            this.errorMessage = 'Failed to request shift swap.';
          }
        });
    } else {
      this.errorMessage = 'Selected shift ID is missing. Please navigate from the upcoming shifts table.';
    }
  }
}