import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AssignshiftComponent } from './shift-service/assign-shift/assignshift/assignshift.component';
import { GetemployeeshiftsComponent } from './shift-service/get-employee-shifts/getemployeeshifts/getemployeeshifts.component';
import { ViewmanagershiftsComponent } from './shift-service/view-manager-shifts/viewmanagershifts/viewmanagershifts.component';
import { ViewmanagerownshiftsComponent } from './shift-service/view-manager-own-shits/viewmanagerownshifts/viewmanagerownshifts.component';
import { RequestshiftswapComponent } from './shift-service/request-shift-swap/requestshiftswap/requestshiftswap.component';
import { ApproveshiftswapComponent } from './shift-service/approve-shift-swap/approveshiftswap/approveshiftswap.component';
import { RejectshiftswapComponent } from './shift-service/reject-shift-swap/rejectshiftswap/rejectshiftswap.component';
import { UpdateshiftComponent } from './shift-service/update-shift/updateshift/updateshift.component';
import { DeleteshiftComponent } from './shift-service/delete-shift/deleteshift/deleteshift.component';
import { GetcolleagueshiftsComponent } from './shift-service/get-colleague-shits/getcolleagueshifts/getcolleagueshifts.component';
import { SignupComponent } from './auth-employee/signup/signup.component';
import { LoginComponent } from './auth-employee/login/login.component';
import { LogoutComponent } from './auth-employee/logout/logout.component';
import { ShiftServiceEmployeeComponent } from './shift-service/shift-service-employee/shift-service-employee/shift-service-employee.component';
import { ShiftServiceManagerComponent } from './shift-service/shift-service-manager/shift-service-manager/shift-service-manager.component';
import { HeaderComponent } from './header/header.component';
import { ManagerdashboardComponent } from './home/manager-ui/managerdashboard/managerdashboard.component';
import { EmployeedashboardComponent } from './home/employee-ui/employeedashboard/employeedashboard.component';
import { ShiftmanagerhomeComponent } from './shift-service/home/shift-managerhome/shiftmanagerhome/shiftmanagerhome.component';
import { ShiftemployeehomeComponent } from './shift-service/home/shift-employeehome/shiftemployeehome/shiftemployeehome.component';
import { AttendanceEmployeedashboardComponent } from './attendance-service/attendance-employeedashboard/attendance-employeedashboard.component';
import { AttendanceManagerdashboardComponent } from './attendance-service/attendance-managerdashboard/attendance-managerdashboard.component';
import { AttendanceReportComponent } from './Attendance-reports/attendance-report/attendance-report.component';
import { FetchReportsByEmployeeIdComponent } from './Attendance-reports/fetch-reports-by-employee-id/fetch-reports-by-employee-id.component';
import { FetchEmployeesByAttendanceTrendComponent } from './Attendance-reports/fetch-employees-by-attendance-trend/fetch-employees-by-attendance-trend.component';
import { FetchAttendanceTrendsComponent } from './Attendance-reports/fetch-attendance-trends/fetch-attendance-trends.component';
import { CalculateReportComponent } from './Attendance-reports/calculate-report/calculate-report.component';
import { DeleteAllReportsByEmployeeIdComponent } from './Attendance-reports/delete-all-reports-by-employee-id/delete-all-reports-by-employee-id.component';
import { ManagerReportsComponent } from './Attendance-reports/manager-reports/manager-reports.component';
import { FetchAllReportsComponent } from './Attendance-reports/fetch-all-reports/fetch-all-reports.component';
import { AttendancemanagerhomeComponent } from './Attendance-reports/attendance-managerhome/attendancemanagerhome/attendancemanagerhome.component';
import { LeaveManagerDashboardComponent } from './leave-balance-service/dashboards/leave-manager-dashboard/leave-manager-dashboard.component';
import { LeaveEmployeeDashboardComponent } from './leave-balance-service/dashboards/leave-employee-dashboard/leave-employee-dashboard.component';
import { RequestLeaveComponent } from './leave-balance-service/request-leave/request-leave.component';
import { LeaveStatusComponent } from './leave-balance-service/leave-status/leave-status.component';
import { ReviewLeaveComponent } from './leave-balance-service/review-leave/review-leave.component';
import { AllLeaveRequestsComponent } from './leave-balance-service/all-leave-requests/all-leave-requests.component';
import { GetLeaveRequestByEmployeeidComponent } from './leave-balance-service/get-leave-request-by-employeeid/get-leave-request-by-employeeid.component';
import { GetLeaveRequestByManageridComponent } from './leave-balance-service/get-leave-request-by-managerid/get-leave-request-by-managerid.component';
import { SwaprequestedComponent } from './shift-service/swap-requested/swaprequested/swaprequested.component';
import { SwaprejectedComponent } from './shift-service/swap-rejected/swaprejected/swaprejected.component';
import { SwapapprovedComponent } from './shift-service/swap-approved/swapapproved/swapapproved.component';
import { GetemplyeeshiftsbyidComponent } from './shift-service/get-employee-shifts-by-id/getemplyeeshiftsbyid/getemplyeeshiftsbyid.component';
import { ViewmanagerswaprequestsComponent } from './shift-service/view-manager-swap-requests/viewmanagerswaprequests/viewmanagerswaprequests.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'shiftservicemanager', component: ShiftServiceManagerComponent },
  { path: 'shiftserviceemployee', component: ShiftServiceEmployeeComponent },

  { path: 'signup', component: SignupComponent },

  { path: 'login', component: LoginComponent },
  { path: 'logout', component: LogoutComponent },
  {
    path: 'home',
    redirectTo: '/login',
    pathMatch: 'full',
  },
  { path: 'header', component: HeaderComponent },
  {
    path: 'manager-dashboard',
    component: ManagerdashboardComponent,
  },
  {
    path: 'employee-dashboard',
    component: EmployeedashboardComponent,
  },

  {
    path: 'assign-shift',
    component: AssignshiftComponent,
  },
  {
    path: 'get-employee-shifts',
    component: GetemployeeshiftsComponent,
  },
  {
    path: 'get-employee-shifts',
    component: GetemployeeshiftsComponent,
  },
  {
    path: 'view-manager-shifts',
    component: ViewmanagershiftsComponent,
  },
  {
    path: 'view-manager-own-shifts',
    component: ViewmanagerownshiftsComponent,
  },
  {
    path: 'request-shift',
    component: RequestshiftswapComponent,
  },
  {
    path: 'approve-shift-swap',
    component: ApproveshiftswapComponent,
  },
  {
    path: 'reject-shift-swap',
    component: RejectshiftswapComponent,
  },
  {
    path: 'update-shift',
    component: UpdateshiftComponent,
  },
  {
    path: 'delete-shift',
    component: DeleteshiftComponent,
  },
  {
    path: 'get-colleague-shifts',
    component: GetcolleagueshiftsComponent,
  },

  {
    path: 'shift-service-employee',
    component: ShiftServiceEmployeeComponent,
  },
  {
    path: 'shift-service-manager',
    component: ShiftServiceManagerComponent,
  },
  {
    path: 'getcolleagueshifts',
    component: GetcolleagueshiftsComponent,
  },
  { path: 'shift-swap-request', component: SwaprequestedComponent },
  { path: 'shift-swap-reject', component: SwaprejectedComponent },
  { path: 'shift-swap-approved', component: SwapapprovedComponent },
  {path:'view-employee-shifts',component:GetemplyeeshiftsbyidComponent},
  {path:'view-manager-swap-requests',component:ViewmanagerswaprequestsComponent},
  {
    path: 'attendance-employeedashboard',
    component: AttendanceEmployeedashboardComponent,
  },
  {
    path: 'attendance-managerdashboard',
    component: AttendanceManagerdashboardComponent,
  },

  { path: 'attendance-reports', component: AttendanceReportComponent },
  {
    path: 'fetch-reports-by-employee-id',
    component: FetchReportsByEmployeeIdComponent,
  },
  {
    path: 'delete-all-reports-by-employee-id',
    component: DeleteAllReportsByEmployeeIdComponent,
  },
  { path: 'calculate-report', component: CalculateReportComponent },
  {
    path: 'fetch-attendance-trends',
    component: FetchAttendanceTrendsComponent,
  },
  {
    path: 'fetch-employees-by-attendance-trend',
    component: FetchEmployeesByAttendanceTrendComponent,
  },
  { path: 'manager-reports', component: ManagerReportsComponent },
  { path: 'all-reports', component: FetchAllReportsComponent },
  { path: 'attendancehome', component: AttendancemanagerhomeComponent },
  {
    path: 'leaveservicemanager',
    component: LeaveManagerDashboardComponent,
  },
  {
    path: 'leaveserviceemployee',
    component: LeaveEmployeeDashboardComponent,
  },
  { path: 'request-leave', component: RequestLeaveComponent },

  { path: 'leave-status', component: LeaveStatusComponent },

  { path: 'review-leave', component: ReviewLeaveComponent },

  { path: 'all-leave-requests', component: AllLeaveRequestsComponent },

  {
    path: 'get-leave-request-by-employeeid',
    component: GetLeaveRequestByEmployeeidComponent,
  },

  {
    path: 'get-leave-request-by-managerid',
    component: GetLeaveRequestByManageridComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
