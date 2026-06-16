package com.dentalclinic.service;

import com.dentalclinic.controller.dto.doctor.*;
import com.dentalclinic.domain.doctor.Doctor;
import com.dentalclinic.domain.doctor.WorkSchedule;
import com.dentalclinic.domain.user.User;
import com.dentalclinic.exception.DuplicateResourceException;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.mapper.DoctorMapper;
import com.dentalclinic.repository.DoctorRepository;
import com.dentalclinic.repository.UserRepository;
import com.dentalclinic.repository.WorkScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final DoctorMapper doctorMapper;

    @Transactional
    @CacheEvict(value = "doctors", allEntries = true)
    public DoctorResponse create(DoctorCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        if (doctorRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new DuplicateResourceException("Doctor profile already exists for this user");
        }

        if (request.getLicenseNumber() != null) {
            if (doctorRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
                throw new DuplicateResourceException("Doctor", "license number", request.getLicenseNumber());
            }
        }

        Doctor doctor = doctorMapper.toEntity(request);
        doctor.setUser(user);
        Doctor saved = doctorRepository.save(doctor);
        log.info("Doctor created: id={}, name={}", saved.getId(), saved.getFullName());
        return doctorMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "doctors", key = "#id")
    public DoctorResponse getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        return doctorMapper.toResponse(doctor);
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponse> getAll(Pageable pageable) {
        return doctorRepository.findByActiveTrue(pageable).map(doctorMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponse> search(String query, Pageable pageable) {
        return doctorRepository.search(query, pageable).map(doctorMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponse> getBySpecialization(String specialization, Pageable pageable) {
        return doctorRepository.findBySpecializationIgnoreCase(specialization, pageable).map(doctorMapper::toResponse);
    }

    @Transactional
    @CachePut(value = "doctors", key = "#id")
    @CacheEvict(value = "doctors", allEntries = true)
    public DoctorResponse update(Long id, DoctorUpdateRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));

        if (request.getLicenseNumber() != null && !request.getLicenseNumber().equals(doctor.getLicenseNumber())) {
            if (doctorRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
                throw new DuplicateResourceException("Doctor", "license number", request.getLicenseNumber());
            }
        }

        doctorMapper.updateEntity(request, doctor);
        Doctor updated = doctorRepository.save(doctor);
        log.info("Doctor updated: id={}", updated.getId());
        return doctorMapper.toResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "doctors", allEntries = true)
    public void delete(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        doctor.setActive(false);
        doctorRepository.save(doctor);
        log.info("Doctor deactivated: id={}", id);
    }

    @Transactional
    @CacheEvict(value = "doctors", allEntries = true)
    public WorkScheduleResponse addSchedule(Long doctorId, WorkScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));

        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().equals(request.getEndTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        WorkSchedule schedule = doctorMapper.toScheduleEntity(request);
        schedule.setDoctor(doctor);
        WorkSchedule saved = workScheduleRepository.save(schedule);
        log.info("Work schedule added: doctorId={}, day={}", doctorId, saved.getDayOfWeek());
        return doctorMapper.toScheduleResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkScheduleResponse> getSchedule(Long doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));
        return doctorMapper.toScheduleResponseList(
                workScheduleRepository.findByDoctorIdAndActiveTrue(doctorId));
    }

    @Transactional
    @CacheEvict(value = "doctors", allEntries = true)
    public void removeSchedule(Long scheduleId) {
        WorkSchedule schedule = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Work schedule", scheduleId));
        schedule.setActive(false);
        workScheduleRepository.save(schedule);
        log.info("Work schedule removed: id={}", scheduleId);
    }
}
