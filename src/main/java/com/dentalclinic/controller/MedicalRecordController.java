package com.dentalclinic.controller;

import com.dentalclinic.controller.dto.common.ApiResponse;
import com.dentalclinic.controller.dto.common.PagedResponse;
import com.dentalclinic.controller.dto.medicalrecord.DocumentAttachmentResponse;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordCreateRequest;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordResponse;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordUpdateRequest;
import com.dentalclinic.domain.medicalrecord.DocumentAttachment;
import com.dentalclinic.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Medical Records", description = "Medical record management endpoints")
@RestController
@RequestMapping("/api/v1/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Operation(summary = "Get all medical records")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<MedicalRecordResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "200") int size) {
        Page<MedicalRecordResponse> records = medicalRecordService.getAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(records)));
    }

    @Operation(summary = "Create a new medical record")
    @PostMapping
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> create(
            @Valid @RequestBody MedicalRecordCreateRequest request) {
        MedicalRecordResponse record = medicalRecordService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medical record created", record));
    }

    @Operation(summary = "Get medical record by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getById(@PathVariable Long id) {
        MedicalRecordResponse record = medicalRecordService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @Operation(summary = "Get medical records by patient")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalRecordResponse>>> getByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MedicalRecordResponse> records = medicalRecordService.getByPatient(patientId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(records)));
    }

    @Operation(summary = "Get medical records by doctor")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalRecordResponse>>> getByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MedicalRecordResponse> records = medicalRecordService.getByDoctor(doctorId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(records)));
    }

    @Operation(summary = "Update medical record")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordUpdateRequest request) {
        MedicalRecordResponse record = medicalRecordService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Medical record updated", record));
    }

    @Operation(summary = "Delete medical record")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        medicalRecordService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Medical record deleted", null));
    }

    @Operation(summary = "Upload document to medical record")
    @PostMapping("/{id}/documents")
    public ResponseEntity<ApiResponse<DocumentAttachmentResponse>> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        DocumentAttachmentResponse attachment = medicalRecordService.uploadDocument(id, file, description);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Document uploaded", attachment));
    }

    @Operation(summary = "Download document")
    @GetMapping("/documents/{attachmentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long attachmentId) {
        DocumentAttachment attachment = medicalRecordService.getAttachmentInfo(attachmentId);
        Resource resource = medicalRecordService.downloadDocument(attachmentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType() != null ? attachment.getContentType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }

    @Operation(summary = "Delete document from medical record")
    @DeleteMapping("/documents/{attachmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long attachmentId) {
        medicalRecordService.deleteDocument(attachmentId);
        return ResponseEntity.ok(ApiResponse.success("Document deleted", null));
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
