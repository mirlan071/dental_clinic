package com.dentalclinic.repository;

import com.dentalclinic.domain.medicalrecord.DocumentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentAttachmentRepository extends JpaRepository<DocumentAttachment, Long> {
    List<DocumentAttachment> findByMedicalRecordId(Long medicalRecordId);
    void deleteByMedicalRecordId(Long medicalRecordId);
}
