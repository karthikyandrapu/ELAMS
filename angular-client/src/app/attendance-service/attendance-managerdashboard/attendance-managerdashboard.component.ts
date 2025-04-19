import { Component } from '@angular/core';

@Component({
  selector: 'app-attendance-managerdashboard',
  standalone: false,
  templateUrl: './attendance-managerdashboard.component.html',
  styleUrls: ['./attendance-managerdashboard.component.css']
})
export class AttendanceManagerdashboardComponent {
  activeComponent: string = 'display-attendance'; // Default active component

  showComponent(componentName: string) {
    this.activeComponent = componentName;
  }
}