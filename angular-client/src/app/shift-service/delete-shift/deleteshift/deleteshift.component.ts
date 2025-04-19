import { Component } from '@angular/core';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-deleteshift',
  standalone: false,
  template: '', // This component might not have its own visible template
  // templateUrl: './deleteshift.component.html', // If you want a confirmation message
  styleUrls: ['./deleteshift.component.css'],
})
export class DeleteshiftComponent {
  constructor(
    private shiftService: ShiftserviceService,
    private authService: AuthenticationService
  ) {}

  deleteShift(shiftId: number, managerId: number, onSuccess: () => void, onError: (errorMessage: string) => void): void {
    this.shiftService.deleteShift(shiftId, managerId).subscribe({
      next: () => {
        console.log(`Shift with ID ${shiftId} deleted successfully from DeleteshiftComponent.`);
        onSuccess(); // Callback to notify the parent component of success
      },
      error: (error: HttpErrorResponse) => {
        console.error(`Error deleting shift with ID ${shiftId} from DeleteshiftComponent:`, error);
        const errorMessage = error.error ? (typeof error.error === 'string' ? error.error : JSON.stringify(error.error)) : 'Failed to delete the shift.';
        onError(errorMessage); // Callback to notify the parent component of the error
      },
    });
  }
}