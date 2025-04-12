package com.elams.mapper;
 
import com.elams.dtos.ShiftDTO;
import com.elams.entities.Shift;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
/**
 * Mapper class for converting between {@link Shift} entity and {@link ShiftDTO} data transfer object.
 */
@Component
public class ShiftMapper {
    private final ModelMapper modelMapper = new ModelMapper();
 
    public ShiftDTO toDTO(Shift shift) {
        return modelMapper.map(shift, ShiftDTO.class);
    }
 
    public Shift toEntity(ShiftDTO shiftDTO) {
        return modelMapper.map(shiftDTO, Shift.class);
    }
}