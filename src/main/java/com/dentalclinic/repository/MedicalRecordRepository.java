package com.dentalclinic.repository;

import com.dentalclinic.domain.medicalrecord.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    Page<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(Long patientId, Pageable pageable);

    Page<MedicalRecord> findByDoctorIdOrderByCreatedAtDesc(Long doctorId, Pageable pageable);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.id = :patientId AND " +
           "LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<MedicalRecord> searchByDiagnosis(
            @Param("patientId") Long patientId,
            @Param("query") String query,
            Pageable pageable);
}
