import { Component, OnInit } from '@angular/core';
import { Shift } from 'src/app/model/shift-model/shift';
import { ShiftStatus } from 'src/app/model/shift-model/shiftStatus';
import { ShiftserviceService } from 'src/app/service/shift-service/shift.service';
import { ActivatedRoute } from '@angular/router';
import { AuthenticationService } from 'src/app/service/auth/auth.service'; 

@Component({
  selector: 'app-updateshift',
  standalone: false,
  templateUrl: './updateshift.component.html',
  styleUrls: ['./updateshift.component.css'],
})
export class UpdateshiftComponent implements OnInit {
  shiftId!: number; 
  managerId!: number; 
  shiftDate!: string;
  shiftTime!: string; 
  employeeId!: number;
  successMessage: string = ''; 
  errorMessage: string = ''; 
  submitted = false; 
  shiftTimeOptions: { display: string; value: string }[] = [
    { display: '9:00 AM - 6:00 PM', value: '09:00:00' },
    { display: '6:00 PM - 3:00 AM', value: '18:00:00' },
    { display: '3:00 AM - 12:00 PM', value: '03:00:00' },
  ];

  constructor(
    private shiftService: ShiftserviceService,
    private route: ActivatedRoute, 
    private authService: AuthenticationService 
  ) {}

  ngOnInit(): void {
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
    });
  }

  updateShift() {
    this.submitted = true;
    this.successMessage = '';
    this.errorMessage = '';

    const updatedShift: Shift = {
      shiftId: this.shiftId,
      shiftDate: this.shiftDate,
      shiftTime: this.shiftTime,
      employeeId: this.employeeId,
      shiftStatus: new ShiftStatus(), 
    };

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