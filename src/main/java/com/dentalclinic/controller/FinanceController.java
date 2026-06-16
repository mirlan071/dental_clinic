package com.dentalclinic.controller;

import com.dentalclinic.controller.dto.common.ApiResponse;
import com.dentalclinic.controller.dto.common.PagedResponse;
import com.dentalclinic.controller.dto.finance.*;
import com.dentalclinic.domain.finance.Invoice;
import com.dentalclinic.service.FinanceService;
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

@Tag(name = "Finance", description = "Invoice and payment management endpoints")
@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @Operation(summary = "Create a new invoice")
    @PostMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceCreateRequest request) {
        InvoiceResponse invoice = financeService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoice created", invoice));
    }

    @Operation(summary = "Get invoice by ID")
    @GetMapping("/invoices/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable Long id) {
        InvoiceResponse invoice = financeService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    @Operation(summary = "Get invoices by patient")
    @GetMapping("/invoices/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceResponse>>> getInvoicesByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<InvoiceResponse> invoices = financeService.getInvoicesByPatient(patientId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toInvoicePagedResponse(invoices)));
    }

    @Operation(summary = "Get invoices by status")
    @GetMapping("/invoices/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceResponse>>> getInvoicesByStatus(
            @PathVariable Invoice.InvoiceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<InvoiceResponse> invoices = financeService.getInvoicesByStatus(status, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toInvoicePagedResponse(invoices)));
    }

    @Operation(summary = "Create a payment")
    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentCreateRequest request) {
        PaymentResponse payment = financeService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment created", payment));
    }

    @Operation(summary = "Get payments by invoice")
    @GetMapping("/payments/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> getPaymentsByInvoice(
            @PathVariable Long invoiceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PaymentResponse> payments = financeService.getPaymentsByInvoice(invoiceId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPaymentPagedResponse(payments)));
    }

    @Operation(summary = "Get payments by patient")
    @GetMapping("/payments/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> getPaymentsByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PaymentResponse> payments = financeService.getPaymentsByPatient(patientId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(toPaymentPagedResponse(payments)));
    }

    private PagedResponse<InvoiceResponse> toInvoicePagedResponse(Page<InvoiceResponse> page) {
        return PagedResponse.<InvoiceResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private PagedResponse<PaymentResponse> toPaymentPagedResponse(Page<PaymentResponse> page) {
        return PagedResponse.<PaymentResponse>builder()
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
