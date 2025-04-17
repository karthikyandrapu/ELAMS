import { Component, OnInit } from '@angular/core';
import { AttendanceService } from 'src/app/service/attendance-service/attendance.service';
import { Attendance } from 'src/app/model/attendance-model/Attendance';

@Component({
  selector: 'app-viewyourattedancebydate',
  templateUrl: './viewyourattedancebydate.component.html',
  styleUrls: ['./viewyourattedancebydate.component.css'],
  standalone: false
})
export class ViewyourattedancebydateComponent implements OnInit {

  selectedDate: string = '';
  filteredAttendance: any[] = [];
  isFlipped: boolean = false;
  errorMessage: string = '';
  attendanceRecords: any[] = [];

  constructor(public attendanceService: AttendanceService) { }

  ngOnInit(): void {
  }

  loadAttendanceByDate(): void {
    this.attendanceRecords = this.attendanceService.getCurrentAttendance();
    this.filteredAttendance = [];
    this.errorMessage = '';
    
    if (!this.selectedDate) {
      this.errorMessage = 'Please select a date.';
      this.isFlipped = true;
      return;
    }
    
    // Check if attendance records are available
    if (!this.attendanceRecords || this.attendanceRecords.length === 0) {
      this.errorMessage = 'No attendance records available. Please check the attendance dashboard.';
      this.isFlipped = true;
      return;
    }
    
    console.log('Filtering attendance for date:', this.selectedDate);
    console.log('Available records:', this.attendanceRecords);
    
    // Format the selected date to compare with record dates
    const selectedDateObj = new Date(this.selectedDate);
    const selectedDateString = selectedDateObj.toISOString().split('T')[0]; // Format to YYYY-MM-DD
    
    // Filter attendance records for the selected date
    this.filteredAttendance = this.attendanceRecords.filter((record: any) => {
      if (record && record.clockInTime) {
        const recordDateObj = new Date(record.clockInTime);
        const recordDateString = recordDateObj.toISOString().split('T')[0];
        console.log(`Comparing dates: ${recordDateString} vs ${selectedDateString}`);
        return recordDateString === selectedDateString;
      }
      return false;
    });
    
    console.log('Filtered attendance:', this.filteredAttendance);
    
    this.isFlipped = true;
    
    if (this.filteredAttendance.length === 0) {
      this.errorMessage = `No attendance records found for ${this.formatDate(this.selectedDate)}.`;
    }
  }

  closeCard(): void {
    this.isFlipped = false;
  }
  
  // Format time from ISO string to readable time
  formatTime(timeString: string): string {
    if (!timeString) return 'Not recorded';
    const date = new Date(timeString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  }
  
  // Format date from ISO string to readable date
  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}