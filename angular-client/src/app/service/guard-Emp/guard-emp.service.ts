import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class GuardEmpService {

  constructor(private router: Router, private userService: AuthenticationService) { }
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
      if (this.userService.isUserLoggedIn() && this.userService.isUserNotAdmin()) {
          console.log("return true:");
          return true;
      } else if (this.userService.isUserLoggedIn() && !this.userService.isUserNotAdmin()) {
          alert("Not authorized");
          return false;
      }
      else {
          this.router.navigate(['login']);
          return false;
      }
  }
}
