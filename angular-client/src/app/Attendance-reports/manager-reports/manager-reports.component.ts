
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { AttendanceReport } from 'src/app/model/AttendanceReport-model/attendanceReport.model';
import { AttendanceReportService } from 'src/app/service/AttendanceReport-service/attendance-report.service';
import { EmployeeserviceService } from 'src/app/service/employee-service/employee.service'; // Import the employee service
import { Employee } from 'src/app/model/employee-model/employee';

@Component({
  selector: 'app-manager-reports',
  standalone:false,
  templateUrl: './manager-reports.component.html',
  styleUrls: ['./manager-reports.component.css']
})
export class ManagerReportsComponent implements OnInit, AfterViewInit, OnDestroy{
  @ViewChild('attendanceChartCanvas') attendanceChartCanvas!: ElementRef<HTMLCanvasElement>;
  attendanceChart: Chart | undefined;
  selectedEmployeeId: number | null = null;
  employeeIds: number[] = []; // Array to store employee IDs for the dropdown
  employees: Employee[] = [];

  constructor(
    private attendanceReportService: AttendanceReportService,
    private employeeService: EmployeeserviceService // Inject the employee service
  ) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    this.fetchEmployeeIds(); // Fetch the employee IDs when the component initializes.
  }

  ngAfterViewInit(): void {
    // Check if canvas is available.  The chart will render when an employee is selected.
    if (!this.attendanceChartCanvas) {
      console.error('Canvas element is not available in AfterViewInit');
    }
  }

  ngOnDestroy(): void {
    if (this.attendanceChart) {
      this.attendanceChart.destroy();
    }
  }

  fetchEmployeeIds(): void {
    this.employeeService.getAllEmployees().subscribe(
      (employees: Employee[]) => {
        this.employees = employees;
        this.employeeIds = employees.map(employee => employee.employeeId);
        if (this.employeeIds && this.employeeIds.length > 0) {
          this.selectedEmployeeId = this.employeeIds[0]; // Initialize dropdown.
          this.fetchAndCreateChart();
        }
      },
      (error) => {
        console.error('Error fetching employees:', error);
        // Handle error appropriately (e.g., show a message to the user)
      }
    );
  }

  onEmployeeSelectionChange(event: any): void {
    this.selectedEmployeeId = parseInt(event.target.value, 10);
    this.fetchAndCreateChart();
  }

  fetchAndCreateChart(): void {
    if (this.selectedEmployeeId === null) {
      console.warn('No employee selected.');
      if (this.attendanceChart) {
        this.attendanceChart.destroy();
        this.attendanceChart = undefined;
      }
      return;
    }

    this.attendanceReportService.getReportsByEmployeeId(this.selectedEmployeeId).subscribe(
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
    // Sort reports by date
    reports.sort((a, b) => a.dateRange.localeCompare(b.dateRange));

    const dateRanges = reports.map((report) => report.dateRange);
    const totalAttendanceData = reports.map((report) => report.totalAttendance ?? 0);
    const absenteeismData = reports.map((report) => report.absenteeism ?? 0);

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
            text: `Attendance Report for Employee ${this.selectedEmployeeId}`,
            font: {
              size: 16,
            },
          },
          legend: {
            position: 'bottom',
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: function (value: string | number) {
                return value;
              },
            },
          },
        },
      },
    };

    if (this.attendanceChart) {
      this.attendanceChart.destroy();
    }
    this.attendanceChart = new Chart(
      this.attendanceChartCanvas.nativeElement,
      chartConfig
    );
  }
}
  
