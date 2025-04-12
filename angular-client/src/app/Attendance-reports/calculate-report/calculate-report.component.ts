// import { Component } from '@angular/core';
// import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';
// import { AttendanceReport } from 'src/app/model/AttendanceReport-model/attendanceReport.model';
// import { AttendanceTrend } from 'src/app/model/AttendanceReport-model/attendanceTrends.model';
// @Component({
//   selector: 'app-calculate-report',
//   standalone: false,
//   templateUrl: './calculate-report.component.html',
//   styleUrls: ['./calculate-report.component.css']
// })
// export class CalculateReportComponent {

// employeesByTrend: { [key: string]: number[] } = {};
//   trendKeys: string[] = [];
//   managerId: number | null = null;

//   reports!: AttendanceReport[];
//   trends: AttendanceTrend[]=[];
//   employeeId!: number;
//   dateRange!: string;
//   reportData: AttendanceReport = {
//     employeeId: null,
//     dateRange: '',
//     totalAttendance:null ,
//     absenteeism: null
//   };
//   updateData: AttendanceReport = {
//     employeeId: null,
//     dateRange: '',
//     totalAttendance: null,
//     absenteeism: null
//   };
//   calculateData = {
//     employeeId: null,
//     dateRange: '',
//     leaveType: '',
//     role: ''
//   };

//   constructor(private attendanceReportService: AttendanceReportService) { }

//   ngOnInit(): void {
//   }

//   // fetchAllReports(): void {
//   //   this.attendanceReportService.getAllReports().subscribe(data => {
//   //     this.reports = data;
//   //   });
//   // }
//   fetchReportsByEmployeeId(): void {
//     this.attendanceReportService.getReportsByEmployeeId(this.employeeId).subscribe(data => {
//       this.reports = data;
//     });
//   }
//   calculateReport(): void {
//     this.attendanceReportService.calculateReport(this.calculateData).subscribe(data => {
//       this.fetchReportsByEmployeeId();
//     });
//   }
// }
import { Component } from '@angular/core';
import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';
import { AttendanceReport } from 'src/app/model/AttendanceReport-model/attendanceReport.model';
import { AttendanceTrend } from 'src/app/model/AttendanceReport-model/attendanceTrends.model';

@Component({
  selector: 'app-calculate-report',
  standalone: false,
  templateUrl: './calculate-report.component.html',
  styleUrls: ['./calculate-report.component.css']
})
export class CalculateReportComponent {

  employeesByTrend: { [key: string]: number[] } = {};
  trendKeys: string[] = [];
  managerId: number | null = null;

  reports!: AttendanceReport[];
  trends: AttendanceTrend[] = [];
  employeeId!: number;
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
    employeeId: null as number | null,
    dateRange: '',
    leaveType: '',
    role: ''
  };
  message!: string;

  constructor(private attendanceReportService: AttendanceReportService) { }

  ngOnInit(): void {
  }

  fetchReportsByEmployeeId(): void {
    this.attendanceReportService.getReportsByEmployeeId(this.employeeId).subscribe(data => {
      this.reports = data;
    });
  }

  calculateReport(): void {
    if ((this.calculateData.employeeId ?? 0) <= 0) {
      this.message = "Employee ID must be a positive number.";
      return;
    }

    this.attendanceReportService.calculateReport(this.calculateData).subscribe({
      next: (data) => {
        this.fetchReportsByEmployeeId();
        this.message = "Report calculated successfully.";
      },
      error: (error) => {
        this.message = "Error calculating report: " + error.message;
      }
    });
  }
}
