// authentication.service.ts
import {
  HttpClient,
  HttpResponse,
  HttpErrorResponse,
} from '@angular/common/http'; // Import HttpErrorResponse
import { Injectable } from '@angular/core';
import {
  tap,
  catchError,
  of,
  Observable,
  Subject,
  map,
  throwError,
} from 'rxjs'; // Import throwError
import { User } from 'src/app/model/user-model/user';
import { UserSignUp } from 'src/app/model/user-model/userSignUp';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  isAuthenticatedSubject = new Subject<boolean>();
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {}
  authenticateURL: string = 'http://localhost:8089/authenticate/users';
  authenticatedSignUpURL: string =
    'http://localhost:8089/authenticate/register';
  user!: User;
  users!: User[];
  userBackend: User = new User();

  authenticate(username: string, password: string): Observable<boolean> {
    this.user = new User();
    this.user.userName = username;
    this.user.password = password;

    return this.getUser(this.user).pipe(
      tap((data: any) => {
        this.userBackend.userName = data.userName;
        this.userBackend.role = data.role;
        sessionStorage.setItem('username', this.userBackend.userName);
        sessionStorage.setItem('role', this.userBackend.role);
        sessionStorage.setItem('empId', data.empId);
        this.isAuthenticatedSubject.next(true);
      }),
      catchError((error) => {
        console.error('Authentication failed:', error);
        this.isAuthenticatedSubject.next(false);
        return of(false);
      })
    );
  }

  registerUser(signUpData: UserSignUp): Observable<boolean> {
    return this.http
      .post(this.authenticatedSignUpURL, signUpData, { observe: 'response' })
      .pipe(
        map((response: HttpResponse<any>) => {
          console.log(
            'Map operator executed (Service). Status:',
            response.status
          );
          return response.status === 201;
        }),
        catchError((error: HttpErrorResponse) => {
          // Type the error as HttpErrorResponse
          console.error('Registration error (Service):', error);
          return throwError(error); // Re-throw the error
        })
      );
  }

  getUser(User: any): Observable<any> {
    return this.http.post<any>(this.authenticateURL, User);
  }

  getLoggedInEmpId(): string | null {
    return sessionStorage.getItem('empId');
  }

  isUserLoggedIn() {
    let user = sessionStorage.getItem('username');
    let role = sessionStorage.getItem('role');
    return !(user === null && role === null);
  }
  isUserAdmin() {
    let role = sessionStorage.getItem('role');
    return role === 'MANAGER';
  }
  isUserNotAdmin() {
    let role = sessionStorage.getItem('role');
    return role === 'EMPLOYEE'; // Ensure this comparison is exact
  }

  logOut() {
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('role');
    sessionStorage.removeItem('empId');
    sessionStorage.clear();
    localStorage.clear();
  }
}
