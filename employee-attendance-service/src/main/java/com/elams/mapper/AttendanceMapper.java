package com.elams.mapper;

import com.elams.dtos.AttendanceDTO;
import com.elams.entities.Attendance;
import com.elams.aop.AppLogger;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Attendance entity and AttendanceDTO.
 * This class utilizes ModelMapper to facilitate the mapping process.
 */
@Component
public class AttendanceMapper {

    private static final Logger logger = AppLogger.getLogger(AttendanceMapper.class);
    private final ModelMapper modelMapper = new ModelMapper();

    /**
     * Converts an Attendance entity to an AttendanceDTO.
     *
     * @param attendance The Attendance entity to convert.
     * @return The corresponding AttendanceDTO.
     */
    public AttendanceDTO toDTO(Attendance attendance) {
        logger.debug("Mapping Attendance entity to AttendanceDTO. Entity: {}", attendance);
        AttendanceDTO dto = modelMapper.map(attendance, AttendanceDTO.class);
        logger.debug("Mapped AttendanceDTO: {}", dto);
        return dto;
    }

    /**
     * Converts an AttendanceDTO to an Attendance entity.
     *
     * @param attendanceDTO The AttendanceDTO to convert.
     * @return The corresponding Attendance entity.
     */
    public Attendance toEntity(AttendanceDTO attendanceDTO) {
        logger.debug("Mapping AttendanceDTO to Attendance entity. DTO: {}", attendanceDTO);
        Attendance entity = modelMapper.map(attendanceDTO, Attendance.class);
        logger.debug("Mapped Attendance entity: {}", entity);
        return entity;
    }
}