package com.dentalclinic.controller.dto.appointment;

import jakarta.validation.constraints.*;
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
public class AppointmentCreateRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotEmpty(message = "At least one service must be selected")
    private List<Long> serviceIds;

    @NotNull(message = "Start time is required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime startTime;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;
}
