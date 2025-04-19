// import { Component } from '@angular/core';
// import { AttendanceReport } from 'src/app/model/AttendanceReport-model/attendanceReport.model';
// import { AttendanceTrend } from 'src/app/model/AttendanceReport-model/attendanceTrends.model';
// import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';
// @Component({
//   selector: 'app-delete-all-reports-by-employee-id',
//   standalone: false,
//   templateUrl: './delete-all-reports-by-employee-id.component.html',
//   styleUrls: ['./delete-all-reports-by-employee-id.component.css']
// })
// export class DeleteAllReportsByEmployeeIdComponent {
  
//    employeesByTrend: { [key: string]: number[] } = {};
//     trendKeys: string[] = [];
//     managerId: number | null = null;
  
//     reports!: AttendanceReport[];
//     trends: AttendanceTrend[]=[];
//     employeeId!: number;
//     dateRange!: string;
//     reportData: AttendanceReport = {
//       employeeId: null,
//       dateRange: '',
//       totalAttendance:null ,
//       absenteeism: null
//     };
//     updateData: AttendanceReport = {
//       employeeId: null,
//       dateRange: '',
//       totalAttendance: null,
//       absenteeism: null
//     };
//     calculateData = {
//       employeeId: null,
//       dateRange: '',
//       leaveType: '',
//       role: ''
//     };
//   message: string | undefined;
  
//     constructor(private attendanceReportService: AttendanceReportService) { }
  
//     ngOnInit(): void {
//     }
//     fetchReportsByEmployeeId(): void {
//       this.attendanceReportService.getReportsByEmployeeId(this.employeeId).subscribe(data => {
//         this.reports = data;
//       });
//     } 
   
//   // deleteAllReportsByEmployeeId(): void {
//   //   this.attendanceReportService.deleteAllReportsByEmployeeId(this.employeeId).subscribe(data => {
//   //     this.fetchReportsByEmployeeId();
//   //   });
//   // }
  
 
//   deleteAllReportsByEmployeeId(): void {
//     this.attendanceReportService.deleteAllReportsByEmployeeId(this.employeeId).subscribe({
//       next: (data) => {
//         this.fetchReportsByEmployeeId(); // Refresh the report list
//         this.message = "All attendance reports deleted successfully.";
//       },
//       error: (error) => {
//         if (error.status === 404) {
//           this.message = "No attendance reports found for this Employee ID.";
//         } else {
//           this.message = "Error deleting reports: " + error.message;
//         }
//       },
//       complete: () => {
//         //  this.message = "Deletion process completed.";  No message on complete.
//       }
//     });
//   }
  
  
// }
import { Component, OnInit } from '@angular/core';
import { AttendanceReport } from 'src/app/model/AttendanceReport-model/attendanceReport.model';
import { AttendanceTrend } from 'src/app/model/AttendanceReport-model/attendanceTrends.model';
import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';
import { HttpErrorResponse } from '@angular/common/http'; // Import HttpErrorResponse

@Component({
  selector: 'app-delete-all-reports-by-employee-id',
  standalone: false,
  templateUrl: './delete-all-reports-by-employee-id.component.html',
  styleUrls: ['./delete-all-reports-by-employee-id.component.css']
})
export class DeleteAllReportsByEmployeeIdComponent implements OnInit {

  employeesByTrend: { [key: string]: number[] } = {};
  trendKeys: string[] = [];
  managerId: number | null = null;

  reports!: AttendanceReport[];
  trends: AttendanceTrend[] = [];
  employeeId!: number;  // Make sure this is initialized.
  dateRange!: string;
  reportData: AttendanceReport = {
    employeeId: null,
    dateRange: '',
    totalAttendance: null,
    absenteeism: null
  };
  updateData: AttendanceReport = {
    employeeId: null,
    dateRange: '',
    totalAttendance: null,
    absenteeism: null
  };
  calculateData = {
    employeeId: null,
    dateRange: '',
    leaveType: '',
    role: ''
  };
  message: string | undefined;

  constructor(private attendanceReportService: AttendanceReportService) { }

  ngOnInit(): void {
  }

  fetchReportsByEmployeeId(): void {
    if (this.employeeId) { // check if employeeId is defined
      this.attendanceReportService.getReportsByEmployeeId(this.employeeId).subscribe(data => {
        this.reports = data;
      });
    } else {
      this.message = "Please enter an Employee ID.";
    }
  }

  deleteAllReportsByEmployeeId(): void {
    if (this.employeeId) { // Check if employeeId is defined
      this.attendanceReportService.deleteAllReportsByEmployeeId(this.employeeId).subscribe({
        next: (data) => {
          this.fetchReportsByEmployeeId();
          this.message = "All attendance reports deleted successfully.";
        },
        error: (error: HttpErrorResponse) => { // Type the error as HttpErrorResponse
          if (error.status === 404) {
            this.message = "No attendance reports found for this Employee ID.";
          } else {
            this.message = "Error deleting reports: " + error.message;
          }
        },
        complete: () => {
          // this.message = "Deletion process completed.";
        }
      });
    } else {
      this.message = "Please enter an Employee ID."; // set message
    }
  }
}

