



import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { AttendanceTrend } from 'src/app/model/AttendanceReport-model/attendanceTrends.model';
import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';

@Component({
  selector: 'app-fetch-attendance-trends',
  templateUrl: './fetch-attendance-trends.component.html',
  styleUrls: ['./fetch-attendance-trends.component.css'],
  standalone: false
})
export class FetchAttendanceTrendsComponent implements OnInit, AfterViewInit {
  @ViewChild('attendancePieChartCanvas') attendancePieChartCanvas!: ElementRef;
  attendancePieChart: Chart | undefined;
  employeeId!: number;
  dateRange!: string;
  trends: AttendanceTrend | undefined;

  constructor(private attendanceReportService: AttendanceReportService) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    // Check if the canvas element is available.
    if (this.attendancePieChartCanvas) {
      this.fetchAttendanceTrends(); // It's safe to call this here.
    } else {
      console.error('attendancePieChartCanvas is undefined in ngAfterViewInit');
      // You might want to add a retry mechanism or inform the user.
    }
  }

  fetchAttendanceTrends(): void {
    console.log("fetchAttendanceTrends called");
    this.attendanceReportService.getAttendanceTrends(this.employeeId!, this.dateRange).subscribe(
      (data: AttendanceTrend) => {
        this.trends = data;
        this.createPieChart(); // Call createPieChart here, inside the subscription.
      },
      (error) => {
        console.error('Error fetching attendance trends:', error);
      }
    );
  }

  createPieChart(): void {
    // Double check here as well
    if (!this.attendancePieChartCanvas?.nativeElement) {
      console.error('Canvas element not found');
      return;
    }

    if (!this.trends) {
      console.error('No trend data available to create chart');
      return;
    }

    const attendancePercentage = this.trends.averageAttendancePercentage ?? 0;
    const absenteeismPercentage = this.trends.averageAbsenteeismPercentage ?? 0;

    const canvasElement = this.attendancePieChartCanvas.nativeElement;
    canvasElement.style.height = '300px'; // Set the height using style
    canvasElement.style.width = '300px'; // Set the width using style.

    const chartConfig: ChartConfiguration<'pie'> = {
      type: 'pie',
      data: {
        labels: ['Attendance', 'Absenteeism'],
        datasets: [{
          label: 'Percentage',
          data: [attendancePercentage, absenteeismPercentage],
          backgroundColor: [
            'rgba(75, 192, 192, 0.8)', // Green
            'rgba(255, 99, 132, 0.8)', // Red
          ],
          borderColor: [
            'rgba(75, 192, 192, 1)',
            'rgba(255, 99, 132, 1)',
          ],
          borderWidth: 1,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false, // Allow height to be set explicitly
        plugins: {
          title: {
            display: true,
            text: `Attendance Trend for Employee ${this.trends.employeeId}`,
            font: {
              size: 16
            }
          },
          legend: {
            position: 'top'
          },
        },
      },
    };

    this.attendancePieChart = new Chart(
      canvasElement,
      chartConfig
    );
  }
}
