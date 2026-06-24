package com.dentalclinic.service;

import com.dentalclinic.controller.dto.finance.InvoiceCreateRequest;
import com.dentalclinic.controller.dto.finance.InvoiceResponse;
import com.dentalclinic.controller.dto.finance.PaymentCreateRequest;
import com.dentalclinic.controller.dto.finance.PaymentResponse;
import com.dentalclinic.domain.appointment.Appointment;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final FinanceMapper financeMapper;
    private final NotificationService notificationService;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceCreateRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId()));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment", request.getAppointmentId()));
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setPatient(patient);
        invoice.setAppointment(appointment);
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setNotes(request.getNotes());
        invoice.setStatus(Invoice.InvoiceStatus.UNPAID);

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created: number={}, patient={}", saved.getInvoiceNumber(), patient.getFullName());
        return financeMapper.toInvoiceResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(financeMapper::toInvoiceResponse);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
        return financeMapper.toInvoiceResponse(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getInvoicesByPatient(Long patientId, Pageable pageable) {
        return invoiceRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable)
                .map(financeMapper::toInvoiceResponse);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getInvoicesByStatus(Invoice.InvoiceStatus status, Pageable pageable) {
        return invoiceRepository.findByStatus(status, pageable).map(financeMapper::toInvoiceResponse);
    }

    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", request.getInvoiceId()));

        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new BusinessException("Invoice is already fully paid");
        }

        if (invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            throw new BusinessException("Cannot add payment to a cancelled invoice");
        }

        if (request.getAmount().compareTo(invoice.getRemainingAmount()) > 0) {
            throw new BusinessException("Payment amount exceeds remaining balance");
        }

        Payment payment = financeMapper.toPaymentEntity(request);
        payment.setInvoice(invoice);
        payment.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDateTime.now());
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()));

        Payment saved = paymentRepository.save(payment);

        BigDecimal newPaidAmount = paymentRepository.sumAmountByInvoiceId(invoice.getId());
        invoice.setPaidAmount(newPaidAmount);
        if (newPaidAmount.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
        } else {
            invoice.setStatus(Invoice.InvoiceStatus.PARTIALLY_PAID);
        }
        invoiceRepository.save(invoice);

        log.info("Payment created: invoice={}, amount={}", invoice.getInvoiceNumber(), saved.getAmount());

        Patient patient = invoice.getPatient();
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            notificationService.notifyPaymentReceived(
                    patient.getEmail(), patient.getFullName(),
                    invoice.getInvoiceNumber(), saved.getAmount().toPlainString(),
                    invoice.getId());
        }

        return financeMapper.toPaymentResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByInvoice(Long invoiceId, Pageable pageable) {
        return paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId, pageable)
                .map(financeMapper::toPaymentResponse);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByPatient(Long patientId, Pageable pageable) {
        return paymentRepository.findByPatientId(patientId, pageable).map(financeMapper::toPaymentResponse);
    }

    @Transactional
    public InvoiceResponse cancelInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));

        if (invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            throw new BusinessException("Invoice is already cancelled");
        }

        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice cancelled: number={}", saved.getInvoiceNumber());
        return financeMapper.toInvoiceResponse(saved);
    }

    private String generateInvoiceNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "INV-" + datePart + "-" + uniquePart;
    }
}
