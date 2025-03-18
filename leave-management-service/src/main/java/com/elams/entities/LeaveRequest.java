package com.elams.entities;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@Table(name="leaveRequest")
@Entity
@AllArgsConstructor
public class LeaveRequest {
    
	@Id
	@Column(name="leave_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long leaveId;
	@NotNull(message = "Employee ID cannot be null")
	@Column(name="employee_id")
	private Long employeeId;
	@NotNull(message = "LeaveType cannot be null")
	@Column(name="leave_type")
	@Enumerated(EnumType.STRING)
	private LeaveType  leaveType;
	@NotNull(message = "Start Date cannot be null")
	@Column(name="start_date")
	private LocalDate startDate;
	@NotNull(message = "End Date cannot be null")
	@Column(name="end_date")	
	private LocalDate  endDate;
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	private LeaveStatus status;
	
	@PrePersist
    @PreUpdate
    private void validateDates() {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End Date must be after Start Date");
        }
    }
}
	
	
	
	
	

