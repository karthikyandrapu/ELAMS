import { Component } from '@angular/core';
import { HeaderComponent } from "../../../../header/header.component";
import { Router } from '@angular/router'; // Import Router

@Component({
  selector: 'app-shiftemployeehome',
  standalone: false,
  templateUrl: './shiftemployeehome.component.html',
  styleUrls: ['./shiftemployeehome.component.css'],
})
export class ShiftemployeehomeComponent {
  isShiftSwapTrackerOpen = false;

  constructor(private router: Router) {} // Inject Router

  toggleShiftSwapTracker() {
    this.isShiftSwapTrackerOpen = !this.isShiftSwapTrackerOpen;
  }

  get arrowIcon(): string {
    return this.isShiftSwapTrackerOpen ? '&#9650;' : '&#9660;';
  }

  navigateToShiftSwapRequest() {
    this.router.navigate(['/shift-swap-request']);
  }

  navigateToShiftSwapReject() {
    this.router.navigate(['/shift-swap-reject']);
  }

  navigateToShiftSwapApproved() {
    this.router.navigate(['/shift-swap-approved']);
  }
}