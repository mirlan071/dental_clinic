package com.dentalclinic.controller.dto.doctor;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class DoctorResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String specialization;
    private String licenseNumber;
    private String biography;
    private boolean active;
    private List<WorkScheduleResponse> workSchedules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
