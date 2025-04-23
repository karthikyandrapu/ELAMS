import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../service/auth/auth.service';

@Component({
  selector: 'app-header',
  standalone:false,
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  empRole: string | null = null; 
  isLoggedOut: boolean = true;

  constructor(private authService: AuthenticationService) {}
  ngOnInit() {
    if (this.authService.isUserLoggedIn()) {
      this.empRole = sessionStorage.getItem('role');
      this.isLoggedOut = false;
    } else {
      this.isLoggedOut = true;
    }
  }

  logout() {
    this.authService.logOut();
    this.empRole = null;
    this.isLoggedOut = true;
  }
}
