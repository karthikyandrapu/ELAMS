import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Shift } from '../../model/shift-model/shift';
import { environment } from 'src/environments/environment.development';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root',
})
export class ShiftserviceService {
  apiURL = environment.shiftUrl;

  constructor(private http: HttpClient) {}

  public assignShiftUrl = this.apiURL + '/assign';
  public getEmployeeShiftsUrl = this.apiURL + '/employee';
  public viewManagerShiftsUrl = this.apiURL + '/manager';
  public viewManagerOwnShiftsUrl = this.apiURL + '/manager';
  public requestSwapShiftUrl = this.apiURL + '/swap/request';
  public approveSwapShiftUrl = this.apiURL + '/swap';
  public rejectSwapShiftUrl = this.apiURL + '/swap';
  public updateShiftUrl = this.apiURL;
  public deleteShiftUrl = this.apiURL;
  public getColleaguesUrl = this.apiURL + '/colleagues';
  public viewManagerEmployeeShiftsUrl = this.apiURL + '/manager';
  public getManagerSwapRequestsUrl = this.apiURL + '/manager';
  public getUpcomingEmployeeShiftsUrl = this.apiURL + '/employee'; // Define URL for upcoming shifts
  public getEmployeeSwapRequestsUrl = this.apiURL + '/employee';
  public getEmployeeRejectedSwapRequestsUrl = this.apiURL + '/employee';
  public getEmployeeApprovedSwapRequestsUrl = this.apiURL + '/employee';

  public assignShift(shift: Shift, managerId: number): Observable<Shift> {
    const params = new HttpParams().set('managerId', managerId.toString());
    const headers = httpOptions.headers.set('role', 'MANAGER');
    return this.http.post<Shift>(this.assignShiftUrl, shift, {
      headers,
      params,
    });
  }

  public getEmployeeShifts(employeeId: number): Observable<Shift[]> {
    return this.http.get<Shift[]>(
      `${this.getEmployeeShiftsUrl}/${employeeId}`,
      httpOptions
    );
  }

  public viewManagerShifts(managerId: number): Observable<Shift[]> {
    const params = new HttpParams().set('managerId', managerId.toString());
    const headers = httpOptions.headers.set('role', 'MANAGER');
    return this.http.get<Shift[]>(
      `${this.viewManagerShiftsUrl}/${managerId}/employees`,
      { headers, params }
    );
  }

  public viewManagerOwnShifts(managerId: number): Observable<Shift[]> {
    const params = new HttpParams().set('managerId', managerId.toString());
    const headers = httpOptions.headers.set('role', 'MANAGER');
    return this.http.get<Shift[]>(
      `${this.viewManagerOwnShiftsUrl}/${managerId}`,
      { headers, params }
    );
  }

  public requestShiftSwap(
    employeeId: number,
    shiftId: number,
    swapWithEmployeeId: number
  ): Observable<Shift> {
    const params = new HttpParams()
      .set('employeeId', employeeId.toString())
      .set('shiftId', shiftId.toString())
      .set('swapWithEmployeeId', swapWithEmployeeId.toString());
    const headers = httpOptions.headers.set('role', 'EMPLOYEE');
    return this.http.post<Shift>(
      this.requestSwapShiftUrl,
      {},
      { headers, params }
    );
  }

  public approveShiftSwap(
    shiftId: number,
    managerId: number,
    swapWithEmployeeId: number
  ): Observable<Shift> {
    const params = new HttpParams()
      .set('managerId', managerId.toString())
      .set('swapWithEmployeeId', swapWithEmployeeId.toString());
    const headers = httpOptions.headers.set('role', 'MANAGER');
    return this.http.post<Shift>(
      `${this.approveSwapShiftUrl}/${shiftId}/approve`,
      {},
      { headers, params }
    );
  }

  public rejectShiftSwap(
    shiftId: number,
    managerId: number
  ): Observable<Shift> {
    const params = new HttpParams().set('managerId', managerId.toString());
    const headers = httpOptions.headers.set('role', 'MANAGER');
    return this.http.post<Shift>(
      `${this.rejectSwapShiftUrl}/${shiftId}/reject`,
      {},
      { headers, params }
    );
  }

  public updateShift(
    shiftId: number,
    managerId: number,
    shift: Shift
  ): Observable<Shift> {
    const params = new HttpParams().set('managerId', managerId.toString());
    const headers = httpOptions.headers.set('role', 'MANAGER');
    return this.http.put<Shift>(
      `${this.updateShiftUrl}/${shiftId}/update`,
      shift,
      { headers, params }
    );
  }

  public deleteShift(shiftId: number, managerId: number): Observable<void> {
    const params = new HttpParams().set('managerId', managerId.toString());
    const headers = httpOptions.headers.set('role', 'MANAGER');
    return this.http.delete<void>(`${this.deleteShiftUrl}/${shiftId}/delete`, {
      headers,
      params,
    });
  }

  public getColleagueShifts(
    employeeId: number,
    shiftDate: string
  ): Observable<Shift[]> {
    const params = new HttpParams().set('shiftDate', shiftDate); // Set shiftDate as a query parameter
    const headers = httpOptions.headers.set('role', 'EMPLOYEE'); // Add the required 'role' header
    return this.http.get<Shift[]>(`${this.getColleaguesUrl}/${employeeId}`, {
      headers,
      params,
    });
  }

  public viewManagerEmployeeShifts(
    managerId: number,
    employeeId: number
  ): Observable<Shift[]> {
    const headers = httpOptions.headers.set('role', 'MANAGER');
    return this.http.get<Shift[]>(
      `${this.viewManagerEmployeeShiftsUrl}/${managerId}/employee/${employeeId}`,
      { headers }
    );
  }

  public getManagerSwapRequests(managerId: number): Observable<Shift[]> {
    const headers = httpOptions.headers.set('role', 'MANAGER');
    return this.http.get<Shift[]>(
      `${this.getManagerSwapRequestsUrl}/${managerId}/swap-requests`,
      { headers }
    );
  }

  // New method to get upcoming shifts for an employee
  public getUpcomingEmployeeShifts(employeeId: number): Observable<Shift[]> {
    const headers = httpOptions.headers.set('role', 'EMPLOYEE');
    return this.http.get<Shift[]>(
      `${this.getUpcomingEmployeeShiftsUrl}/${employeeId}/upcoming`,
      { headers }
    );
  }

  // New methods for employee swap requests
  public viewEmployeeSwapRequests(employeeId: number): Observable<Shift[]> {
    const headers = httpOptions.headers.set('role', 'EMPLOYEE');
    return this.http.get<Shift[]>(
      `${this.getEmployeeSwapRequestsUrl}/${employeeId}/swap/requests`,
      { headers }
    );
  }

  public viewEmployeeRejectedSwapRequests(employeeId: number): Observable<Shift[]> {
    const headers = httpOptions.headers.set('role', 'EMPLOYEE');
    return this.http.get<Shift[]>(
      `${this.getEmployeeRejectedSwapRequestsUrl}/${employeeId}/swap/rejected`,
      { headers }
    );
  }

  public viewEmployeeApprovedSwapRequests(employeeId: number): Observable<Shift[]> {
    const headers = httpOptions.headers.set('role', 'EMPLOYEE');
    return this.http.get<Shift[]>(
      `${this.getEmployeeApprovedSwapRequestsUrl}/${employeeId}/swap/approved`,
      { headers }
    );
  }
}