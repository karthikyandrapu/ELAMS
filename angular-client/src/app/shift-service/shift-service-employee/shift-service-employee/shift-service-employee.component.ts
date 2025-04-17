import { Component ,OnInit} from '@angular/core';
import { Router } from '@angular/router'; // Import Router
import { Chart } from 'chart.js';

@Component({
  selector: 'app-shift-service-employee',
  standalone:false,
  templateUrl: './shift-service-employee.component.html',
  styleUrls: ['./shift-service-employee.component.css']
})
export class ShiftServiceEmployeeComponent implements OnInit {
  isShiftSwapTrackerOpen = false;
  shiftData = { totalShifts: 20, openShifts: 2 };
  leaveRequestsData = { total: 50, pending: 10, approved: 35, rejected: 5 };
  

  constructor(private router: Router) {} // Inject Router
  ngOnInit(){
    this.renderCharts();
  }

  ngAfterViewInit(): void {
    this.renderCharts(); // Render charts after the view is initialized
  }
  
  toggleShiftSwapTracker() {
    this.isShiftSwapTrackerOpen = !this.isShiftSwapTrackerOpen;
  }

  get arrowIcon(): string {
    return this.isShiftSwapTrackerOpen ? '&#9650;' : '&#9660;';
  }

  viewAllShifts(): void {
    // Logic to fetch or navigate to all shifts
    console.log('View All My Shifts button clicked');
    // Example: Navigate to a route
    this.router.navigate(['/get-employee-shifts']);
  }

  renderCharts(): void {
    this.renderLeaveRequestsChart();
    this.renderShiftChart();
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
