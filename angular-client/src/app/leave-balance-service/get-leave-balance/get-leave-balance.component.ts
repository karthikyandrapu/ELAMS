import { Component } from '@angular/core';
import { LeaveBalance } from 'src/app/model/leave-model/LeaveBalance';
import { LeaveRequestService } from 'src/app/service/leave-balance/leave-balance-service.service';

@Component({
  selector: 'app-getall',
  templateUrl: './get-leave-balance.component.html',
  styleUrls: ['./get-leave-balance.component.css']
})
export class GetallComponent {

  employeeId!: number;
  leaveType!: string;
  leaveBalance!: LeaveBalance[];
  error !:string;

  constructor( private  leaveRequestService:LeaveRequestService) { }

  ngOnInit(): void {
    // Optionally call getLeaveBalance() on init
  }

  getLeaveBalance(): void {
    
    this.leaveRequestService.getLeaveBalancesOfEmployee(this.employeeId)
      .subscribe({
      next: (data: LeaveBalance[]) => {
        this.leaveBalance = data;
      },
      error: (error: any) => {
        this.error = 'Could not retrieve leave balance';
        console.error('Error fetching leave balances:', error);
      }
      });
  }

}
