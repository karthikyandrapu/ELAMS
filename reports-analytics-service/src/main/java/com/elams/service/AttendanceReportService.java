package com.elams.service;

import java.util.List;
import java.util.Map;

import com.elams.dto.AttendanceReportDTO;
import com.elams.dto.AttendanceTrendsDTO;
import com.elams.entities.AttendanceReport;
import com.elams.entities.LeaveType;
import com.elams.enums.EmployeeRole;
import com.elams.exception.ResourceNotFoundException;

public interface AttendanceReportService {
	List<AttendanceReport> getAttendanceReports() throws ResourceNotFoundException;

    AttendanceReport getAttendanceReportById(Long employeeId) throws ResourceNotFoundException;

    AttendanceReport createAttendanceReport(AttendanceReport attendanceReport) throws ResourceNotFoundException;

    boolean deleteAttendanceReport(Long employeeId) throws ResourceNotFoundException;
    boolean deleteAllAttendanceReportsByEmployeeId(Long employeeId) throws ResourceNotFoundException;
    boolean updateAttendanceReport(Long employeeId, int totalAttendance, int absenteeism); 
    AttendanceReportDTO calculateAttendanceReport(Long employeeId, String dateRange,LeaveType leaveType,EmployeeRole role);
    public AttendanceTrendsDTO calculateAttendanceTrends(Long employeeId, String dateRange);
    public List<AttendanceReport> getAttendanceReportsById(Long employeeId) throws ResourceNotFoundException;
    public void deleteTrendsByEmployeeId(Long employeeId);
    public Map<String, List<Long>> listEmployeesByAttendanceTrend(Long managerIdFromController);
       
	
}
