import { Component } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-rejectshiftswap',
  standalone:false,
  templateUrl: './rejectshiftswap.component.html',
  styleUrls: ['./rejectshiftswap.component.css'],
})
export class RejectshiftswapComponent {
  shiftId!: number; // ID of the shift for which the swap is being rejected
  managerId!: number; // ID of the manager rejecting the shift swap
  successMessage: string = ''; // Message to display on successful rejection
  errorMessage: string = ''; // Message to display on error
  submitted = false; // Flag to indicate if the form has been submitted

  constructor(private shiftService: ShiftserviceService) {}

  rejectSwap() {
    this.submitted = true;
    this.successMessage = '';
    this.errorMessage = '';

    // Call the service to reject the shift swap
    this.shiftService.rejectShiftSwap(this.shiftId, this.managerId).subscribe(
      (response: Shift) => {
        this.successMessage = 'Shift swap rejected successfully!';
        console.log('Shift swap rejection successful:', response);
      },
      (error) => {
        console.error('Error rejecting shift swap:', error);
        this.errorMessage =
          'You are not authorized to reject this shift swap or an error occurred.';
      }
    );
  }
}
