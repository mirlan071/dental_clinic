package com.dentalclinic.controller;

import com.dentalclinic.controller.dto.common.ApiResponse;
import com.dentalclinic.controller.dto.common.PagedResponse;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordCreateRequest;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordResponse;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordUpdateRequest;
import com.dentalclinic.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Medical Records", description = "Medical record management endpoints")
@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Operation(summary = "Create a new medical record")
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> create(
            @Valid @RequestBody MedicalRecordCreateRequest request) {
        MedicalRecordResponse record = medicalRecordService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medical record created", record));
    }

    @Operation(summary = "Get medical record by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getById(@PathVariable Long id) {
        MedicalRecordResponse record = medicalRecordService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @Operation(summary = "Get medical records by patient")
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalRecordResponse>>> getByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MedicalRecordResponse> records = medicalRecordService.getByPatient(patientId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(records)));
    }

    @Operation(summary = "Get medical records by doctor")
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalRecordResponse>>> getByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MedicalRecordResponse> records = medicalRecordService.getByDoctor(doctorId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(records)));
    }

    @Operation(summary = "Update medical record")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordUpdateRequest request) {
        MedicalRecordResponse record = medicalRecordService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Medical record updated", record));
    }

    @Operation(summary = "Delete medical record")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        medicalRecordService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Medical record deleted", null));
    }

    private PagedResponse<MedicalRecordResponse> toPagedResponse(Page<MedicalRecordResponse> page) {
        return PagedResponse.<MedicalRecordResponse>builder()
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
