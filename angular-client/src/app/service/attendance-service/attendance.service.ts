// src/app/attendance/attendance.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { ClockInOutRequest } from 'src/app/model/attendance-model/ClockInOutRequest';
import { Attendance } from 'src/app/model/attendance-model/Attendance';



@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private attendanceApiUrl = `${environment.attendanceApiUrl}/attendance`; // Correct path based on backend @RequestMapping
  private currentAttendanceRecord!:any;
<<<<<<< HEAD

=======
>>>>>>> abe3542a6b61b174854fa531819cdf99279dbed3

  constructor(private http: HttpClient) { }

  clockIn(employeeId: number): Observable<string> {
    const body: ClockInOutRequest = { employeeId };
    return this.http.post<string>(`${this.attendanceApiUrl}/clockin`, body, { responseType: 'text' as 'json' });
  }

  clockOut(employeeId: number): Observable<string> {
    const body: ClockInOutRequest = { employeeId };
    return this.http.post<string>(`${this.attendanceApiUrl}/clockout`, body, { responseType: 'text' as 'json' });
  }
  getOwnAttendance(employeeId: number, role: string): Observable<Attendance[]> {
    const headers = new HttpHeaders({
      'X-Employee-Role': role,
      'X-Employee-Id': employeeId.toString()
    });
    return this.http.get<Attendance[]>(`${this.attendanceApiUrl}/employee/${employeeId}`, { headers });
  }

  getEmployeeAttendance(employeeId: number, role: string, managerId: number | null): Observable<Attendance[]> {
    const headers = new HttpHeaders({
      'X-Employee-Role': role,
      'X-Employee-Id': managerId ? managerId.toString() : ''
    });
    return this.http.get<Attendance[]>(`${this.attendanceApiUrl}/employee/${employeeId}`, { headers });
  }

  getTodayAttendance(role: string, managerId: number | null): Observable<Attendance[]> {
    const headers = new HttpHeaders({
      'X-Employee-Role': role,
      'X-Employee-Id': managerId ? managerId.toString() : ''
    });
    return this.http.get<Attendance[]>(`${this.attendanceApiUrl}/today`, { headers });
  }

  getEmployeeAttendanceByDate(employeeId: number, date: string, role: string, managerId: number | null): Observable<Attendance[]> {
    const headers = new HttpHeaders({
      'X-Employee-Role': role,
      'X-Employee-Id': managerId ? managerId.toString() : ''
    });
    return this.http.get<Attendance[]>(`${this.attendanceApiUrl}/employee/${employeeId}/date/${date}`, { headers });
  }

  countEmployeeAttendance(employeeId: number, startDate: string, endDate: string): Observable<number> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<number>(`${this.attendanceApiUrl}/count?employeeId=${employeeId}`, { params });
  }

<<<<<<< HEAD
  //setting and getting current attendace data from displayattendance

=======
>>>>>>> abe3542a6b61b174854fa531819cdf99279dbed3
  setCurrentAttendance(attendanceBody:any){
    this.currentAttendanceRecord = attendanceBody;
  }

  getCurrentAttendance(){
    return this.currentAttendanceRecord;
  }
}