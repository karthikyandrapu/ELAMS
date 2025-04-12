export interface AttendanceTrend {
    trendId: number;
    employeeId: number;
    dateRange: string;
    averageAttendancePercentage: number;
    averageAbsenteeismPercentage: number;
    overallTrend: string;
  }