import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AssignshiftComponent } from './shift-service/assign-shift/assignshift/assignshift.component';
import { ShiftserviceService } from './service/shift-service/shift.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EmployeeserviceService } from './service/employee-service/employee.service';
import { GetemployeeshiftsComponent } from './shift-service/get-employee-shifts/getemployeeshifts/getemployeeshifts.component';
import { ViewmanagershiftsComponent } from './shift-service/view-manager-shifts/viewmanagershifts/viewmanagershifts.component';
import { ViewmanagerownshiftsComponent } from './shift-service/view-manager-own-shits/viewmanagerownshifts/viewmanagerownshifts.component';
import { RequestshiftswapComponent } from './shift-service/request-shift-swap/requestshiftswap/requestshiftswap.component';
import { ApproveshiftswapComponent } from './shift-service/approve-shift-swap/approveshiftswap/approveshiftswap.component';
import { RejectshiftswapComponent } from './shift-service/reject-shift-swap/rejectshiftswap/rejectshiftswap.component';
import { UpdateshiftComponent } from './shift-service/update-shift/updateshift/updateshift.component';
import { DeleteshiftComponent } from './shift-service/delete-shift/deleteshift/deleteshift.component';
import { GetcolleagueshiftsComponent } from './shift-service/get-colleague-shits/getcolleagueshifts/getcolleagueshifts.component';
import { LoginComponent } from './auth-employee/login/login.component';
import { AuthenticationService } from './service/auth/auth.service';
import { SignupComponent } from './auth-employee/signup/signup.component';
import { LogoutComponent } from './auth-employee/logout/logout.component';
import { ShiftServiceEmployeeComponent } from './shift-service/shift-service-employee/shift-service-employee/shift-service-employee.component';
import { ShiftServiceManagerComponent } from './shift-service/shift-service-manager/shift-service-manager/shift-service-manager.component';
import { HeaderComponent } from './header/header.component';
import { ManagerdashboardComponent } from './home/manager-ui/managerdashboard/managerdashboard.component';
import { EmployeedashboardComponent } from './home/employee-ui/employeedashboard/employeedashboard.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ClockinComponent } from './attendance-service/clockin/clockin.component';
import { ClockoutComponent } from './attendance-service/clockout/clockout.component';
import { CountattendanceComponent } from './attendance-service/countattendance/countattendance.component';
import { GetattendanceforemployeeComponent } from './attendance-service/getattendanceforemployee/getattendanceforemployee.component';
import { GetattendanceforemployeeanddateComponent } from './attendance-service/getattendanceforemployeeanddate/getattendanceforemployeeanddate.component';
import { GetattendancefortodayComponent } from './attendance-service/getattendancefortoday/getattendancefortoday.component';
import { DisplayattendanceComponent } from './attendance-service/displayattendance/displayattendance.component';
import { AttendanceService } from './service/attendance-service/attendance.service';
import { AttendanceEmployeedashboardComponent } from './attendance-service/attendance-employeedashboard/attendance-employeedashboard.component';
import { AttendanceManagerdashboardComponent } from './attendance-service/attendance-managerdashboard/attendance-managerdashboard.component';
import { FetchAllReportsComponent } from './Attendance-reports/fetch-all-reports/fetch-all-reports.component';
import { AttendancemanagerhomeComponent } from './Attendance-reports/attendance-managerhome/attendancemanagerhome/attendancemanagerhome.component';
import { ManagerReportsComponent } from './Attendance-reports/manager-reports/manager-reports.component';
import { FetchEmployeesByAttendanceTrendComponent } from './Attendance-reports/fetch-employees-by-attendance-trend/fetch-employees-by-attendance-trend.component';
import { FetchReportsByEmployeeIdComponent } from './Attendance-reports/fetch-reports-by-employee-id/fetch-reports-by-employee-id.component';
import { FetchAttendanceTrendsComponent } from './Attendance-reports/fetch-attendance-trends/fetch-attendance-trends.component';
import { DeleteAllReportsByEmployeeIdComponent } from './Attendance-reports/delete-all-reports-by-employee-id/delete-all-reports-by-employee-id.component';
import { CalculateReportComponent } from './Attendance-reports/calculate-report/calculate-report.component';
import { AttendanceReportComponent } from './Attendance-reports/attendance-report/attendance-report.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatTableModule } from '@angular/material/table';
import { RequestLeaveComponent } from './leave-balance-service/request-leave/request-leave.component';
import { AttendanceReportService } from './service/AttendanceReport-service/attendance-report.service';
import { LeaveRequestService } from './service/leave-request-service/leave-request.service';
import { AllLeaveRequestsComponent } from './leave-balance-service/all-leave-requests/all-leave-requests.component';
import { LeaveEmployeeDashboardComponent } from './leave-balance-service/dashboards/leave-employee-dashboard/leave-employee-dashboard.component';
import { LeaveManagerDashboardComponent } from './leave-balance-service/dashboards/leave-manager-dashboard/leave-manager-dashboard.component';
import { GetLeaveRequestByEmployeeidComponent } from './leave-balance-service/get-leave-request-by-employeeid/get-leave-request-by-employeeid.component';
import { GetLeaveRequestByManageridComponent } from './leave-balance-service/get-leave-request-by-managerid/get-leave-request-by-managerid.component';
import { LeavemanagerhomeComponent } from './leave-balance-service/home/leavemanagerhome/leavemanagerhome.component';
import { LeaveemployeehomeComponent } from './leave-balance-service/home/leaveemployeehome/leaveemployeehome.component';
import { LeaveStatusComponent } from './leave-balance-service/leave-status/leave-status.component';
import { ReviewLeaveComponent } from './leave-balance-service/review-leave/review-leave.component';
import { UpcomingshiftsComponent } from './shift-service/upcoming-shifts/upcomingshifts/upcomingshifts.component';
import { SwapapprovedComponent } from './shift-service/swap-approved/swapapproved/swapapproved.component';
import { SwaprejectedComponent } from './shift-service/swap-rejected/swaprejected/swaprejected.component';
import { SwaprequestedComponent } from './shift-service/swap-requested/swaprequested/swaprequested.component';
import { GetemplyeeshiftsbyidComponent } from './shift-service/get-employee-shifts-by-id/getemplyeeshiftsbyid/getemplyeeshiftsbyid.component';
import { ViewmanagerswaprequestsComponent } from './shift-service/view-manager-swap-requests/viewmanagerswaprequests/viewmanagerswaprequests.component';
import { SidenavbarComponent } from './navbar/sidenavbar/sidenavbar.component';
<<<<<<< HEAD
import { ViewyourattedancebydateComponent } from './attendance-service/viewyourattedancebydate/viewyourattedancebydate.component';



