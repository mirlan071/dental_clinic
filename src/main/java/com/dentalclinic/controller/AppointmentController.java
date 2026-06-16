package com.dentalclinic.controller;

import com.dentalclinic.controller.dto.appointment.AppointmentCreateRequest;
import com.dentalclinic.controller.dto.appointment.AppointmentResponse;
import com.dentalclinic.controller.dto.appointment.AppointmentUpdateRequest;
import com.dentalclinic.controller.dto.common.ApiResponse;
import com.dentalclinic.controller.dto.common.PagedResponse;
import com.dentalclinic.domain.appointment.Appointment;
import com.dentalclinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Appointments", description = "Appointment management endpoints")
@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Create a new appointment")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(@Valid @RequestBody AppointmentCreateRequest request) {
        AppointmentResponse appointment = appointmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment created", appointment));
    }

    @Operation(summary = "Get appointment by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getById(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(appointment));
    }

    @Operation(summary = "Get appointments by patient")
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<AppointmentResponse>>> getByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AppointmentResponse> appointments = appointmentService.getByPatient(patientId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(appointments)));
    }

    @Operation(summary = "Get appointments by doctor")
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<AppointmentResponse>>> getByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AppointmentResponse> appointments = appointmentService.getByDoctor(doctorId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(appointments)));
    }

    @Operation(summary = "Reschedule appointment")
    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> reschedule(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartTime) {
        AppointmentResponse appointment = appointmentService.reschedule(id, newStartTime);
        return ResponseEntity.ok(ApiResponse.success("Appointment rescheduled", appointment));
    }

    @Operation(summary = "Cancel appointment")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancel(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled", appointment));
    }

    @Operation(summary = "Update appointment status")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateRequest request) {
        AppointmentResponse appointment = appointmentService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated", appointment));
    }

    @Operation(summary = "Get appointments by status and date range")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<AppointmentResponse>>> search(
            @RequestParam Appointment.AppointmentStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AppointmentResponse> appointments = appointmentService.getByStatusAndDateRange(
                status, start, end, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(appointments)));
    }

    private PagedResponse<AppointmentResponse> toPagedResponse(Page<AppointmentResponse> page) {
        return PagedResponse.<AppointmentResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
