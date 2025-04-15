import { Component } from '@angular/core';
import { Router } from '@angular/router'; // Import Router

@Component({
  selector: 'app-shift-service-employee',
  standalone:false,
  templateUrl: './shift-service-employee.component.html',
  styleUrls: ['./shift-service-employee.component.css']
})
export class ShiftServiceEmployeeComponent {
  isShiftSwapTrackerOpen = false;

  constructor(private router: Router) {} // Inject Router

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
  
}
