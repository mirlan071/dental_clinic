package com.dentalclinic.controller;

import com.dentalclinic.controller.dto.common.ApiResponse;
import com.dentalclinic.controller.dto.common.PagedResponse;
import com.dentalclinic.controller.dto.patient.PatientCreateRequest;
import com.dentalclinic.controller.dto.patient.PatientResponse;
import com.dentalclinic.controller.dto.patient.PatientUpdateRequest;
import com.dentalclinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patients", description = "Patient management endpoints")
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Create a new patient")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponse>> create(@Valid @RequestBody PatientCreateRequest request) {
        PatientResponse patient = patientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Patient created", patient));
    }

    @Operation(summary = "Get patient by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponse>> getById(@PathVariable Long id) {
        PatientResponse patient = patientService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(patient));
    }

    @Operation(summary = "Get all patients with pagination")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<PatientResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<PatientResponse> patients = patientService.getAll(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(patients)));
    }

    @Operation(summary = "Search patients")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<PatientResponse>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<PatientResponse> patients = patientService.search(query, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(patients)));
    }

    @Operation(summary = "Update patient")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientUpdateRequest request) {
        PatientResponse patient = patientService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Patient updated", patient));
    }

    @Operation(summary = "Delete (deactivate) patient")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Patient deactivated", null));
    }

    private PagedResponse<PatientResponse> toPagedResponse(Page<PatientResponse> page) {
        return PagedResponse.<PatientResponse>builder()
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
