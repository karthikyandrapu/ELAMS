import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { Chart, registerables } from 'chart.js';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { LeaveRequestService } from 'src/app/service/leave-request-service/leave-request.service';
import { HttpClient } from '@angular/common/http';
import { forkJoin } from 'rxjs'; // Import forkJoin
import { tap } from 'rxjs/operators'; // Import tap for cleaner side effects
 
Chart.register(...registerables);
 
@Component({
  selector: 'app-employeedashboard',
  standalone: false,
  templateUrl: './employeedashboard.component.html',
  styleUrls: ['./employeedashboard.component.css'],
})
export class EmployeedashboardComponent implements OnInit {
  empRole: string | null = null;
  employeeId!: number;
  empName: string | null = null;
  isDarkMode: boolean = false;
  loadingCharts = true;
  leaveRequests: any[] = [];
  errorMessage: string = '';
 
  attendanceData = { total: 150, present: 135, absent: 15 };
  leaveRequestsData = { total: 0, pending: 0, approved: 0, rejected: 0 };
  leaveBalanceData = { averageBalance: 12 };
  shiftData = { totalShifts: 0, scheduledShifts: 0, completedShifts: 0, swappedShiftsCount: 0 };
  reportData = { generatedReports: 5 };
 
  constructor(
    private router: Router,
    private shiftService: ShiftserviceService,
    public authService: AuthenticationService,
    public employeeService: EmployeeserviceService,
    private leaveRequestService: LeaveRequestService,
    private http: HttpClient
  ) {}
 
