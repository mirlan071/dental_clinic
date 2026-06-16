package com.dentalclinic.controller.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
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
public class AppointmentUpdateRequest {

    @Future(message = "Appointment must be in the future")
    private LocalDateTime startTime;

    private List<Long> serviceIds;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;

    private String status;
}
