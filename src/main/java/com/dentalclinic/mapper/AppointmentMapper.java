package com.dentalclinic.mapper;

import com.dentalclinic.controller.dto.appointment.AppointmentResponse;
import com.dentalclinic.domain.appointment.Appointment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {PatientMapper.class, DoctorMapper.class, DentalServiceMapper.class})
public interface AppointmentMapper {

    @Mapping(target = "status", expression = "java(appointment.getStatus().name())")
    AppointmentResponse toResponse(Appointment appointment);

    List<AppointmentResponse> toResponseList(List<Appointment> appointments);
}
