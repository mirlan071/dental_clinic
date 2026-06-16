package com.dentalclinic.controller.dto.doctor;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorUpdateRequest {

    @Size(max = 150, message = "Specialization must not exceed 150 characters")
    private String specialization;

    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    @Size(max = 2000, message = "Biography must not exceed 2000 characters")
    private String biography;
}
