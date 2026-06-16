package com.dentalclinic.controller;

import com.dentalclinic.controller.dto.common.ApiResponse;
import com.dentalclinic.controller.dto.common.PagedResponse;
import com.dentalclinic.controller.dto.service.DentalServiceCreateRequest;
import com.dentalclinic.controller.dto.service.DentalServiceResponse;
import com.dentalclinic.controller.dto.service.DentalServiceUpdateRequest;
import com.dentalclinic.service.DentalServiceService;
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

@Tag(name = "Dental Services", description = "Dental service catalog management endpoints")
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class DentalServiceController {

    private final DentalServiceService dentalServiceService;

    @Operation(summary = "Create a new dental service")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DentalServiceResponse>> create(
            @Valid @RequestBody DentalServiceCreateRequest request) {
        DentalServiceResponse service = dentalServiceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service created", service));
    }

    @Operation(summary = "Get service by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<DentalServiceResponse>> getById(@PathVariable Long id) {
        DentalServiceResponse service = dentalServiceService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(service));
    }

    @Operation(summary = "Get all dental services")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<DentalServiceResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<DentalServiceResponse> services = dentalServiceService.getAll(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(services)));
    }

    @Operation(summary = "Search dental services")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<DentalServiceResponse>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<DentalServiceResponse> services = dentalServiceService.search(query, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(services)));
    }

    @Operation(summary = "Get services by category")
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<DentalServiceResponse>>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<DentalServiceResponse> services = dentalServiceService.getByCategory(category, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPagedResponse(services)));
    }

    @Operation(summary = "Update dental service")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DentalServiceResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody DentalServiceUpdateRequest request) {
        DentalServiceResponse service = dentalServiceService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Service updated", service));
    }

    @Operation(summary = "Delete (deactivate) dental service")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        dentalServiceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Service deactivated", null));
    }

    private PagedResponse<DentalServiceResponse> toPagedResponse(Page<DentalServiceResponse> page) {
        return PagedResponse.<DentalServiceResponse>builder()
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
