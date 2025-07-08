package com.elams.mapper;

import com.elams.dtos.ShiftStatusDTO;
import com.elams.entities.ShiftStatus;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
/**
 * Mapper class for converting between {@link ShiftStatus} entity and {@link ShiftStatusDTO} data transfer object.
 */
@Component
public class ShiftStatusMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public ShiftStatusDTO toDTO(ShiftStatus shiftStatus) {
        return modelMapper.map(shiftStatus, ShiftStatusDTO.class);
    }

    public ShiftStatus toEntity(ShiftStatusDTO shiftStatusDTO) {
        return modelMapper.map(shiftStatusDTO, ShiftStatus.class);
    }
}