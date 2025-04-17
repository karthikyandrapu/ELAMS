import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-shift-employee-home',
  standalone: false,
  templateUrl: './shift-employee-home.component.html',
  styleUrls: ['./shift-employee-home.component.css']
})
export class ShiftEmployeeHomeComponent {
   isShiftSwapTrackerOpen = false;
  
    constructor() {}
  
    toggleShiftSwapTracker() {
      this.isShiftSwapTrackerOpen = !this.isShiftSwapTrackerOpen;
    }
  
    get arrowIcon(): string {
      return this.isShiftSwapTrackerOpen ? '&#9650;' : '&#9660;';
    }

}