  ngOnInit(): void {
    this.empRole = sessionStorage.getItem('role');
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.employeeId = parseInt(empId, 10);
      this.loadInitialDataAndLeaveRequests(); // Call the combined loading function
      this.employeeService.getEmployeeById(this.employeeId).subscribe({
        next: (response) => {
          console.log('Employee Name:', response.name);
          sessionStorage.setItem('empName', response.name);
          this.empName = response.name;
        },
        error: (error) => {
          console.error('Error fetching employee name:', error);
        },
      });
    }
  }
 
  loadInitialDataAndLeaveRequests(): void {
    this.loadingCharts = true;
 
    const shifts$ = this.shiftService.getEmployeeShifts(this.employeeId).pipe(
      tap((shifts) => {
        this.shiftData.totalShifts = shifts.length;
        this.shiftData.scheduledShifts = shifts.filter(shift => shift.shiftStatus?.status === 'SCHEDULED').length;
        this.shiftData.completedShifts = shifts.filter(shift => shift.shiftStatus?.status === 'COMPLETED').length;
      })
    );
 
    const leaveRequests$ = this.http
      .get<any[]>(`http://localhost:8052/leave-requests/employee?employeeId=${this.employeeId}`)
      .pipe(
        tap((data: any[]) => {
          this.leaveRequests = data;
          this.leaveRequestsData.total = data.length;
          this.leaveRequestsData.pending = data.filter(request => request.status === 'PENDING').length;
          this.leaveRequestsData.approved = data.filter(request => request.status === 'APPROVED').length;
          this.leaveRequestsData.rejected = data.filter(request => request.status === 'REJECTED').length;
          this.errorMessage = '';
        })
      );
 
    forkJoin([shifts$, leaveRequests$]).subscribe({
      next: () => {
        this.loadingCharts = false;
        this.renderCharts(); // Render charts only after both observables complete
      },
      error: (error) => {
        console.error('Error fetching initial data and leave requests:', error);
        this.loadingCharts = false;
        this.errorMessage = 'Failed to load dashboard data.';
        // Handle error appropriately
      },
    });
  }
 
  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    document.body.classList.toggle('dark-theme');
  }
 
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
 
  renderCharts(): void {
    this.renderAttendanceChart();
    this.renderLeaveRequestsChart();
    this.renderLeaveBalanceChart();
    this.renderShiftChart();
    this.renderReportsChart();
  }
 
  renderAttendanceChart(): void {
    const ctx = document.getElementById('attendanceChart') as HTMLCanvasElement;
    new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Present', 'Absent'],
        datasets: [{
          data: [this.attendanceData.present, this.attendanceData.absent],
          backgroundColor: ['#28a745', '#dc3545'],
          borderWidth: 1
        }]
      },
      options: {
        onClick: () => this.navigateTo(this.empRole?.toUpperCase() === 'MANAGER' ? '/manager/attendance' : '/employee/attendance'),
        plugins: {
          legend: {
            position: 'bottom',
          },
          title: {
            display: true,
            text: 'Attendance Summary',
            font: {
              size: 16
            }
          }
        }
      }
    });
  }
 
  renderLeaveRequestsChart(): void {
    const ctx = document.getElementById('leaveRequestsChart') as HTMLCanvasElement;
    if (ctx) {
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Pending', 'Approved', 'Rejected'],
          datasets: [{
            label: 'leave Requests',
            data: [this.leaveRequestsData.pending, this.leaveRequestsData.approved, this.leaveRequestsData.rejected],
            backgroundColor: ['#ffc107', '#28a745', '#dc3545'],
            borderWidth: 1
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true
            }
          },
          plugins: {
            legend: {
              display: false
            },
            title: {
              display: true,
              text: 'Leave Request Status',
              font: {
                size: 16
              }
            }
          }
        }
      });
    }
  }
 
  renderLeaveBalanceChart(): void {
    const ctx = document.getElementById('leaveBalanceChart') as HTMLCanvasElement;
    if (!ctx) {
      console.error('leaveBalanceChart is undefined');
      return;
    }
    new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [{
          label: 'Average Leave Balance',
          data: [15, 14, 13, this.attendanceData.absent, 11, 10],
          borderColor: '#007bff',
          borderWidth: 2,
          fill: false
        }]
      },
      options: {
        onClick: () => this.navigateTo(this.empRole?.toUpperCase() === 'MANAGER' ? '/manager/leave-balance' : '/employee/leave-balance'),
        scales: {
          y: {
            beginAtZero: true
          }
        },
        plugins: {
          legend: {
            display: false
          },
          title: {
            display: true,
            text: 'Average Leave Balance Trend',
            font: {
              size: 16
            }
          }
        }
      }
    });
  }
 
  renderShiftChart(): void {
    const ctx = document.getElementById('shiftChart') as HTMLCanvasElement;
    if (ctx) {
      new Chart(ctx, {
        type: 'pie',
        data: {
          labels: ['Scheduled', 'Completed'],
          datasets: [{
            data: [this.shiftData.scheduledShifts, this.shiftData.completedShifts],
            backgroundColor: ['#17a2b8', '#6c757d'],
            borderWidth: 1
          }]
        },
        options: {
          plugins: {
            legend: {
              position: 'bottom'
            },
            title: {
              display: true,
              text: 'Shift Overview',
              font: {
                size: 16
              }
            }
          }
        }
      });
    }
  }
 
  renderReportsChart(): void {
    const ctx = document.getElementById('reportsChart') as HTMLCanvasElement;
    new Chart(ctx, {
      type: 'polarArea',
      data: {
        labels: ['Attendance Reports', 'Leave Reports', 'Shift Reports'],
        datasets: [{
          label: 'Generated Reports',
          data: [this.reportData.generatedReports, this.reportData.generatedReports - 2, this.reportData.generatedReports - 1], // Example data
          backgroundColor: ['#00c853', '#3f51b5', '#ff9800'],
          borderWidth: 1
        }]
      },
      options: {
        onClick: () => this.navigateTo(this.empRole?.toUpperCase() === 'MANAGER' ? '/manager-reports' : '/employee/reports'),
        plugins: {
          legend: {
            position: 'bottom'
          },
          title: {
            display: true,
            text: 'Generated Reports',
            font: {
              size: 16
            }
          }
        }
      }
    });
  }
}
 