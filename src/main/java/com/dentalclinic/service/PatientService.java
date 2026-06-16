package com.dentalclinic.service;

import com.dentalclinic.controller.dto.patient.PatientCreateRequest;
import com.dentalclinic.controller.dto.patient.PatientResponse;
import com.dentalclinic.controller.dto.patient.PatientUpdateRequest;
import com.dentalclinic.domain.patient.Patient;
import com.dentalclinic.exception.DuplicateResourceException;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.mapper.PatientMapper;
import com.dentalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional
    @CacheEvict(value = "patients", allEntries = true)
    public PatientResponse create(PatientCreateRequest request) {
        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Patient", "phone", request.getPhone());
        }
        if (request.getEmail() != null && patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Patient", "email", request.getEmail());
        }

        Patient patient = patientMapper.toEntity(request);
        patient.setGender(Patient.Gender.valueOf(request.getGender()));
        Patient saved = patientRepository.save(patient);
        log.info("Patient created: id={}, name={}", saved.getId(), saved.getFullName());
        return patientMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "patients", key = "#id")
    public PatientResponse getById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        return patientMapper.toResponse(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> getAll(Pageable pageable) {
        return patientRepository.findByActiveTrue(pageable).map(patientMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> search(String query, Pageable pageable) {
        return patientRepository.search(query, pageable).map(patientMapper::toResponse);
    }

    @Transactional
    @CachePut(value = "patients", key = "#id")
    @CacheEvict(value = "patients", allEntries = true)
    public PatientResponse update(Long id, PatientUpdateRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));

        if (request.getPhone() != null && !request.getPhone().equals(patient.getPhone())) {
            if (patientRepository.existsByPhone(request.getPhone())) {
                throw new DuplicateResourceException("Patient", "phone", request.getPhone());
            }
        }
        if (request.getEmail() != null && !request.getEmail().equals(patient.getEmail())) {
            if (patientRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Patient", "email", request.getEmail());
            }
        }

        patientMapper.updateEntity(request, patient);
        if (request.getGender() != null) {
            patient.setGender(Patient.Gender.valueOf(request.getGender()));
        }

        Patient updated = patientRepository.save(patient);
        log.info("Patient updated: id={}", updated.getId());
        return patientMapper.toResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "patients", allEntries = true)
    public void delete(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        patient.setActive(false);
        patientRepository.save(patient);
        log.info("Patient deactivated: id={}", id);
    }
}
