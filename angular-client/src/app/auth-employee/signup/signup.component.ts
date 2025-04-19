import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { UserSignUp } from 'src/app/model/user-model/userSignUp';
import { AuthenticationService } from 'src/app/service/auth/auth.service';

@Component({
  selector: 'app-signup',
  standalone: false,
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent implements OnInit, OnDestroy {
  signupForm!: FormGroup; // Use the '!' non-null assertion operator
  userSignUp: UserSignUp = new UserSignUp();
  errorMessage: string = '';
  signUpSubscription: Subscription | undefined;
  usernamePattern = '^[a-zA-Z][a-zA-Z0-9._-]*$'; // Starts with alphabet, followed by allowed chars
  namePattern = '^[a-zA-Z\\s]*$'; // Allows only alphabets and spaces
  passwordPattern = '^(?=.*[^a-zA-Z0-9])(?=.*[a-zA-Z])(?=.*[0-9])([^\\s]+)$';
  minUsernameLength = 5;
  minPasswordLength = 8;
  minNameLength = 2;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router
  ) {}

  ngOnInit() {
    this.signupForm = this.formBuilder.group({
      empId: ['', [Validators.required, Validators.pattern('^[0-9]*$')]],
      empName: [
        '',
        [Validators.required, Validators.pattern(this.namePattern), Validators.minLength(this.minNameLength)],
      ],
      userName: [
        '',
        [
          Validators.required,
          Validators.pattern(this.usernamePattern),
          Validators.minLength(this.minUsernameLength),
        ],
      ],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(this.minPasswordLength),
          Validators.pattern(this.passwordPattern),
        ],
      ],
      empRole: ['EMPLOYEE'], // Default value
    });
  }

  handleRegistrationError(error: any) {
    console.error('Registration error (Component):', error);
    console.log('Error Object:', error);
    console.log('error.error:', error.error);

    this.errorMessage = 'Registration failed. Please check your details.'; // Default message

    if (error.error) {
      if (typeof error.error === 'object' && error.error.message) {
        console.log('error.error is object with message:', error.error.message);
        this.errorMessage = error.error.message;
      } else if (typeof error.error === 'string') {
        console.log('error.error is string:', error.error);
        this.errorMessage = error.error; // Assign the string directly
      } else {
        console.log('error.error is object but no message:', error.error);
      }
    } else {
      console.log('error.error is falsy');
      this.errorMessage = 'An unexpected error occurred during registration.';
    }
    console.log('this.errorMessage (final):', this.errorMessage);
  }

  register() {
    if (this.signupForm.valid) {
      this.userSignUp = this.signupForm.value; // Assign form values to userSignUp
      console.log('Submitting registration (Component):', this.userSignUp);

      this.signUpSubscription = this.authService
        .registerUser(this.userSignUp)
        .subscribe({
          next: (registrationSuccessful) => {
            console.log(
              'Registration successful (Component - Next Value):',
              registrationSuccessful
            );

            if (registrationSuccessful === true) {
              console.log('Navigation condition met (Component).');
              this.router
                .navigate(['/login'], {
                  queryParams: { registered: 'true' },
                })
                .then(() => {
                  console.log('Navigation to /login completed (Component).');
                })
                .catch((error) => {
                  console.error('Navigation error (Component):', error);
                });
            } else {
              this.handleRegistrationError(
                'Registration was not successful (backend did not return true)'
              );
            }
          },
          error: (error) => {
            this.handleRegistrationError(error);
          },
        });
    } else {
      // Mark all controls as touched to trigger validation messages
      Object.values(this.signupForm.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }

  ngOnDestroy(): void {
    if (this.signUpSubscription) {
      this.signUpSubscription.unsubscribe();
    }
  }
}