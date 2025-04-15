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

  @ViewChild('attendanceChartCanvas') attendanceChartCanvas!: ElementRef;

  constructor(
    private authService: AuthenticationService,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit(): void {
    console.log('Component initialized');
    this.userId = parseInt(this.authService.getLoggedInEmpId() || '', 10);
    this.userRole = sessionStorage.getItem('role') || '';
    console.log(`User ID: ${this.userId}, Role: ${this.userRole}`);
    this.loadOwnAttendance();
  }

  ngAfterViewInit(): void {
    // Ensure view is properly initialized before attempting to render chart
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
            console.log('Raw Attendance Data:', data);
            this.isLoading = false;
            
            // Handle different possible response structures
            if (Array.isArray(data)) {
              this.attendanceRecordBody = data;
            } else if (data && Array.isArray(data)) {
              this.attendanceRecordBody = data;
            } else if (data && typeof data === 'object') {
              this.attendanceRecordBody = Object.values(data).filter(item => Array.isArray(item)).flat();
              this.attendanceService.setCurrentAttendance(this.attendanceRecordBody)
            } else {
              this.attendanceRecordBody = [];
            }

            if (this.attendanceRecordBody.length > 0) {
              this.attendanceRecords = this.attendanceRecordBody;
              if (this.viewMode === 'graph') {
                this.createBarGraph(this.attendanceRecordBody);
              }
              this.setPagination();
              this.changePage(1);
            } else {
              this.errorMessage = 'No attendance records found.';
              this.clearChart();
            }
          },
          error: (error) => {
            console.error('Error loading attendance:', error);
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

  switchView(mode: 'graph' | 'table'): void {
    console.log(`Switching view to: ${mode}`);
    this.viewMode = mode;
    if (this.viewMode === 'graph' && this.attendanceRecordBody.length > 0) {
      console.log('Recreating graph with existing data');
      this.createBarGraph(this.attendanceRecordBody);
    }
  }

  createBarGraph(dataForGraph: any[]): void {
    // Add a small delay to ensure canvas is ready
    setTimeout(() => {
      if (!this.attendanceChartCanvas?.nativeElement) {
        console.error('Canvas element not found');
        return;
      }

      const ctx = this.attendanceChartCanvas.nativeElement.getContext('2d');
      if (!ctx) {
        console.error('Could not get canvas context');
        return;
      }

      // Filter out records without valid clockInTime
      const validRecords = dataForGraph.filter(record => 
        record.clockInTime && !isNaN(new Date(record.clockInTime).getTime())
      );

      if (validRecords.length === 0) {
        console.warn('No valid records with clockInTime found');
        return;
      }

      // Sort records by date
      validRecords.sort((a, b) => 
        new Date(a.clockInTime).getTime() - new Date(b.clockInTime).getTime()
      );

      const labels = validRecords.map(record => this.formatDate(record.clockInTime));
      const data = validRecords.map(record => record.workHours || 0); // Default to 0 if null
      const backgroundColors = data.map(hours => this.getBarColor(hours));

      // Destroy previous chart if exists
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
                  font: {
                    weight: 'bold'
                  }
                }
              },
              x: {
                title: { 
                  display: true, 
                  text: 'Date',
                  font: {
                    weight: 'bold'
                  }
                }
              }
            },
            plugins: {
              tooltip: {
                callbacks: {
                  label: function(context) {
                    return `Hours: ${context.raw}`;
                  }
                }
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
    if (hours === null) return '#ff6384'; // red
    if (hours > 8) return '#4bc0c0'; // teal
    if (hours > 7) return '#36a2eb'; // blue
    if (hours > 4) return '#ffcd56'; // yellow
    return '#ff6384'; // red
  }

  setPagination(): void {
    this.totalPages = Math.ceil(this.attendanceRecordBody.length / this.pageSize);
    this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  changePage(page: number): void {
    this.currentPage = page;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedAttendanceRecordBody = this.attendanceRecordBody.slice(startIndex, endIndex);
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
    this.setPagination();
    this.clearChart();
  }

  private clearChart(): void {
    if (this.chart) {
      this.chart.destroy();
      this.chart = null;
    }
  }

  
}