import { Component } from '@angular/core';

import { AttendanceReport } from 'src/app/model/AttendanceReport-model/attendanceReport.model';
import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';
import { AttendanceTrend } from 'src/app/model/AttendanceReport-model/attendanceTrends.model';





@Component({
  selector: 'app-fetch-reports-by-employee-id',
  standalone: false,
  templateUrl: './fetch-reports-by-employee-id.component.html',
  styleUrls: ['./fetch-reports-by-employee-id.component.css']
})
export class FetchReportsByEmployeeIdComponent {

 employeesByTrend: { [key: string]: number[] } = {};
  trendKeys: string[] = [];
  managerId: number | null = null;

  reports!: AttendanceReport[];
  trends: AttendanceTrend[]=[];
  employeeId!: number;
  dateRange!: string;
  reportData: AttendanceReport = {
    employeeId: null,
    dateRange: '',
    totalAttendance:null ,
    absenteeism: null
  };
  updateData: AttendanceReport = {
    employeeId: null,
    dateRange: '',
    totalAttendance: null,
    absenteeism: null
  };
  
  
  




  constructor(private attendanceReportService: AttendanceReportService) { }

  ngOnInit(): void {
  }

  fetchReportsByEmployeeId(): void {
    this.attendanceReportService.getReportsByEmployeeId(this.employeeId).subscribe(data => {
      this.reports = data;
    });
  }
}
