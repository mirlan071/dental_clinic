package com.dentalclinic.repository;

import com.dentalclinic.domain.finance.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByInvoiceIdOrderByPaymentDateDesc(Long invoiceId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.invoice.patient.id = :patientId ORDER BY p.paymentDate DESC")
    Page<Payment> findByPatientId(@Param("patientId") Long patientId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