=======
import { LayoutComponent } from './layouts/layout/layout.component';
import { ViewyourattedancebydateComponent } from './attendance-service/viewyourattedancebydate/viewyourattedancebydate.component';
import { ShiftmanagerhomeComponent } from './shift-service/shift-manager-home/shiftmanagerhome.component';
import { ShiftEmployeeHomeComponent } from './shift-service/shift-employee-home/shift-employee-home.component';
>>>>>>> abe3542a6b61b174854fa531819cdf99279dbed3
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    AssignshiftComponent,
    UpcomingshiftsComponent,
    GetemployeeshiftsComponent,
    ViewmanagershiftsComponent,
    ViewmanagerownshiftsComponent,
    RequestshiftswapComponent,
    ApproveshiftswapComponent,
    RejectshiftswapComponent,
    UpdateshiftComponent,
    DeleteshiftComponent,
    GetcolleagueshiftsComponent,
    SwapapprovedComponent,
    SwaprejectedComponent,
    SwaprequestedComponent,
    GetemplyeeshiftsbyidComponent,
    ViewmanagerswaprequestsComponent,
    LogoutComponent,
    ShiftServiceEmployeeComponent,
    ShiftServiceManagerComponent,
    HeaderComponent,
    ManagerdashboardComponent,
    EmployeedashboardComponent,
    ClockinComponent,
    ClockoutComponent,
    CountattendanceComponent,
    GetattendanceforemployeeComponent,
    GetattendanceforemployeeanddateComponent,
    GetattendancefortodayComponent,
    DisplayattendanceComponent,
    AttendanceEmployeedashboardComponent,
    AttendanceManagerdashboardComponent,
    AttendanceReportComponent,
    CalculateReportComponent,
    DeleteAllReportsByEmployeeIdComponent,
    FetchAttendanceTrendsComponent,
    FetchReportsByEmployeeIdComponent,
    FetchEmployeesByAttendanceTrendComponent,
    ManagerReportsComponent,
    AttendancemanagerhomeComponent,
    FetchAllReportsComponent,
    AllLeaveRequestsComponent,
    LeaveEmployeeDashboardComponent,
    LeaveManagerDashboardComponent,
    GetLeaveRequestByEmployeeidComponent,
    GetLeaveRequestByManageridComponent,
    LeavemanagerhomeComponent,
    LeaveemployeehomeComponent,
    LeaveStatusComponent,
    RequestLeaveComponent,
    ReviewLeaveComponent,
    SidenavbarComponent,
<<<<<<< HEAD
    GetattendanceforemployeeanddateComponent,
    ViewyourattedancebydateComponent
=======
    LayoutComponent,
    GetattendanceforemployeeanddateComponent,
    ViewyourattedancebydateComponent,
    ShiftmanagerhomeComponent,
    ShiftEmployeeHomeComponent,
>>>>>>> abe3542a6b61b174854fa531819cdf99279dbed3
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    NgbModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatDividerModule,
    MatCardModule,
    MatTableModule,
  ],
  providers: [
    ShiftserviceService,
    EmployeeserviceService,
    AuthenticationService,
    AttendanceService,
    AttendanceReportService,
    LeaveRequestService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
