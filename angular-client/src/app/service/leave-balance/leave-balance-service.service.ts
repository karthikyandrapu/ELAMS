// leave-request.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { LeaveBalance } from 'src/app/model/leave-model/LeaveBalance';



@Injectable({
  providedIn: 'root'
})
export class LeaveRequestService {
  getAllLeaveRequests() {
    throw new Error('Method not implemented.');
  }
  private apiUrl = 'http://localhost:8084/api/leave-balance'; // Your backend API

  constructor(private http: HttpClient) {}

  getLeaveBalancesOfEmployee(employeeId: number): Observable<LeaveBalance[]> {
    const params = new HttpParams().set('employeeId', employeeId.toString()); 
    console.log(params);
    return this.http.get<LeaveBalance[]>(`${this.apiUrl}/all`, { params });
  }
  createLeaveBalance(employeeId: number, leaveType: string, balance: number): Observable<LeaveBalance> {
    const params = new HttpParams()
      .set('employeeId', employeeId.toString())
      .set('leaveType', leaveType.toString())
      .set('balance', balance.toString());

    return this.http.post<LeaveBalance>(`${this.apiUrl}/create`, null, { params });
  }
  getLeaveBalance(employeeId: number, leaveType: string): Observable<number> {
    const params = new HttpParams()
      .set('employeeId', employeeId.toString())
      .set('leaveType', leaveType.toString());

    return this.http.get<any>(`${this.apiUrl}/getBalance`, { params })
      

  }
}
