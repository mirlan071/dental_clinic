package com.dentalclinic.mapper;

import com.dentalclinic.controller.dto.medicalrecord.DocumentAttachmentResponse;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordCreateRequest;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordResponse;
import com.dentalclinic.domain.medicalrecord.DocumentAttachment;
import com.dentalclinic.domain.medicalrecord.MedicalRecord;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MedicalRecordMapper {

    MedicalRecord toEntity(MedicalRecordCreateRequest request);

    @Mapping(target = "patientId", expression = "java(record.getPatient().getId())")
    @Mapping(target = "patientName", expression = "java(record.getPatient().getFullName())")
    @Mapping(target = "doctorId", expression = "java(record.getDoctor().getId())")
    @Mapping(target = "doctorName", expression = "java(record.getDoctor().getFullName())")
    @Mapping(target = "appointmentId", expression = "java(record.getAppointment() != null ? record.getAppointment().getId() : null)")
    MedicalRecordResponse toResponse(MedicalRecord record);

    DocumentAttachmentResponse toDocumentResponse(DocumentAttachment document);

    List<DocumentAttachmentResponse> toDocumentResponseList(List<DocumentAttachment> documents);
}
