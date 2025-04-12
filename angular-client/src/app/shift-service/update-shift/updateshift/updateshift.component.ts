import { Component } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { ShiftStatus } from 'src/app/model/shift-model/shiftStatus';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';

@Component({
  selector: 'app-updateshift',
  standalone:false,
  templateUrl: './updateshift.component.html',
  styleUrls: ['./updateshift.component.css'],
})
export class UpdateshiftComponent {
  shiftId!: number; // ID of the shift to be updated
  managerId!: number; // ID of the manager performing the update
  shiftDate!: string; // New shift date
  shiftTime!: string; // New shift time
  successMessage: string = ''; // Message to display on successful update
  errorMessage: string = ''; // Message to display on error
  submitted = false; // Flag to indicate if the form has been submitted

  constructor(private shiftService: ShiftserviceService) {}

  updateShift() {
    this.submitted = true;
    this.successMessage = '';
    this.errorMessage = '';

    // Create a Shift object with the updated details
    const updatedShift: Shift = {
      shiftId: this.shiftId,
      shiftDate: this.shiftDate,
      shiftTime: this.shiftTime,
      employeeId: 0,
      shiftStatus: new ShiftStatus(),
    };

    // Call the service to update the shift
    this.shiftService
      .updateShift(this.shiftId, this.managerId, updatedShift)
      .subscribe(
        (response: Shift) => {
          this.successMessage = 'Shift updated successfully!';
          console.log('Shift update successful:', response);
        },
        (error) => {
          console.error('Error updating shift:', error);
          this.errorMessage =
            'You are not authorized to update this shift or an error occurred.';
        }
      );
  }
}
