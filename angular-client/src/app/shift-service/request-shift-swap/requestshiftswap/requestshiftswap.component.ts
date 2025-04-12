import { Component } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-requestshiftswap',
  standalone:false,
  templateUrl: './requestshiftswap.component.html',
  styleUrls: ['./requestshiftswap.component.css'],
})
export class RequestshiftswapComponent {
  employeeId!: number; // ID of the employee requesting the shift swap
  shiftId!: number; // ID of the shift to be swapped
  swapWithEmployeeId!: number; // ID of the employee whose shift is being requested
  successMessage: string = ''; // Message to display on successful request
  errorMessage: string = ''; // Message to display on error
  submitted = false; // Flag to indicate if the form has been submitted

  constructor(private shiftService: ShiftserviceService) {}

  requestSwap() {
    this.submitted = true;
    this.successMessage = '';
    this.errorMessage = '';

    // Call the service to request a shift swap
    this.shiftService
      .requestShiftSwap(this.employeeId, this.shiftId, this.swapWithEmployeeId)
      .subscribe(
        (response: Shift) => {
          this.successMessage = 'Shift swap request submitted successfully!';
          console.log('Shift swap request successful:', response);
        },
        (error) => {
          console.error('Error requesting shift swap:', error);
          this.errorMessage =
            'You are not authorized to request a shift swap or an error occurred.';
        }
      );
  }
}
