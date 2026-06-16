package com.dentalclinic.mapper;

import com.dentalclinic.controller.dto.service.DentalServiceCreateRequest;
import com.dentalclinic.controller.dto.service.DentalServiceResponse;
import com.dentalclinic.controller.dto.service.DentalServiceUpdateRequest;
import com.dentalclinic.domain.service.DentalService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DentalServiceMapper {

    DentalService toEntity(DentalServiceCreateRequest request);

    DentalServiceResponse toResponse(DentalService service);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(DentalServiceUpdateRequest request, @MappingTarget DentalService service);
}
