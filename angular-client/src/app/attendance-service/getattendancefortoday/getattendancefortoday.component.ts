import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';
import { Modal } from 'bootstrap';

@Component({
  selector: 'app-getattendancefortoday',
  templateUrl: './getattendancefortoday.component.html',
  styleUrls: ['./getattendancefortoday.component.css'],
  standalone: false

})
export class GetattendancefortodayComponent implements OnInit {

  todayAttendanceRecords: any = [];
  errorMessage: string = '';
  userRole: string = "";
  employeeId: number | null = null;
  attendanceModal: Modal | undefined;

  // Pagination properties
  pageSize = 10; // Number of records per page
  currentPage = 1;
  pagedTodayAttendanceRecords: any[] = [];
  totalPages = 1;
  pages: number[] = [];

  @ViewChild('todayAttendanceModal') todayAttendanceModalRef!: ElementRef;

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit(): void {
    this.employeeId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || '';
    const modalElement = document.getElementById('todayAttendanceModal');
    if (modalElement) {
      this.attendanceModal = new Modal(modalElement);
      modalElement.addEventListener('hidden.bs.modal', () => {
        this.currentPage = 1; // Reset page on modal close
      });
    }
  }

  loadTodayAttendance(): void {
    this.attendanceService.getTodayAttendance('MANAGER', this.employeeId).subscribe({
      next: (data) => {
        this.errorMessage = '';
        this.todayAttendanceRecords = Object.values(data)[1];
        this.setPagination();
        this.changePage(1);
        this.showAttendanceModal();
      },
      error: (error) => {
        this.errorMessage = 'Failed to load today\'s attendance: ' + (error.error || error.message);
        console.log('Error Message:', this.errorMessage);
        this.todayAttendanceRecords = [];
        this.pagedTodayAttendanceRecords = [];
        this.showAttendanceModal();
      }
    });
  }

  showAttendanceModal(): void {
    if (this.attendanceModal) {
      this.attendanceModal.show();
    }
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.todayAttendanceRecords.length / this.pageSize);
    this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  changePage(page: number): void {
    this.currentPage = page;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedTodayAttendanceRecords = this.todayAttendanceRecords.slice(startIndex, endIndex);
  }

  openTodayAttendanceModal(): void {
    this.loadTodayAttendance();
  }
}