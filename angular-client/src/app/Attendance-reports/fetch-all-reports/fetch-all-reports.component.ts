import { Component, OnInit } from '@angular/core';
import { AttendanceReport } from 'src/app/model/AttendanceReport-model/attendanceReport.model';
import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';

@Component({
  selector: 'app-fetch-all-reports',
  standalone:false,
  templateUrl: './fetch-all-reports.component.html',
  styleUrls: ['./fetch-all-reports.component.css']
})
export class FetchAllReportsComponent implements OnInit {
  reports: AttendanceReport[] = [];
  message: string = '';

  constructor(private attendanceReportService: AttendanceReportService) {}

  ngOnInit(): void {
    this.fetchAllReports();
  }

  fetchAllReports(): void {
    this.attendanceReportService.getAllReports().subscribe(
      (data) => {
        this.reports = data;
        this.message = ''; // Clear any previous message
      },
      (error) => {
        this.message = "Error fetching all reports: " + error.message;
        this.reports = []; // Clear reports on error
      }
    );
  }
}
