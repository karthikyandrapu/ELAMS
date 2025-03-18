package com.elams.modelmapper;

import com.elams.dto.LeaveRequestDTO;
import com.elams.entities.LeaveRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public LeaveRequestDTO toDTO(LeaveRequest leaveRequest) {
        return modelMapper.map(leaveRequest, LeaveRequestDTO.class);
    }

    public LeaveRequest toEntity(LeaveRequestDTO leaveRequestDTO) {
        return modelMapper.map(leaveRequestDTO, LeaveRequest.class);
    }

}
