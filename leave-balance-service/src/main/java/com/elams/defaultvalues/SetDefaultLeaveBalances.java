package com.elams.defaultvalues;

import org.springframework.stereotype.Component;

import com.elams.entities.LeaveType;

@Component
public class SetDefaultLeaveBalances {

	public Double getDefaultLeaveBalances(LeaveType leaveType)
	{
		switch(leaveType)
		{
		case SICK:
			return 5.0;
		case VACATION:
			return 8.0;
		case CASUAL:
			return 3.0;
		case OTHER:
			return 3.0;
		default:
			return 0.0;
			
		}
	}
}
