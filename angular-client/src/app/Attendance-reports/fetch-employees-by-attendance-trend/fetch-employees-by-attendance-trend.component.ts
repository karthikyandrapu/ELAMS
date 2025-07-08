// import { Component } from '@angular/core';
// import { AttendanceReportService } from '../service/AttendanceReport-service/attendance-report.service';
// import { AttendanceReport } from '../model/AttendanceReport-model/attendanceReport.model';
// import { AttendanceTrend } from '../model/AttendanceReport-model/attendanceTrends.model';

// @Component({
//   selector: 'app-fetch-employees-by-attendance-trend',
//   standalone: false,
//   templateUrl: './fetch-employees-by-attendance-trend.component.html',
//   styleUrl: './fetch-employees-by-attendance-trend.component.css'
// })
// export class FetchEmployeesByAttendanceTrendComponent {

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
  
//     constructor(private attendanceReportService: AttendanceReportService) { }
  
//     ngOnInit(): void {
//     }
//   fetchEmployeesByAttendanceTrend(): void {
//     this.attendanceReportService.getEmployeesByAttendanceTrend(this.managerId!).subscribe(data => {
//       this.employeesByTrend = data;
//       this.trendKeys = Object.keys(this.employeesByTrend);
//     });
//   }
// }
import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-fetch-employees-by-attendance-trend',
  standalone:false,
  templateUrl: './fetch-employees-by-attendance-trend.component.html',
  styleUrls: ['./fetch-employees-by-attendance-trend.component.css']
})
export class FetchEmployeesByAttendanceTrendComponent implements OnInit, OnDestroy {

  employeesByTrend: { [key: string]: number[] } = {};
  trendKeys: string[] = [];
  managerId: number | null = null;
  chart: Chart | null = null;
  loading: boolean = false;
  error: string | null = null;

  private subscription: Subscription = new Subscription();
  @ViewChild('attendanceChartCanvas') attendanceChartCanvas: ElementRef | undefined; // Use ViewChild

  constructor(private attendanceReportService: AttendanceReportService) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    // No longer initialize chart here.  Do it in ngAfterViewInit
  }

  ngAfterViewInit(): void {
     this.createChart(); // Initialize chart.js
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    if (this.chart) {
      this.chart.destroy();
    }
  }

  fetchEmployeesByAttendanceTrend(): void {
    this.loading = true;
    this.error = null;
    if (this.managerId === null) {
      this.error = "Please enter a Manager ID.";
      this.loading = false;
      return;
    }

    this.subscription.add(
      this.attendanceReportService.getEmployeesByAttendanceTrend(this.managerId).subscribe({
        next: (data: { [x: string]: number[]; }) => {
          this.employeesByTrend = data;
          this.trendKeys = Object.keys(data);
          this.updateChart(); // Update the chart with fetched data
          this.loading = false;
        },
        error: (error: { message: string; }) => {
          this.error = error.message || "Failed to fetch data.";
          this.loading = false;
          this.employeesByTrend = {};
          this.trendKeys = [];
          this.updateChart(); //clear chart
        }
      })
    );
  }

  createChart(): void {
    if (!this.attendanceChartCanvas?.nativeElement) {
      console.error('Could not find canvas element');
      return;
    }
    const canvas = this.attendanceChartCanvas.nativeElement as HTMLCanvasElement;


    const chartConfig: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels: [],
        datasets: [{
          label: 'Number of Employees',
          data: [],
          backgroundColor: [
            'rgba(255, 99, 132, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)',
            'rgba(153, 102, 255, 0.6)',
            'rgba(255, 159, 64, 0.6)',
            'rgba(199, 21, 133, 0.6)'
          ],
          borderColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)',
            'rgba(255, 159, 64, 1)',
            'rgba(199, 21, 133, 1)'
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Employee Attendance Trends',
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
          }
        }
      }
    };

    this.chart = new Chart(canvas, chartConfig);
  }

  updateChart(): void {
    if (!this.chart) return;

    const labels = this.trendKeys;
    const data = this.trendKeys.map(key => this.employeesByTrend[key] ? this.employeesByTrend[key].length : 0);

    this.chart.data = {
      labels: labels,
      datasets: [{
        label: 'Number of Employees',
        data: data,
        backgroundColor: [
          'rgba(255, 99, 132, 0.6)',
          'rgba(54, 162, 235, 0.6)',
          'rgba(255, 206, 86, 0.6)',
          'rgba(75, 192, 192, 0.6)',
          'rgba(153, 102, 255, 0.6)',
          'rgba(255, 159, 64, 0.6)',
          'rgba(199, 21, 133, 0.6)'
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64, 1)',
          'rgba(199, 21, 133, 1)'
        ],
        borderWidth: 1
      }]
    };
    this.chart.update();
  }
}
