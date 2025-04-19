import { Injectable } from '@angular/core';
import { HttpClient ,HttpErrorResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AttendanceTrend } from '../../model/AttendanceReport-model/attendanceTrends.model';
import { AttendanceReport} from '../../model/AttendanceReport-model/attendanceReport.model';
import {  throwError } from 'rxjs';
import { catchError ,map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AttendanceReportService {

  
  private apiUrl = 'http://localhost:8090/api/attendance-reports';

  constructor(private http: HttpClient) { }

  getAllReports(): Observable<any> {
    return this.http.get(`${this.apiUrl}`);
  }

  getReportsByEmployeeId(employeeId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/reports/${employeeId}`);
  }

  createReport(reportData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, reportData);
  }

  updateReport(updateData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update`, updateData);
  }

  deleteReport(employeeId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${employeeId}`);
  }

  // deleteAllReportsByEmployeeId(employeeId: number): Observable<any> {
  //   return this.http.delete(`${this.apiUrl}/all/${employeeId}`);
  // }
  deleteAllReportsByEmployeeId(employeeId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/all/${employeeId}`).pipe(
      catchError(this.handleError1) // Use the error handler
    );
  }

  private handleError1(error: any) {
    if (error.status === 404) {
      return throwError(() => new Error('No attendance reports found.'));
    }
     else {
      return throwError(()=> new Error(`An error occurred: ${error.message}`));
    }
  }

  calculateReport(calculateData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/calculate`, calculateData);
  }

  
  getAttendanceTrends(employeeId: number, dateRange: string): Observable<AttendanceTrend> {
    return this.http.get<AttendanceTrend>(`${this.apiUrl}/trends/${employeeId}/${dateRange}`);
  }

  private handleError(error: HttpErrorResponse) {
    console.error('An error occurred:', error.message);
    return throwError('Something went wrong; please try again later.');
  }


  deleteEmployeeTrends(employeeId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/trends/${employeeId}`);
  }

  getEmployeesByAttendanceTrend(managerId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/employees-by-trend/${managerId}`);
  }
}

