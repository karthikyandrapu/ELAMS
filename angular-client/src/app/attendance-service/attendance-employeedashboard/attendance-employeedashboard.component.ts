import { Component } from '@angular/core';

@Component({
  selector: 'app-attendance-employeedashboard',
  standalone: false,
  templateUrl: './attendance-employeedashboard.component.html',
  styleUrls: ['./attendance-employeedashboard.component.css']
})
export class AttendanceEmployeedashboardComponent {
  activeComponent: string = 'display-attendance'; // Default active component

  showComponent(componentName: string) {
    this.activeComponent = componentName;
  }
}