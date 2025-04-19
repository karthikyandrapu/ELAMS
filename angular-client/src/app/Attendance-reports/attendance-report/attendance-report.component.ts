
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { AttendanceReport } from 'src/app/model/AttendanceReport-model/attendanceReport.model';
import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';
@Component({
  selector: 'app-attendance-report',
  templateUrl: './attendance-report.component.html',
  styleUrls: ['./attendance-report.component.css'],
  standalone: false
})
export class AttendanceReportComponent implements OnInit, AfterViewInit {
  @ViewChild('attendanceChartCanvas') attendanceChartCanvas!: ElementRef;
  attendanceChart: Chart | undefined;
  employeeId: number | null = null; // Change to accept null

  constructor(private attendanceReportService: AttendanceReportService) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    // Fetch employee ID from session storage in ngOnInit
    const empId = sessionStorage.getItem('empId');
    if (empId) {
      this.employeeId = parseInt(empId, 10); // Parse it to a number
    } else {
      console.error('Employee ID not found in session storage');
      // Optionally, handle the error, e.g., redirect to login or show a message
      // this.router.navigate(['/login']); //  Make sure to import Router
    }
  }

  ngAfterViewInit(): void {
    if (this.attendanceChartCanvas && this.employeeId !== null) { //check for null
      this.fetchAndCreateChart();
    } else if (!this.attendanceChartCanvas) {
      console.error('Canvas element is not available in AfterViewInit');
    }
     // else,  employeeId is null, and  no chart is rendered.
  }

  fetchAndCreateChart(): void {
    if (this.employeeId === null) {
      console.error('Employee ID is null, cannot fetch chart.');
      return; // Exit if employeeId is null
    }
    this.attendanceReportService.getReportsByEmployeeId(this.employeeId).subscribe(
      (reports: AttendanceReport[]) => {
        this.createBarChart(reports);
      },
      (error) => {
        console.error('Error fetching attendance reports:', error);
        // Handle error
      }
    );
  }

  createBarChart(reports: AttendanceReport[]): void {
    if (!this.attendanceChartCanvas?.nativeElement) {
      console.error('Canvas element not found');
      return;
    }

    reports.sort((a, b) => a.dateRange.localeCompare(b.dateRange));

    const dateRanges = reports.map(report => report.dateRange);
    const totalAttendanceData = reports.map(report => report.totalAttendance ?? 0);
    const absenteeismData = reports.map(report => report.absenteeism ?? 0);

    const chartConfig: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels: dateRanges,
        datasets: [
          {
            label: 'Total Attendance',
            data: totalAttendanceData,
            backgroundColor: 'rgba(75, 192, 192, 0.6)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1,
          },
          {
            label: 'Absenteeism',
            data: absenteeismData,
            backgroundColor: 'rgba(255, 99, 132, 0.6)',
            borderColor: 'rgba(255, 99, 132, 1)',
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: `Attendance Report for Employee ${this.employeeId}`,
            font: {
              size: 16
            }
          },
          legend: {
            position: 'bottom'
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: function (value: string | number) {
                return value;
              }
            }
          },
        },
      },
    };

    this.attendanceChart = new Chart(
      this.attendanceChartCanvas.nativeElement,
      chartConfig
    );
  }
}


