package com.dentalclinic.mapper;

import com.dentalclinic.controller.dto.doctor.DoctorCreateRequest;
import com.dentalclinic.controller.dto.doctor.DoctorResponse;
import com.dentalclinic.controller.dto.doctor.DoctorUpdateRequest;
import com.dentalclinic.controller.dto.doctor.WorkScheduleRequest;
import com.dentalclinic.controller.dto.doctor.WorkScheduleResponse;
import com.dentalclinic.domain.doctor.Doctor;
import com.dentalclinic.domain.doctor.WorkSchedule;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class})
public interface DoctorMapper {

    Doctor toEntity(DoctorCreateRequest request);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(target = "userId", expression = "java(doctor.getUser().getId())")
    @Mapping(target = "fullName", expression = "java(doctor.getFullName())")
    DoctorResponse toResponse(Doctor doctor);

    List<DoctorResponse> toResponseList(List<Doctor> doctors);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(DoctorUpdateRequest request, @MappingTarget Doctor doctor);

    WorkSchedule toScheduleEntity(WorkScheduleRequest request);

    WorkScheduleResponse toScheduleResponse(WorkSchedule schedule);

    List<WorkScheduleResponse> toScheduleResponseList(List<WorkSchedule> schedules);
}
