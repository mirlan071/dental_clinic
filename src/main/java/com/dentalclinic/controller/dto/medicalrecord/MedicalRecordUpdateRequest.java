package com.dentalclinic.controller.dto.medicalrecord;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordUpdateRequest {

    @Size(max = 5000, message = "Diagnosis must not exceed 5000 characters")
    private String diagnosis;

    @Size(max = 5000, message = "Treatment must not exceed 5000 characters")
    private String treatment;

    @Size(max = 2000, message = "Recommendations must not exceed 2000 characters")
    private String recommendations;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}
