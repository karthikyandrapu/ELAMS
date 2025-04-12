import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/service/auth/auth.service';
 
@Component({
  selector: 'app-logout',
  standalone:false,
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent {
  constructor(
    private authentocationService: AuthenticationService,
    private router: Router) {
 
  }
 
  ngOnInit() {
    this.authentocationService.logOut();
    this.router.navigate(['login']);
  }
}
 
 