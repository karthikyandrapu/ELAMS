import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';
import { Modal } from 'bootstrap';
import { Chart, registerables } from 'chart.js';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service';
Chart.register(...registerables);

@Component({
  selector: 'app-getattendanceforemployee',
  templateUrl: './getattendanceforemployee.component.html',
  styleUrls: ['./getattendanceforemployee.component.css'],
  standalone: false
})
export class GetattendanceforemployeeComponent implements OnInit, AfterViewInit {
  todayAttendanceRecords: any = [];
  errorMessage: string = '';
  managerId!: number;
  userRole: string = "";
  employeeAttendanceRecords: any = [];
  viewSelectedEmployeeId!: number;
  attendanceModal: Modal | undefined;
  @ViewChild('attendanceChartCanvas') attendanceChartCanvas!: ElementRef;
  attendanceChart: Chart | undefined;

  // Pagination properties
  pageSize = 5;
  currentPage = 1;
  pagedAttendanceRecords: any[] = [];
  totalPages = 1;
  pages: number[] = [];
  employeeIds: number[] = [];

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService,
    private employeeService: EmployeeserviceService
  ) { }

  ngOnInit(): void {
    this.managerId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || '';
    const modalElement = document.getElementById('attendanceModal');
    if (modalElement) {
      this.attendanceModal = new Modal(modalElement);
      modalElement.addEventListener('hidden.bs.modal', () => {
        if (this.attendanceChart) {
          this.attendanceChart.destroy();
          this.attendanceChart = undefined;
        }
        this.currentPage = 1;
      });
      modalElement.addEventListener('shown.bs.modal', () => {
        this.renderAttendanceChart();
      });
    }
    this.loadEmployeeIds();
  }

  ngAfterViewInit(): void {
    // Ensures the canvas element is available in the DOM
  }

  loadEmployeeIds(): void {
    this.employeeService.findAllEmployeeIdsByManagerId(this.managerId).subscribe({
      next: (ids) => {
        console.log(ids);
        
        this.employeeIds = ids;
        console.log('Employee IDs loaded:', this.employeeIds);
      },
      error: (error) => {
        console.error('Error loading employee IDs:', error);
        this.errorMessage = 'Failed to load employee list. Please try again later.';
        this.employeeIds = [];
      }
    });
  }

  loadEmployeeAttendance(): void {
    if (this.viewSelectedEmployeeId) {
      this.attendanceService.getEmployeeAttendance(this.viewSelectedEmployeeId, 'MANAGER', this.managerId).subscribe({
        next: (data) => {
          this.errorMessage = '';
          this.employeeAttendanceRecords = Object.values(data)[1];
          this.setPagination();
          this.changePage(1);
          this.showAttendanceModal();
        },
        error: (error) => {
          this.errorMessage = 'Failed to load employee attendance: ' + (error.error || error.message);
          this.employeeAttendanceRecords = [];
          this.pagedAttendanceRecords = [];
          this.showAttendanceModal();
          if (this.attendanceChart) {
            this.attendanceChart.destroy();
            this.attendanceChart = undefined;
          }
        }
      });
    } else {
      this.errorMessage = 'Please select an employee to view their attendance.';
      this.employeeAttendanceRecords = [];
      this.pagedAttendanceRecords = [];
      this.showAttendanceModal();
      if (this.attendanceChart) {
        this.attendanceChart.destroy();
        this.attendanceChart = undefined;
      }
    }
  }
  showAttendanceModal(): void {
    if (this.attendanceModal) {
      this.attendanceModal.show();
    }
  }

  renderAttendanceChart(): void {
    if (this.employeeAttendanceRecords && this.employeeAttendanceRecords.length > 0 && this.attendanceChartCanvas) {
      const dates = this.employeeAttendanceRecords.map((record: any) => new Date(record.clockInTime).toLocaleDateString());
      const workHours = this.employeeAttendanceRecords.map((record: any) => record.workHours);

      this.attendanceChart = new Chart(this.attendanceChartCanvas.nativeElement, {
        type: 'bar',
        data: {
          labels: dates,
          datasets: [{
            label: 'Work Hours',
            data: workHours,
            backgroundColor: 'rgba(54, 162, 235, 0.8)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: 'Work Hours'
              }
            },
            x: {
              title: {
                display: true,
                text: 'Date'
              }
            }
          },
          plugins: {
            tooltip: {
              callbacks: {
                label: (context) => {
                  return `Work Hours: ${context.parsed.y.toFixed(2)}`;
                }
              }
            }
          }
        }
      });
    } else if (this.attendanceChart) {
      this.attendanceChart.destroy();
      this.attendanceChart = undefined;
    }
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.employeeAttendanceRecords.length / this.pageSize);
    this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  changePage(page: number): void {
    this.currentPage = page;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedAttendanceRecords = this.employeeAttendanceRecords.slice(startIndex, endIndex);
  }
}
