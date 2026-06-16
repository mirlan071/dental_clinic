package com.dentalclinic.service;

import com.dentalclinic.controller.dto.appointment.AppointmentCreateRequest;
import com.dentalclinic.controller.dto.appointment.AppointmentResponse;
import com.dentalclinic.controller.dto.appointment.AppointmentUpdateRequest;
import com.dentalclinic.domain.appointment.Appointment;
import com.dentalclinic.domain.doctor.Doctor;
import com.dentalclinic.domain.patient.Patient;
import com.dentalclinic.domain.service.DentalService;
import com.dentalclinic.exception.BusinessException;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.exception.TimeConflictException;
import com.dentalclinic.mapper.AppointmentMapper;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.DoctorRepository;
import com.dentalclinic.repository.DentalServiceRepository;
import com.dentalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DentalServiceRepository dentalServiceRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentResponse create(AppointmentCreateRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", request.getDoctorId()));

        List<DentalService> services = dentalServiceRepository.findAllById(request.getServiceIds());
        if (services.isEmpty()) {
            throw new BusinessException("No valid services found");
        }

        int totalDuration = services.stream()
                .mapToInt(DentalService::getDurationMinutes)
                .sum();
        LocalDateTime endTime = request.getStartTime().plusMinutes(totalDuration);

        checkDoctorConflict(doctor.getId(), request.getStartTime(), endTime, null);
        checkPatientConflict(patient.getId(), request.getStartTime(), endTime);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setServices(services);
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(endTime);
        appointment.setNotes(request.getNotes());
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment created: id={}, patient={}, doctor={}",
                saved.getId(), patient.getFullName(), doctor.getFullName());
        return appointmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        return appointmentMapper.toResponse(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getByPatient(Long patientId, Pageable pageable) {
        return appointmentRepository.findByPatientIdOrderByStartTimeDesc(patientId, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getByDoctor(Long doctorId, Pageable pageable) {
        return appointmentRepository.findByDoctorIdOrderByStartTimeDesc(doctorId, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional
    public AppointmentResponse reschedule(Long id, LocalDateTime newStartTime) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED ||
            appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new BusinessException("Cannot reschedule a completed or cancelled appointment");
        }

        int totalDuration = appointment.getServices().stream()
                .mapToInt(DentalService::getDurationMinutes)
                .sum();
        LocalDateTime newEndTime = newStartTime.plusMinutes(totalDuration);

        checkDoctorConflict(appointment.getDoctor().getId(), newStartTime, newEndTime, id);
        checkPatientConflict(appointment.getPatient().getId(), newStartTime, newEndTime);

        appointment.setStartTime(newStartTime);
        appointment.setEndTime(newEndTime);

        Appointment updated = appointmentRepository.save(appointment);
        log.info("Appointment rescheduled: id={}, newTime={}", id, newStartTime);
        return appointmentMapper.toResponse(updated);
    }

    @Transactional
    public AppointmentResponse cancel(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel a completed appointment");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        Appointment updated = appointmentRepository.save(appointment);
        log.info("Appointment cancelled: id={}", id);
        return appointmentMapper.toResponse(updated);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        Appointment.AppointmentStatus newStatus = Appointment.AppointmentStatus.valueOf(request.getStatus());
        appointment.setStatus(newStatus);

        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }

        Appointment updated = appointmentRepository.save(appointment);
        log.info("Appointment status updated: id={}, status={}", id, newStatus);
        return appointmentMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getByStatusAndDateRange(
            Appointment.AppointmentStatus status,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable) {
        return appointmentRepository.findByStatusAndStartTimeBetween(status, start, end, pageable)
                .map(appointmentMapper::toResponse);
    }

    private void checkDoctorConflict(Long doctorId, LocalDateTime start, LocalDateTime end, Long excludeId) {
        List<Appointment> conflicts;
        if (excludeId != null) {
            conflicts = appointmentRepository.findConflictingAppointmentsExcluding(doctorId, start, end, excludeId);
        } else {
            conflicts = appointmentRepository.findConflictingAppointments(doctorId, start, end);
        }
        if (!conflicts.isEmpty()) {
            throw new TimeConflictException("Doctor has a conflicting appointment during this time");
        }
    }

    private void checkPatientConflict(Long patientId, LocalDateTime start, LocalDateTime end) {
        List<Appointment> conflicts = appointmentRepository.findPatientConflictingAppointments(patientId, start, end);
        if (!conflicts.isEmpty()) {
            throw new TimeConflictException("Patient has a conflicting appointment during this time");
        }
    }
}
