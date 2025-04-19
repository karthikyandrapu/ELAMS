export interface AttendanceReport {
    employeeId : number | null ;
    dateRange : string;
    totalAttendance: number | null;
    absenteeism : number | null;
  }