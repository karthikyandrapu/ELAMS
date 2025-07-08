// leave-request.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LeaveRequestService {
  getAllLeaveRequests() {
    throw new Error('Method not implemented.');
  }
  private apiUrl = 'http://localhost:8052/leave-requests'; // Your backend API

  constructor(private http: HttpClient) {}

  requestLeave(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/request`, data); // Ensures a POST request
  }

  getLeaveRequestStatus(leaveId: number, employeeId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${leaveId}/status/${employeeId}`);
  }

  reviewLeaveRequest(leaveId: number, managerId: number, decision: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${leaveId}/review/${managerId}?decision=${decision}`, {}); // Empty body for PUT request
  }

  AllLeaveRequests(managerId: number): Observable<any[]> {
    const params = new HttpParams().set('managerId', managerId.toString());
    return this.http.get<any[]>(`${this.apiUrl}/manager`, { params });
  }
  
}
