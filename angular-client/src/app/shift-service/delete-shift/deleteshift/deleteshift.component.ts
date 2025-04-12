import { Component } from '@angular/core';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
 
@Component({
  selector: 'app-deleteshift',
  standalone:false,
  templateUrl: './deleteshift.component.html',
  styleUrls: ['./deleteshift.component.css']
})
export class DeleteshiftComponent {
 
  shiftId!: number; // ID of the shift to be deleted
  managerId!: number; // ID of the manager performing the deletion
  successMessage: string = ''; // Message to display on successful deletion
  errorMessage: string = ''; // Message to display on error
  submitted = false; // Flag to indicate if the form has been submitted
 
  constructor(private shiftService: ShiftserviceService) {}
 
  deleteShift() {
    this.submitted = true;
    this.successMessage = '';
    this.errorMessage = '';
 
    // Call the service to delete the shift
    this.shiftService.deleteShift(this.shiftId, this.managerId).subscribe(
      () => {
        this.successMessage = 'Shift deleted successfully!';
        console.log('Shift deletion successful');
      },
      error => {
        console.error('Error deleting shift:', error);
        this.errorMessage = 'You are not authorized to delete this shift or an error occurred.';
      }
    );
  }
}
 