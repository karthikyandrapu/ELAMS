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

  // Month view properties
  currentMonthIndex: number = 0;
  attendanceByMonth: { [key: string]: any[] } = {};
  monthLabels: string[] = [];
  currentMonthData: any[] = [];
  currentMonthLabel: string = '';

  // Pagination properties
  pageSize = 5;
  currentPage = 1;
  pagedAttendanceRecords: any[] = [];
  totalPages = 1;
  pages: number[] = [];
  employeeIds: number[] = [];
  maxPagesToShow = 5; // Maximum number of pagination buttons to show

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
        this.organizeAttendanceByMonth();
        this.renderCurrentMonthChart();
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
          this.organizeAttendanceByMonth();
          this.setPagination();
          this.changePage(1);
          this.showAttendanceModal();
        },
        error: (error) => {
          this.errorMessage = 'Failed to load employee attendance: ' + (error.error || error.message);
          this.employeeAttendanceRecords = [];
          this.pagedAttendanceRecords = [];
          this.attendanceByMonth = {};
          this.monthLabels = [];
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
      this.attendanceByMonth = {};
      this.monthLabels = [];
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

  organizeAttendanceByMonth(): void {
    this.attendanceByMonth = {};
    this.monthLabels = [];
    
    if (!this.employeeAttendanceRecords || this.employeeAttendanceRecords.length === 0) {
      return;
    }
    
    // Group records by month
    this.employeeAttendanceRecords.forEach((record: any) => {
      const date = new Date(record.clockInTime);
      const monthKey = `${date.getFullYear()}-${date.getMonth() + 1}`;
      const monthLabel = date.toLocaleString('default', { month: 'long', year: 'numeric' });
      
      if (!this.attendanceByMonth[monthKey]) {
        this.attendanceByMonth[monthKey] = [];
        this.monthLabels.push(monthLabel);
      }
      
      this.attendanceByMonth[monthKey].push(record);
    });
    
    // Sort monthLabels by date (newest first)
    this.monthLabels.sort((a, b) => {
      const dateA = new Date(a);
      const dateB = new Date(b);
      return dateB.getTime() - dateA.getTime();
    });
    
    // Initialize with the latest month
    this.currentMonthIndex = 0;
    this.setCurrentMonthData();
    
    console.log('Attendance organized by month:', this.attendanceByMonth);
    console.log('Month labels:', this.monthLabels);
  }

  setCurrentMonthData(): void {
    if (this.monthLabels.length === 0) {
      this.currentMonthData = [];
      this.currentMonthLabel = 'No Data';
      return;
    }
    
    this.currentMonthLabel = this.monthLabels[this.currentMonthIndex];
    
    // Convert month label back to key format
    const date = new Date(this.currentMonthLabel);
    const monthKey = `${date.getFullYear()}-${date.getMonth() + 1}`;
    
    this.currentMonthData = this.attendanceByMonth[monthKey] || [];
    
    // Update pagination for table to show current month's data
    this.pagedAttendanceRecords = this.currentMonthData.slice(0, this.pageSize);
    this.totalPages = Math.ceil(this.currentMonthData.length / this.pageSize);
    this.updateVisiblePaginationPages();
  }

  previousMonth(): void {
    if (this.currentMonthIndex < this.monthLabels.length - 1) {
      this.currentMonthIndex++;
      this.setCurrentMonthData();
      this.renderCurrentMonthChart();
      this.changePage(1); // Reset to first page when changing month
    }
  }

  nextMonth(): void {
    if (this.currentMonthIndex > 0) {
      this.currentMonthIndex--;
      this.setCurrentMonthData();
      this.renderCurrentMonthChart();
      this.changePage(1); // Reset to first page when changing month
    }
  }

  renderCurrentMonthChart(): void {
    if (this.attendanceChart) {
      this.attendanceChart.destroy();
      this.attendanceChart = undefined;
    }
    
    if (this.currentMonthData.length === 0 || !this.attendanceChartCanvas) {
      return;
    }
    
    // Sort by date
    const sortedData = [...this.currentMonthData].sort((a, b) => 
      new Date(a.clockInTime).getTime() - new Date(b.clockInTime).getTime()
    );
    
    const dates = sortedData.map((record: any) => 
      new Date(record.clockInTime).toLocaleDateString('en-US', { day: 'numeric' })
    );
    
    const workHours = sortedData.map((record: any) => record.workHours);
    const backgroundColors = workHours.map((hours: number) => this.getBarColor(hours));
    
    this.attendanceChart = new Chart(this.attendanceChartCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: dates,
        datasets: [{
          label: 'Work Hours',
          data: workHours,
          backgroundColor: backgroundColors,
          borderColor: backgroundColors,
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: 'Work Hours',
              font: {
                weight: 'bold'
              }
            }
          },
          x: {
            title: {
              display: true,
              text: 'Day',
              font: {
                weight: 'bold'
              }
            }
          }
        },
        plugins: {
          title: {
            display: true,
            text: this.currentMonthLabel,
            font: {
              size: 16,
              weight: 'bold'
            }
          },
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
  }

  getBarColor(hours: number | null): string {
    if (hours === null) return '#ff6384'; // red
    if (hours > 8) return '#4bc0c0'; // teal
    if (hours > 7) return '#36a2eb'; // blue
    if (hours > 4) return '#ffcd56'; // yellow
    return '#ff6384'; // red
  }

  setPagination(): void {
    if (this.currentMonthData && this.currentMonthData.length > 0) {
      this.totalPages = Math.ceil(this.currentMonthData.length / this.pageSize);
    } else {
      this.totalPages = Math.ceil(this.employeeAttendanceRecords.length / this.pageSize);
    }
    this.updateVisiblePaginationPages();
  }

  // Smart pagination display to save space
  updateVisiblePaginationPages(): void {
    if (this.totalPages <= this.maxPagesToShow) {
      // If total pages are less than max to show, display all pages
      this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
    } else {
      const halfMaxPages = Math.floor(this.maxPagesToShow / 2);
      
      // Calculate start and end pages to show
      let startPage = Math.max(this.currentPage - halfMaxPages, 1);
      let endPage = Math.min(startPage + this.maxPagesToShow - 1, this.totalPages);
      
      // Adjust if at end of range
      if (endPage - startPage + 1 < this.maxPagesToShow) {
        startPage = Math.max(endPage - this.maxPagesToShow + 1, 1);
      }
      
      // Create array with page numbers to display
      this.pages = [];
      
      // Always add first page
      this.pages.push(1);
      
      // Add ellipsis after first page if needed
      if (startPage > 2) {
        this.pages.push(-1); // Using -1 to represent ellipsis
      }
      
      // Add pages around current page
      for (let i = Math.max(startPage, 2); i <= Math.min(endPage, this.totalPages - 1); i++) {
        this.pages.push(i);
      }
      
      // Add ellipsis before last page if needed
      if (endPage < this.totalPages - 1) {
        this.pages.push(-2); // Using -2 to represent ellipsis
      }
      
      // Always add last page if more than one page
      if (this.totalPages > 1) {
        this.pages.push(this.totalPages);
      }
    }
  }

  changePage(page: number): void {
    this.currentPage = page;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    
    // Use current month data if available, otherwise use all records
    if (this.currentMonthData && this.currentMonthData.length > 0) {
      this.pagedAttendanceRecords = this.currentMonthData.slice(startIndex, endIndex);
    } else {
      this.pagedAttendanceRecords = this.employeeAttendanceRecords.slice(startIndex, endIndex);
    }
    
    this.updateVisiblePaginationPages();
  }

  // Helper method to check if the page number is an ellipsis
  isEllipsis(pageNumber: number): boolean {
    return pageNumber < 0;
  }
}
