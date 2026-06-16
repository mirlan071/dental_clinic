package com.dentalclinic.repository;

import com.dentalclinic.domain.finance.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByPatientIdOrderByCreatedAtDesc(Long patientId, Pageable pageable);

    Page<Invoice> findByStatus(Invoice.InvoiceStatus status, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end")
    Page<Invoice> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end")
    BigDecimal sumPaymentsByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT i FROM Invoice i WHERE i.patient.id = :patientId AND i.status != 'CANCELLED'")
    Page<Invoice> findActiveByPatientId(@Param("patientId") Long patientId, Pageable pageable);
}
