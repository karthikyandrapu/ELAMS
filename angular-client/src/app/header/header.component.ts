import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../service/auth/auth.service';

@Component({
  selector: 'app-header',
  standalone:false,
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  empRole: string | null = null; // Role of the logged-in user (EMPLOYEE or MANAGER)
  isLoggedOut: boolean = true; // By default, navbar is hidden

  constructor(private authService: AuthenticationService) {}
  // header.component.ts
  ngOnInit() {
    if (this.authService.isUserLoggedIn()) {
      this.empRole = sessionStorage.getItem('role');
      this.isLoggedOut = false;
    } else {
      this.isLoggedOut = true;
    }
  }

  logout() {
    // Call the logout method from AuthenticationService
    this.authService.logOut();
    this.empRole = null; // Clear the role
    this.isLoggedOut = true; // Set logged-out state
  }
}
