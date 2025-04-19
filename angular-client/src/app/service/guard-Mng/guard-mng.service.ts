import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class GuardMngService {
  constructor(private router: Router, private userService: AuthenticationService) { }
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
      if (this.userService.isUserLoggedIn() && this.userService.isUserAdmin()) {
          console.log("return true:");
          return true;
      } else if (this.userService.isUserLoggedIn() && !this.userService.isUserAdmin()) {
          alert("Not authorized");
          return false;
      }
      else {
          this.router.navigate(['login']);
          return false;
      }
  }
}
