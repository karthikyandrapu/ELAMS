import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AssignshiftComponent } from './shift-service/assign-shift/assignshift/assignshift.component';
import { GetemployeeshiftsComponent } from './shift-service/get-employee-shifts/getemployeeshifts/getemployeeshifts.component';
import { ViewmanagershiftsComponent } from './shift-service/view-manager-shifts/viewmanagershifts/viewmanagershifts.component';
import { ViewmanagerownshiftsComponent } from './shift-service/view-manager-own-shits/viewmanagerownshifts/viewmanagerownshifts.component';
import { UpdateshiftComponent } from './shift-service/update-shift/updateshift/updateshift.component';
import { GetcolleagueshiftsComponent } from './shift-service/get-colleague-shits/getcolleagueshifts/getcolleagueshifts.component';
import { SignupComponent } from './auth-employee/signup/signup.component';
import { LoginComponent } from './auth-employee/login/login.component';
import { LogoutComponent } from './auth-employee/logout/logout.component';
import { ShiftServiceEmployeeComponent } from './shift-service/shift-service-employee/shift-service-employee/shift-service-employee.component';
import { ShiftServiceManagerComponent } from './shift-service/shift-service-manager/shift-service-manager/shift-service-manager.component';
import { HeaderComponent } from './header/header.component';
import { ManagerdashboardComponent } from './home/manager-ui/managerdashboard/managerdashboard.component';
import { EmployeedashboardComponent } from './home/employee-ui/employeedashboard/employeedashboard.component';
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
import { LeaveHomeComponent } from './leave-balance-service/leave-home-page/leave-home/leave-home.component';
import { LeaveHomeManagerComponent } from './leave-balance-service/leave-home-page/leave-home-manager/leave-home-manager.component';
import { RequestLeaveManagerComponent } from './leave-balance-service/request-leave-manager/request-leave-manager.component';
import { GetLeaveRequestsManagerComponent } from './leave-balance-service/get-leave-requests-manager/get-leave-requests-manager.component';
import { AddLeaveBalanceComponent } from './leave-balance-service/add-leave-balance/add-leave-balance.component';
import { GuardEmpService } from './service/guard-Emp/guard-emp.service';
import { GuardMngService } from './service/guard-Mng/guard-mng.service';
import { SwapWithAnotherEmployeeComponent } from './shift-service/swap-with-another-employee/swap-with-another-employee.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'shiftservicemanager', component: ShiftServiceManagerComponent ,canActivate: [GuardMngService]},
  { path: 'shiftserviceemployee', component: ShiftServiceEmployeeComponent,canActivate: [GuardEmpService] },

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
    component: ManagerdashboardComponent, canActivate: [GuardMngService]
  },
  {
    path: 'employee-dashboard',
    component: EmployeedashboardComponent, canActivate: [GuardEmpService]
  },

  {
    path: 'assign-shift',
    component: AssignshiftComponent, canActivate: [GuardMngService]
  },
  {
    path: 'get-employee-shifts',
    component: GetemployeeshiftsComponent,canActivate: [GuardEmpService]
  },
  {
    path: 'get-employee-shifts',
    component: GetemployeeshiftsComponent,canActivate: [GuardEmpService]
  },
  {
    path: 'view-manager-shifts',
    component: ViewmanagershiftsComponent, canActivate: [GuardMngService]
  },
  {
    path: 'view-manager-own-shifts',
    component: ViewmanagerownshiftsComponent,  canActivate: [GuardMngService]
  },
 
 
  {
    path: 'update-shift',
    component: UpdateshiftComponent, canActivate: [GuardMngService]
  },
  
  {
    path: 'get-colleague-shifts',
    component: GetcolleagueshiftsComponent,canActivate: [GuardEmpService]
  },

  {
    path: 'shift-service-employee',
    component: ShiftServiceEmployeeComponent,canActivate: [GuardEmpService]
  },
  {
    path: 'shift-service-manager',
    component: ShiftServiceManagerComponent, canActivate: [GuardMngService]
  },
  {
    path: 'getcolleagueshifts',
    component: GetcolleagueshiftsComponent,canActivate: [GuardEmpService]
  },
  { path: 'shift-swap-request', component: SwaprequestedComponent,canActivate: [GuardEmpService] },
  { path: 'shift-swap-reject', component: SwaprejectedComponent,canActivate: [GuardEmpService] },
  { path: 'shift-swap-approved', component: SwapapprovedComponent,canActivate: [GuardEmpService] },
  {path:'view-employee-shifts',component:GetemplyeeshiftsbyidComponent, canActivate: [GuardMngService]},
  {path:'view-manager-swap-requests',component:ViewmanagerswaprequestsComponent , canActivate: [GuardMngService]},
  {path:'swap-with-another-employee',component:SwapWithAnotherEmployeeComponent,canActivate: [GuardEmpService]},
  {
    path: 'attendance-employeedashboard',
    component: AttendanceEmployeedashboardComponent,canActivate: [GuardEmpService]
  },
  {
    path: 'attendance-managerdashboard',
    component: AttendanceManagerdashboardComponent,canActivate: [GuardMngService]
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
  { path: 'attendancehome', component: AttendancemanagerhomeComponent ,canActivate: [GuardMngService]},
  {
    path: 'leaveservicemanager',
    component: LeaveManagerDashboardComponent,canActivate: [GuardMngService]
  },
  {
    path: 'leaveserviceemployee',
    component: LeaveEmployeeDashboardComponent,canActivate: [GuardEmpService]
  },
  { path: 'request-leave', component: RequestLeaveComponent , canActivate: [GuardEmpService]},

  { path: 'leave-status', component: LeaveStatusComponent ,canActivate: [GuardEmpService]},

  { path: 'review-leave', component: ReviewLeaveComponent, canActivate: [GuardMngService]},

  { path: 'all-leave-requests', component: AllLeaveRequestsComponent ,canActivate: [GuardMngService]},

  {
    path: 'get-leave-request-by-employeeid',
    component: GetLeaveRequestByEmployeeidComponent,canActivate: [GuardEmpService]
  },

  {
    path: 'get-leave-request-by-managerid',
    component: GetLeaveRequestByManageridComponent,
    canActivate: [GuardMngService],
  },
  {
    path:'leave-home',
    component: LeaveHomeComponent,canActivate: [GuardEmpService]

  },
  {
    path:'leave-home-manager',
    component:  LeaveHomeManagerComponent,canActivate: [GuardMngService]

  },
  {
    path:'request-leave-manager',
    component:  RequestLeaveManagerComponent,canActivate: [GuardMngService]

  },
  {
    path: 'get-leave-request-manager',
    component: GetLeaveRequestsManagerComponent,canActivate: [GuardMngService]
  },
  {
    path: 'add-leave-balance',
    component:AddLeaveBalanceComponent,canActivate: [GuardMngService]
  },


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
