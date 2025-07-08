create database employee_db2; 
create database employee_db;
create database attendance_db;
create database leavebalancedb;
create database leaveRequest;
create database EmployeeAttendanceReport;
create database shift_db;

use employee_db2; 
use employee_db;
use attendance_db;
use leavebalancedb;
use leaveRequest;
use EmployeeAttendanceReport;
use shift_db;

SELECT * FROM employee_db2.emp_users;
SELECT * FROM employee_db.employee;
SELECT * FROM attendance_db.attendance;
SELECT * FROM leavebalancedb.leave_balance;
SELECT * FROM leaverequest.leave_request;
SELECT * FROM shift_db.shift_management_table;
SELECT * FROM shift_db.shift_status;


drop database employee_db2; 
drop database employee_db;
drop database attendance_db;
drop database leavebalancedb;
drop database leaveRequest;
drop database EmployeeAttendanceReport;
drop database shift_db;