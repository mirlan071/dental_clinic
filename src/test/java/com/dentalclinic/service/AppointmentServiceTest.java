package com.dentalclinic.service;

import com.dentalclinic.controller.dto.appointment.AppointmentCreateRequest;
import com.dentalclinic.controller.dto.appointment.AppointmentResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DentalServiceRepository dentalServiceRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private DentalService dentalService;
    private Appointment appointment;
    private AppointmentCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setSpecialization("General Dentistry");
        com.dentalclinic.domain.user.User user = new com.dentalclinic.domain.user.User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        doctor.setUser(user);

        dentalService = new DentalService();
        dentalService.setId(1L);
        dentalService.setName("Consultation");
        dentalService.setDurationMinutes(30);

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setServices(List.of(dentalService));
        appointment.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        appointment.setEndTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(30));
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        createRequest = AppointmentCreateRequest.builder()
                .patientId(1L)
                .doctorId(1L)
                .serviceIds(List.of(1L))
                .startTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0))
                .build();
    }

    @Test
    void create_appointment_success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(dentalServiceRepository.findAllById(any())).thenReturn(List.of(dentalService));
        when(appointmentRepository.findConflictingAppointments(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientConflictingAppointments(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(new AppointmentResponse());

        AppointmentResponse result = appointmentService.create(createRequest);

        assertNotNull(result);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void create_appointment_conflict_throws() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(dentalServiceRepository.findAllById(any())).thenReturn(List.of(dentalService));
        when(appointmentRepository.findConflictingAppointments(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new Appointment()));

        assertThrows(TimeConflictException.class, () -> appointmentService.create(createRequest));
    }

    @Test
    void cancel_appointment_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(any())).thenReturn(new AppointmentResponse());

        appointmentService.cancel(1L);

        assertEquals(Appointment.AppointmentStatus.CANCELLED, appointment.getStatus());
    }

    @Test
    void cancel_completed_appointment_throws() {
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThrows(BusinessException.class, () -> appointmentService.cancel(1L));
    }
}
