// login.component.ts
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthenticationService } from '../../service/auth/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone:false,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  invalidLogin = false;
  authSubscription: Subscription | undefined;
  isAuthenticatedSubscription: Subscription | undefined;

  constructor(
    private router: Router,
    private loginservice: AuthenticationService,
    private fb: FormBuilder
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  ngOnInit() {
    // Check if the user is already logged in on component initialization
    if (this.loginservice.isUserLoggedIn()) {
      this.navigateToDashboard();
    }
  }

  checkLogin() {
    if (this.loginForm.valid) {
      const username = this.loginForm.value.username;
      const password = this.loginForm.value.password;

      this.authSubscription = this.loginservice
        .authenticate(username, password)
        .subscribe({
          error: (error) => {
            console.error('Login error:', error);
            this.invalidLogin = true;
            this.loginForm.reset();
          },
          complete: () => {
            console.log('Authentication Observable completed.');
          },
        });

      this.isAuthenticatedSubscription =
        this.loginservice.isAuthenticated$.subscribe((isAuthenticated) => {
          console.log(
            'isAuthenticated received in LoginComponent:',
            isAuthenticated
          );
          if (isAuthenticated) {
            this.invalidLogin = false;
            console.log('Navigating to dashboard from LoginComponent');
            this.navigateToDashboard();
          } else if (this.authSubscription && !this.authSubscription.closed) {
            this.invalidLogin = true;
            this.loginForm.reset();
          }
        });
    } else {
      // Mark all controls as touched to trigger validation messages
      Object.values(this.loginForm.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }

  navigateToDashboard() {
    if (this.loginservice.isUserNotAdmin()) {
      console.log('navigating to employee dashboard');
      this.router.navigate(['/employee-dashboard']); // Assuming you have a route for the employee dashboard
    } else {
      console.log('navigating to manager dashboard');
      this.router.navigate(['/manager-dashboard']); // Assuming you have a route for the manager dashboard
    }
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
    if (this.isAuthenticatedSubscription) {
      this.isAuthenticatedSubscription.unsubscribe();
    }
  }
}