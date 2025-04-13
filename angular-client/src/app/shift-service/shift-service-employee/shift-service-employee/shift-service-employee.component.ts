import { Component } from '@angular/core';

@Component({
  selector: 'app-shift-service-employee',
  standalone:false,
  templateUrl: './shift-service-employee.component.html',
  styleUrls: ['./shift-service-employee.component.css']
})
export class ShiftServiceEmployeeComponent {
  isShiftSwapTrackerOpen = false;

  toggleShiftSwapTracker() {
    this.isShiftSwapTrackerOpen = !this.isShiftSwapTrackerOpen;
  }

  get arrowIcon(): string {
    return this.isShiftSwapTrackerOpen ? '&#9650;' : '&#9660;';
  }
  
}
