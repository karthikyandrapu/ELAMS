import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/service/auth/auth.service'; // Adjust the path as needed
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);
 

@Component({
  selector: 'app-sidenavbar',
  standalone:false,
  templateUrl: './sidenavbar.component.html',
  styleUrls: ['./sidenavbar.component.css']
})
export class SidenavbarComponent {
   empRole: string | null = null;
    isDarkMode: boolean = false;
   
    // Dummy data for charts (replace with your actual API calls)
    attendanceData = { total: 150, present: 135, absent: 15 };
    leaveRequestsData = { total: 50, pending: 10, approved: 35, rejected: 5 };
    leaveBalanceData = { averageBalance: 12 };
    shiftData = { totalShifts: 20, openShifts: 2 };
    reportData = { generatedReports: 5 };
   
    constructor(private router: Router, public authService: AuthenticationService) {}
   
    ngOnInit(): void {
      this.empRole = sessionStorage.getItem('role');
      if (this.empRole?.toUpperCase() === 'MANAGER') {
        // Load manager-specific dashboard data/charts if needed
      } else {
        // Load employee-specific dashboard data/charts if needed
      }
      this.renderCharts();
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
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Pending', 'Approved', 'Rejected'],
          datasets: [{
            label: 'Leave Requests',
            data: [this.leaveRequestsData.pending, this.leaveRequestsData.approved, this.leaveRequestsData.rejected],
            backgroundColor: ['#ffc107', '#28a745', '#dc3545'],
            borderWidth: 1
          }]
        },
        options: {
          onClick: () => this.navigateTo(this.empRole?.toUpperCase() === 'MANAGER' ? '/manager/leave' : '/employee/leave'),
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
   
    renderLeaveBalanceChart(): void {
      const ctx = document.getElementById('leaveBalanceChart') as HTMLCanvasElement;
      new Chart(ctx, {
        type: 'line',
        data: {
          labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'], // Example months
          datasets: [{
            label: 'Average Leave Balance',
            data: [15, 14, 13, this.leaveBalanceData.averageBalance, 11, 10], // Example data
            borderColor: '#007bff',
            borderWidth: 2,
            fill: false
          }]
        },
        options: {
          onClick: () => this.navigateTo(this.empRole?.toUpperCase() === 'MANAGER' ? '/manager/leave-balance' : '/employee/leave-balance'), // Adjust route
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
      new Chart(ctx, {
        type: 'pie',
        data: {
          labels: ['Total Shifts', 'Open Shifts'],
          datasets: [{
            data: [this.shiftData.totalShifts, this.shiftData.openShifts],
            backgroundColor: ['#17a2b8', '#6c757d'],
            borderWidth: 1
          }]
        },
        options: {
          onClick: () => this.navigateTo(this.empRole?.toUpperCase() === 'MANAGER' ? '/shift-service-manager' : '/shift-service-employee'),
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
