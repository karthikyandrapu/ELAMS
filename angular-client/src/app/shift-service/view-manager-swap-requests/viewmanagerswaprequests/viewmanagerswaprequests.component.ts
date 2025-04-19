import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-viewmanagerswaprequests',
  standalone: false,
  templateUrl: './viewmanagerswaprequests.component.html',
  styleUrls: ['./viewmanagerswaprequests.component.css']
})
export class ViewmanagerswaprequestsComponent implements OnInit {
  swapRequests: Shift[] = [];
  pagedSwapRequests: Shift[] = []; // For paginated requests
  managerId!: number;
  noRecordFound = false;
  errorMessage: string = '';
  approveSuccessMessage: string = '';
  approveErrorMessage: string = '';
  rejectSuccessMessage: string = '';
  rejectErrorMessage: string = '';

  // Pagination properties
  pageSize = 5; // Adjust as needed
  currentPage = 1;
  totalSwapRequests = 0;
  totalPages = 0;
  pages: number[] = [];
  maxPagesToShow = 5;

  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService
  ) {}

  ngOnInit(): void {
    // Fetch managerId from session storage via AuthenticationService
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10);
      this.loadManagerSwapRequests();
    } else {
      console.error('Manager ID not found in session storage.');
      this.errorMessage = 'Manager ID not found. Please log in again.';
    }
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
        this.totalSwapRequests = this.swapRequests.length;
        this.setPagination();
        this.changePage(1); // Load the first page
        if (this.swapRequests.length === 0) {
          this.noRecordFound = true;
        }
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching manager swap requests:', error);
        this.errorMessage = 'Failed to load swap requests.';
        this.noRecordFound = true;
      }
    });
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.totalSwapRequests / this.pageSize);
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
      const endIndex = Math.min(startIndex + this.pageSize, this.totalSwapRequests);
      this.pagedSwapRequests = this.swapRequests.slice(startIndex, endIndex);
      this.updateVisiblePaginationPages();
    }
  }

  isEllipsis(pageNumber: number): boolean {
    return pageNumber < 0;
  }

  approveSwapRequest(shiftId: number, swapWithEmployeeId: number): void {
    this.approveSuccessMessage = '';
    this.approveErrorMessage = '';
    this.shiftService.approveShiftSwap(shiftId, this.managerId, swapWithEmployeeId)
      .subscribe({
        next: (response) => {
          console.log(`Swap request for shift ID ${shiftId} approved.`, response);
          this.approveSuccessMessage = `Swap request for shift ID ${shiftId} approved.`;
          this.loadManagerSwapRequests(); // Reload to update the list
        },
        error: (error: HttpErrorResponse) => {
          console.error(`Error approving swap request for shift ID ${shiftId}:`, error);
          this.approveErrorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'Failed to approve swap request.';
        }
      });
  }

  rejectSwapRequest(shiftId: number): void {
    this.rejectSuccessMessage = '';
    this.rejectErrorMessage = '';
    this.shiftService.rejectShiftSwap(shiftId, this.managerId)
      .subscribe({
        next: (response) => {
          console.log(`Swap request for shift ID ${shiftId} rejected.`, response);
          this.rejectSuccessMessage = `Swap request for shift ID ${shiftId} rejected.`;
          this.loadManagerSwapRequests(); // Reload to update the list
        },
        error: (error: HttpErrorResponse) => {
          console.error(`Error rejecting swap request for shift ID ${shiftId}:`, error);
          this.rejectErrorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'Failed to reject swap request.';
        }
      });
  }
}