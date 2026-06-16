package com.dentalclinic.mapper;

import com.dentalclinic.controller.dto.patient.PatientCreateRequest;
import com.dentalclinic.controller.dto.patient.PatientResponse;
import com.dentalclinic.controller.dto.patient.PatientUpdateRequest;
import com.dentalclinic.domain.patient.Patient;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatientMapper {

    Patient toEntity(PatientCreateRequest request);

    PatientResponse toResponse(Patient patient);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(PatientUpdateRequest request, @MappingTarget Patient patient);
}
