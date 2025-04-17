import { Component, OnInit, OnDestroy, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';
import { Attendance } from 'src/app/model/attendance-model/Attendance';
import { Chart, ChartOptions, ChartData, registerables } from 'chart.js';
import { Subscription } from 'rxjs';

Chart.register(...registerables);

@Component({
  selector: 'app-displayattendance',
  templateUrl: './displayattendance.component.html',
  styleUrls: ['./displayattendance.component.css'],
  standalone: false
})
export class DisplayattendanceComponent implements OnInit, OnDestroy, AfterViewInit {
  userId: number | null = null;
  userRole: string = "";
  attendanceRecordBody: any[] = [];
  attendanceRecords: Attendance[] = [];
  errorMessage: string = '';
  viewMode: 'graph' | 'table' = 'graph';
  chart: Chart | null = null;
  attendanceSubscription: Subscription | undefined;
  isLoading: boolean = false;

  // Pagination properties (for table view)
  pageSize = 10;
  currentPage = 1;
  pagedAttendanceRecordBody: any[] = [];
  totalPages = 1;
  pages: number[] = [];
  maxPagesToShow = 5;

  // Current week filter properties
  currentWeekOnly: boolean = true;
  currentWeekData: any[] = [];

  // Month view properties
  currentMonthIndex: number = 0;
  attendanceByMonth: { [key: string]: any[] } = {};
  monthLabels: string[] = [];
  currentMonthData: any[] = [];
  currentMonthLabel: string = '';

  @ViewChild('attendanceChartCanvas') attendanceChartCanvas!: ElementRef;

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit(): void {
    this.userId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || '';
    this.loadOwnAttendance();
  }

  ngAfterViewInit(): void {
    // Ensure view is properly initialized
  }

  ngOnDestroy(): void {
    if (this.attendanceSubscription) {
      this.attendanceSubscription.unsubscribe();
    }
    this.clearChart();
  }

  loadOwnAttendance(): void {
    if (this.userId && this.userRole) {
      this.isLoading = true;
      this.errorMessage = '';
      
      this.attendanceSubscription = this.attendanceService.getOwnAttendance(this.userId, this.userRole)
        .subscribe({
          next: (data) => {
            this.isLoading = false;
            
            if (Array.isArray(data)) {
              this.attendanceRecordBody = data;
            } else if (data && Array.isArray(data)) {
              this.attendanceRecordBody = data;
            } else if (data && typeof data === 'object') {
              this.attendanceRecordBody = Object.values(data).filter(item => Array.isArray(item)).flat();
            } else {
              this.attendanceRecordBody = [];
            }

            if (this.attendanceRecordBody.length > 0) {
              this.attendanceRecords = this.attendanceRecordBody;
              this.filterCurrentWeekData();
              this.organizeAttendanceByMonth();
              if (this.viewMode === 'graph') {
                this.createBarGraph(this.currentWeekOnly ? this.currentWeekData : this.currentMonthData);
              }
              this.setPagination();
              this.changePage(1);
            } else {
              this.errorMessage = 'No attendance records found.';
              this.clearChart();
            }
          },
          error: (error) => {
            this.isLoading = false;
            this.errorMessage = 'Failed to load attendance records. Please try again later.';
            this.clearData();
          }
        });
    } else {
      this.errorMessage = 'Employee ID or role not found. Please log in again.';
      this.clearData();
    }
  }

  // New method to organize attendance by month
  organizeAttendanceByMonth(): void {
    this.attendanceByMonth = {};
    this.monthLabels = [];
    
    if (!this.attendanceRecordBody || this.attendanceRecordBody.length === 0) {
      return;
    }
    
    this.attendanceRecordBody.forEach((record: any) => {
      const date = new Date(record.clockInTime);
      const monthKey = `${date.getFullYear()}-${date.getMonth() + 1}`;
      const monthLabel = date.toLocaleString('default', { month: 'long', year: 'numeric' });
      
      if (!this.attendanceByMonth[monthKey]) {
        this.attendanceByMonth[monthKey] = [];
        this.monthLabels.push(monthLabel);
      }
      
      this.attendanceByMonth[monthKey].push(record);
    });
    
    this.monthLabels.sort((a, b) => {
      const dateA = new Date(a);
      const dateB = new Date(b);
      return dateB.getTime() - dateA.getTime();
    });
    
    this.currentMonthIndex = 0;
    this.setCurrentMonthData();
  }

  setCurrentMonthData(): void {
    if (this.monthLabels.length === 0) {
      this.currentMonthData = [];
      this.currentMonthLabel = 'No Data';
      return;
    }
    
    this.currentMonthLabel = this.monthLabels[this.currentMonthIndex];
    const date = new Date(this.currentMonthLabel);
    const monthKey = `${date.getFullYear()}-${date.getMonth() + 1}`;
    this.currentMonthData = this.attendanceByMonth[monthKey] || [];
  }

  previousMonth(): void {
    if (this.currentMonthIndex < this.monthLabels.length - 1) {
      this.currentMonthIndex++;
      this.setCurrentMonthData();
      if (this.viewMode === 'graph') {
        this.createBarGraph(this.currentMonthData);
      }
      this.changePage(1);
    }
  }

  nextMonth(): void {
    if (this.currentMonthIndex > 0) {
      this.currentMonthIndex--;
      this.setCurrentMonthData();
      if (this.viewMode === 'graph') {
        this.createBarGraph(this.currentMonthData);
      }
      this.changePage(1);
    }
  }

  switchView(mode: 'graph' | 'table'): void {
    this.viewMode = mode;
    if (this.viewMode === 'graph' && this.attendanceRecordBody.length > 0) {
      this.createBarGraph(this.currentWeekOnly ? this.currentWeekData : this.currentMonthData);
    }
  }

  filterCurrentWeekData(): void {
    const today = new Date();
    const firstDayOfWeek = new Date(today);
    const day = today.getDay();
    const diff = today.getDate() - day + (day === 0 ? -6 : 1);
    firstDayOfWeek.setDate(diff);
    firstDayOfWeek.setHours(0, 0, 0, 0);
    
    const lastDayOfWeek = new Date(firstDayOfWeek);
    lastDayOfWeek.setDate(lastDayOfWeek.getDate() + 6);
    lastDayOfWeek.setHours(23, 59, 59, 999);
    
    this.currentWeekData = this.attendanceRecordBody.filter(record => {
      const recordDate = new Date(record.clockInTime);
      return recordDate >= firstDayOfWeek && recordDate <= lastDayOfWeek;
    });
  }

  toggleWeekFilter(): void {
    this.currentWeekOnly = !this.currentWeekOnly;
    if (this.viewMode === 'graph') {
      this.createBarGraph(this.currentWeekOnly ? this.currentWeekData : this.currentMonthData);
    }
  }

  createBarGraph(dataForGraph: any[]): void {
    setTimeout(() => {
      if (!this.attendanceChartCanvas?.nativeElement) return;

      const ctx = this.attendanceChartCanvas.nativeElement.getContext('2d');
      if (!ctx) return;

      const validRecords = dataForGraph.filter(record => 
        record.clockInTime && !isNaN(new Date(record.clockInTime).getTime())
      );

      if (validRecords.length === 0) return;

      validRecords.sort((a, b) => 
        new Date(a.clockInTime).getTime() - new Date(b.clockInTime).getTime()
      );

      const labels = validRecords.map(record => this.formatDate(record.clockInTime));
      const data = validRecords.map(record => record.workHours || 0);
      const backgroundColors = data.map(hours => this.getBarColor(hours));

      this.clearChart();

      try {
        this.chart = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: labels,
            datasets: [{
              label: 'Work Hours',
              data: data,
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
                  font: { weight: 'bold' }
                }
              },
              x: {
                title: { 
                  display: true, 
                  text: 'Date',
                  font: { weight: 'bold' }
                }
              }
            },
            plugins: {
              tooltip: {
                callbacks: {
                  label: (context) => `Hours: ${context.raw}`
                }
              },
              title: {
                display: true,
                text: this.currentWeekOnly ? 
                  'Current Week Attendance' : 
                  `${this.currentMonthLabel} Attendance`,
                font: { size: 16 }
              }
            }
          }
        });
      } catch (error) {
        console.error('Error creating chart:', error);
      }
    }, 100);
  }

  getBarColor(hours: number | null): string {
    if (hours === null) return '#ff6384';
    if (hours > 8) return '#4bc0c0';
    if (hours > 7) return '#36a2eb';
    if (hours > 4) return '#ffcd56';
    return '#ff6384';
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.attendanceRecordBody.length / this.pageSize);
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
        this.pages.push(-1);
      }
      
      for (let i = Math.max(startPage, 2); i <= Math.min(endPage, this.totalPages - 1); i++) {
        this.pages.push(i);
      }
      
      if (endPage < this.totalPages - 1) {
        this.pages.push(-2);
      }
      
      if (this.totalPages > 1) {
        this.pages.push(this.totalPages);
      }
    }
  }

  changePage(page: number): void {
    this.currentPage = page;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedAttendanceRecordBody = this.attendanceRecordBody.slice(startIndex, endIndex);
    this.updateVisiblePaginationPages();
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
      });
    } catch (e) {
      return 'Invalid Date';
    }
  }

  private clearData(): void {
    this.attendanceRecords = [];
    this.attendanceRecordBody = [];
    this.pagedAttendanceRecordBody = [];
    this.currentWeekData = [];
    this.setPagination();
    this.clearChart();
  }

  private clearChart(): void {
    if (this.chart) {
      this.chart.destroy();
      this.chart = null;
    }
  }

  isEllipsis(pageNumber: number): boolean {
    return pageNumber < 0;
  }
}
