package com.dentalclinic.controller;

import com.dentalclinic.controller.dto.common.ApiResponse;
import com.dentalclinic.controller.dto.common.PagedResponse;
import com.dentalclinic.controller.dto.doctor.*;
import com.dentalclinic.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Doctors", description = "Doctor management endpoints")
@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary = "Create a new doctor")
    @PostMapping
    public ResponseEntity<ApiResponse<DoctorResponse>> create(@Valid @RequestBody DoctorCreateRequest request) {
        DoctorResponse doctor = doctorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor created", doctor));
    }

    @Operation(summary = "Get doctor by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponse>> getById(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(doctor));
    }

    @Operation(summary = "Get all doctors")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<DoctorResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<DoctorResponse> doctors = doctorService.getAll(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(doctors)));
    }

    @Operation(summary = "Search doctors")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<DoctorResponse>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<DoctorResponse> doctors = doctorService.search(query, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(doctors)));
    }

    @Operation(summary = "Get doctors by specialization")
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<ApiResponse<PagedResponse<DoctorResponse>>> getBySpecialization(
            @PathVariable String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<DoctorResponse> doctors = doctorService.getBySpecialization(specialization, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(doctors)));
    }

    @Operation(summary = "Update doctor")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateRequest request) {
        DoctorResponse doctor = doctorService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Doctor updated", doctor));
    }

    @Operation(summary = "Delete (deactivate) doctor")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor deactivated", null));
    }

    @Operation(summary = "Add work schedule for doctor")
    @PostMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<WorkScheduleResponse>> addSchedule(
            @PathVariable Long id,
            @Valid @RequestBody WorkScheduleRequest request) {
        WorkScheduleResponse schedule = doctorService.addSchedule(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Schedule added", schedule));
    }

    @Operation(summary = "Get doctor work schedule")
    @GetMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<List<WorkScheduleResponse>>> getSchedule(@PathVariable Long id) {
        List<WorkScheduleResponse> schedules = doctorService.getSchedule(id);
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }

    @Operation(summary = "Remove work schedule")
    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> removeSchedule(@PathVariable Long scheduleId) {
        doctorService.removeSchedule(scheduleId);
        return ResponseEntity.ok(ApiResponse.success("Schedule removed", null));
    }

    private PagedResponse<DoctorResponse> toPagedResponse(Page<DoctorResponse> page) {
        return PagedResponse.<DoctorResponse>builder()
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
