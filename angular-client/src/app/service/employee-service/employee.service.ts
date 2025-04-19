import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Employee } from 'src/app/model/employee-model/employee';
import { environment } from 'src/environments/environment.development';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root',
})
export class EmployeeserviceService {
  private apiURL = environment.employeeUrl;

  constructor(private http: HttpClient) {}

  getEmployeeById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiURL}/${id}`, httpOptions);
  }

  getAllEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.apiURL, httpOptions);
  }

  getAllEmployeeIds(): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiURL}/employeeIds`).pipe(
      catchError((error) => {
        console.error('Error fetching employee IDs:', error);
        return throwError(() => new Error('Failed to fetch employee IDs.'));
      })
    );
  }

  findAllEmployeeIdsByManagerId(managerId: number): Observable<number[]> {
    return this.http
      .get<number[]>(`${this.apiURL}/employeeIds/${managerId}`)
      .pipe(
        catchError((error) => {
          console.error('Error fetching employee IDs by manager ID:', error);
          return throwError(
            () => new Error('Failed to fetch employee IDs by manager ID.')
          );
        })
      );
  }

  createEmployee(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.apiURL, employee, httpOptions);
  }

  updateEmployee(id: number, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(
      `${this.apiURL}/${id}`,
      employee,
      httpOptions
    );
  }

  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${id}`, httpOptions);
  }

  getEmployeesByManager(managerId: number): Observable<Employee[]> {
    return this.http.get<Employee[]>(
      `${this.apiURL}/manager/${managerId}`,
      httpOptions
    );
  }

  updateEmployeeManager(id: number, managerId: number): Observable<Employee> {
    const params = { managerId: managerId.toString() };
    return this.http.put<Employee>(
      `${this.apiURL}/${id}/manager`,
      {},
      { ...httpOptions, params }
    );
  }
}
