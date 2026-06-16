package com.dentalclinic.service;

import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordCreateRequest;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordResponse;
import com.dentalclinic.controller.dto.medicalrecord.MedicalRecordUpdateRequest;
import com.dentalclinic.domain.doctor.Doctor;
import com.dentalclinic.domain.medicalrecord.MedicalRecord;
import com.dentalclinic.domain.patient.Patient;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.mapper.MedicalRecordMapper;
import com.dentalclinic.repository.DoctorRepository;
import com.dentalclinic.repository.MedicalRecordRepository;
import com.dentalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Transactional
    public MedicalRecordResponse create(MedicalRecordCreateRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", request.getDoctorId()));

        MedicalRecord record = medicalRecordMapper.toEntity(request);
        record.setPatient(patient);
        record.setDoctor(doctor);

        MedicalRecord saved = medicalRecordRepository.save(record);
        log.info("Medical record created: id={}, patient={}", saved.getId(), patient.getFullName());
        return medicalRecordMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse getById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record", id));
        return medicalRecordMapper.toResponse(record);
    }

    @Transactional(readOnly = true)
    public Page<MedicalRecordResponse> getByPatient(Long patientId, Pageable pageable) {
        return medicalRecordRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable)
                .map(medicalRecordMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<MedicalRecordResponse> getByDoctor(Long doctorId, Pageable pageable) {
        return medicalRecordRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId, pageable)
                .map(medicalRecordMapper::toResponse);
    }

    @Transactional
    public MedicalRecordResponse update(Long id, MedicalRecordUpdateRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record", id));

        if (request.getDiagnosis() != null) {
            record.setDiagnosis(request.getDiagnosis());
        }
        if (request.getTreatment() != null) {
            record.setTreatment(request.getTreatment());
        }
        if (request.getRecommendations() != null) {
            record.setRecommendations(request.getRecommendations());
        }
        if (request.getNotes() != null) {
            record.setNotes(request.getNotes());
        }

        MedicalRecord updated = medicalRecordRepository.save(record);
        log.info("Medical record updated: id={}", id);
        return medicalRecordMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record", id));
        medicalRecordRepository.delete(record);
        log.info("Medical record deleted: id={}", id);
    }
}
