import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone:false,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'ELAMS';
  showSidebar = true;

  constructor(private router: Router) {
    // Listen to route changes and hide the sidebar on specific routes
    this.router.events.subscribe(() => {
      const currentRoute = this.router.url;
      // Hide sidebar on login and signup pages
      this.showSidebar = currentRoute !== '/login' && currentRoute !== '/signup';
    });
  }
}