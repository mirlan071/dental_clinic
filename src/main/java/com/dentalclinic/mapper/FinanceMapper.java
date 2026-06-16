package com.dentalclinic.mapper;

import com.dentalclinic.controller.dto.finance.InvoiceCreateRequest;
import com.dentalclinic.controller.dto.finance.InvoiceResponse;
import com.dentalclinic.controller.dto.finance.PaymentCreateRequest;
import com.dentalclinic.controller.dto.finance.PaymentResponse;
import com.dentalclinic.domain.finance.Invoice;
import com.dentalclinic.domain.finance.Payment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinanceMapper {

    Invoice toInvoiceEntity(InvoiceCreateRequest request);

    @Mapping(target = "patientId", expression = "java(invoice.getPatient().getId())")
    @Mapping(target = "patientName", expression = "java(invoice.getPatient().getFullName())")
    @Mapping(target = "appointmentId", expression = "java(invoice.getAppointment() != null ? invoice.getAppointment().getId() : null)")
    @Mapping(target = "remainingAmount", expression = "java(invoice.getRemainingAmount())")
    InvoiceResponse toInvoiceResponse(Invoice invoice);

    List<InvoiceResponse> toInvoiceResponseList(List<Invoice> invoices);

    Payment toPaymentEntity(PaymentCreateRequest request);

    @Mapping(target = "invoiceId", expression = "java(payment.getInvoice().getId())")
    @Mapping(target = "invoiceNumber", expression = "java(payment.getInvoice().getInvoiceNumber())")
    PaymentResponse toPaymentResponse(Payment payment);

    List<PaymentResponse> toPaymentResponseList(List<Payment> payments);
}
