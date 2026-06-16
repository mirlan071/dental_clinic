package com.dentalclinic.service;

import com.dentalclinic.controller.dto.finance.InvoiceCreateRequest;
import com.dentalclinic.controller.dto.finance.InvoiceResponse;
import com.dentalclinic.controller.dto.finance.PaymentCreateRequest;
import com.dentalclinic.controller.dto.finance.PaymentResponse;
import com.dentalclinic.domain.finance.Invoice;
import com.dentalclinic.domain.finance.Payment;
import com.dentalclinic.domain.patient.Patient;
import com.dentalclinic.exception.BusinessException;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.mapper.FinanceMapper;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.InvoiceRepository;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private FinanceMapper financeMapper;

    @InjectMocks
    private FinanceService financeService;

    private Patient patient;
    private Invoice invoice;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");

        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("INV-20240101-ABCDEF");
        invoice.setPatient(patient);
        invoice.setTotalAmount(new BigDecimal("500.00"));
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setStatus(Invoice.InvoiceStatus.UNPAID);
    }

    @Test
    void create_invoice_success() {
        InvoiceCreateRequest request = InvoiceCreateRequest.builder()
                .patientId(1L)
                .totalAmount(new BigDecimal("500.00"))
                .build();

        InvoiceResponse response = InvoiceResponse.builder()
                .id(1L)
                .invoiceNumber("INV-20240101-ABCDEF")
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(financeMapper.toInvoiceResponse(any(Invoice.class))).thenReturn(response);

        InvoiceResponse result = financeService.createInvoice(request);

        assertNotNull(result);
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void create_payment_exceeds_amount_throws() {
        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .invoiceId(1L)
                .amount(new BigDecimal("600.00"))
                .paymentMethod("CASH")
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(BusinessException.class, () -> financeService.createPayment(request));
    }

    @Test
    void create_payment_already_paid_throws() {
        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .invoiceId(1L)
                .amount(new BigDecimal("100.00"))
                .paymentMethod("CASH")
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(BusinessException.class, () -> financeService.createPayment(request));
    }

    @Test
    void create_payment_partial_updates_status() {
        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .invoiceId(1L)
                .amount(new BigDecimal("250.00"))
                .paymentMethod("CASH")
                .build();

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("250.00"));
        payment.setInvoice(invoice);

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .amount(new BigDecimal("250.00"))
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(financeMapper.toPaymentEntity(any(PaymentCreateRequest.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(financeMapper.toPaymentResponse(any(Payment.class))).thenReturn(response);

        financeService.createPayment(request);

        assertEquals(Invoice.InvoiceStatus.PARTIALLY_PAID, invoice.getStatus());
        assertEquals(new BigDecimal("250.00"), invoice.getPaidAmount());
    }

    @Test
    void create_payment_full_updates_to_paid() {
        invoice.setTotalAmount(new BigDecimal("250.00"));
        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .invoiceId(1L)
                .amount(new BigDecimal("250.00"))
                .paymentMethod("CASH")
                .build();

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("250.00"));
        payment.setInvoice(invoice);

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .amount(new BigDecimal("250.00"))
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(financeMapper.toPaymentEntity(any(PaymentCreateRequest.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(financeMapper.toPaymentResponse(any(Payment.class))).thenReturn(response);

        financeService.createPayment(request);

        assertEquals(Invoice.InvoiceStatus.PAID, invoice.getStatus());
    }
}
