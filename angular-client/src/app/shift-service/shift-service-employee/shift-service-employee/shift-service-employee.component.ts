import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router'; // Import Router
import { Chart } from 'chart.js';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { Shift } from 'src/app/model/shift-model/shift';
import { forkJoin } from 'rxjs'; // Import forkJoin to handle multiple observables

@Component({
  selector: 'app-shift-service-employee',
  standalone: false,
  templateUrl: './shift-service-employee.component.html',
  styleUrls: ['./shift-service-employee.component.css']
})
export class ShiftServiceEmployeeComponent implements OnInit, AfterViewInit {
  isShiftSwapTrackerOpen = false;
  shiftData = { totalShifts: 0, scheduledShifts: 0, completedShifts: 0, swappedShiftsCount: 0 }; // Updated shiftData
  swapRequestsData = { total: 0, pending: 0, approved: 0, rejected: 0 };
  employeeId!: number;
  swappedShifts: Shift[] = []; // To store swapped shifts data
  showSwappedShiftsPopup = false; // Control visibility of swapped shifts
  loadingCharts = true; // Add a loading flag for charts

  constructor(
    private router: Router,
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService
  ) {} // Inject Router and ShiftService

  ngOnInit() {
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.employeeId = parseInt(empId, 10);
      this.loadInitialData(); // Load all necessary data
    } else {
      console.error('Employee ID not found in session storage.');
      this.loadingCharts = false; // Ensure loading flag is false in case of error
    }
  }

  ngAfterViewInit(): void {
    // Charts will be rendered after data is loaded
  }

  loadInitialData(): void {
    this.loadingCharts = true;
    forkJoin([
      this.shiftService.getEmployeeShifts(this.employeeId),
      this.shiftService.viewEmployeeSwapRequests(this.employeeId)
    ]).subscribe({
      next: ([shifts, swapRequests]) => {
        this.shiftData.totalShifts = shifts.length;
        this.shiftData.scheduledShifts = shifts.filter(shift => shift.shiftStatus?.status === 'SCHEDULED').length;
        this.shiftData.completedShifts = shifts.filter(shift => shift.shiftStatus?.status === 'COMPLETED').length;

        this.swapRequestsData.pending = swapRequests.filter(sr => sr.shiftStatus?.status === 'SWAP_REQUESTED').length;
        this.swapRequestsData.approved = swapRequests.filter(sr => sr.shiftStatus?.status === 'SWAP_REQUEST_APPROVED').length;
        this.swapRequestsData.rejected = swapRequests.filter(sr => sr.shiftStatus?.status === 'SWAP_REQUEST_REJECTED').length;

        this.renderCharts(); // Render charts after all data is loaded
        this.loadingCharts = false;
      },
      error: (error) => {
        console.error('Error fetching initial data:', error);
        this.loadingCharts = false;
        // Handle error appropriately
      }
    });

    this.loadSwappedShiftsCount(); // Load swapped shifts count separately if needed in the notification
  }

  loadSwappedShiftsCount(): void {
    this.shiftService.viewEmployeeSwappedWithAnotherEmployee(this.employeeId).subscribe({
      next: (swappedShifts) => {
        this.shiftData.swappedShiftsCount = swappedShifts.length;
      },
      error: (error) => {
        console.error('Error fetching swapped shifts:', error);
        // Optionally handle error
      }
    });
  }

  toggleShiftSwapTracker() {
    this.isShiftSwapTrackerOpen = !this.isShiftSwapTrackerOpen;
  }

  get arrowIcon(): string {
    return this.isShiftSwapTrackerOpen ? '&#9650;' : '&#9660;';
  }

  viewAllShifts(): void {
    this.router.navigate(['/get-employee-shifts']);
  }

  toggleSwappedShiftsPopup() {
    this.showSwappedShiftsPopup = !this.showSwappedShiftsPopup;
  }

  renderCharts(): void {
    this.renderswapRequestsChart();
    this.renderShiftChart();
  }

  renderswapRequestsChart(): void {
    const ctx = document.getElementById('swapRequestsChart') as HTMLCanvasElement;
    if (ctx) {
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Pending', 'Approved', 'Rejected'],
          datasets: [{
            label: 'Swap Requests',
            data: [this.swapRequestsData.pending, this.swapRequestsData.approved, this.swapRequestsData.rejected],
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
              text: 'Swap Request Status',
              font: {
                size: 16
              }
            }
          }
        }
      });
    }
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
}