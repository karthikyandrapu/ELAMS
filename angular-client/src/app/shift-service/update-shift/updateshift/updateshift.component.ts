import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { ShiftStatus } from 'src/app/model/shift-model/shiftStatus';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { ActivatedRoute } from '@angular/router'; // Import ActivatedRoute
import { AuthenticationService } from 'src/app/service/auth/auth.service'; // Import AuthService

@Component({
  selector: 'app-updateshift',
  standalone: false,
  templateUrl: './updateshift.component.html',
  styleUrls: ['./updateshift.component.css'],
})
export class UpdateshiftComponent implements OnInit {
  shiftId!: number; // ID of the shift to be updated
  managerId!: number; // ID of the manager performing the update
  shiftDate!: string; // New/Existing shift date
  shiftTime!: string; // New/Existing shift time
  employeeId!: number; // Existing employee ID
  successMessage: string = ''; // Message to display on successful update
  errorMessage: string = ''; // Message to display on error
  submitted = false; // Flag to indicate if the form has been submitted
  shiftTimeOptions: { display: string; value: string }[] = [
    { display: '9:00 AM - 6:00 PM', value: '09:00:00' },
    { display: '6:00 PM - 3:00 AM', value: '18:00:00' },
    { display: '3:00 AM - 12:00 PM', value: '03:00:00' },
  ];

  constructor(
    private shiftService: ShiftserviceService,
    private route: ActivatedRoute, // Inject ActivatedRoute
    private authService: AuthenticationService // Inject AuthService
  ) {}

  ngOnInit(): void {
    // Retrieve manager ID from authentication service
    const empId = this.authService.getLoggedInEmpId();
    if (empId) {
      this.managerId = parseInt(empId, 10);
    } else {
      console.error('Manager ID not found in session storage.');
      this.errorMessage = 'Could not retrieve manager information.';
      return;
    }

    // Retrieve data from query parameters
    this.route.queryParams.subscribe(params => {
      this.shiftId = +params['shiftId']; // Convert to number
      this.shiftDate = params['shiftDate'];
      this.shiftTime = params['shiftTime'];
      this.employeeId = +params['employeeId']; // Convert to number

      // Optionally, you can fetch the full shift details from the service here
      // if you need more data to pre-populate the form.
      // this.loadShiftDetails();
    });
  }

  // Optional: Method to load full shift details if needed
  // loadShiftDetails(): void {
  //   this.shiftService.getShiftById(this.shiftId).subscribe({
  //     next: (shift) => {
  //       this.shiftDate = shift.shiftDate;
  //       this.shiftTime = shift.shiftTime;
  //       this.employeeId = shift.employeeId;
  //       // Populate other form fields if necessary
  //     },
  //     error: (error) => {
  //       console.error('Error loading shift details:', error);
  //       this.errorMessage = 'Failed to load shift details for updating.';
  //     }
  //   });
  // }

  updateShift() {
    this.submitted = true;
    this.successMessage = '';
    this.errorMessage = '';

    // Create a Shift object with the updated details
    const updatedShift: Shift = {
      shiftId: this.shiftId,
      shiftDate: this.shiftDate,
      shiftTime: this.shiftTime,
      employeeId: this.employeeId, // Keep the original employee ID
      shiftStatus: new ShiftStatus(), // You might need to fetch and update the status appropriately
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