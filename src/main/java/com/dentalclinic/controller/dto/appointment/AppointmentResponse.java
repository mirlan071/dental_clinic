package com.dentalclinic.controller.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.dentalclinic.controller.dto.doctor.DoctorResponse;
import com.dentalclinic.controller.dto.patient.PatientResponse;
import com.dentalclinic.controller.dto.service.DentalServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentResponse {

    private Long id;
    private PatientResponse patient;
    private DoctorResponse doctor;
    private List<DentalServiceResponse> services;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
