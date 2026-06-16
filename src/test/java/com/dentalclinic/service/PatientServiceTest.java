package com.dentalclinic.service;

import com.dentalclinic.controller.dto.patient.PatientCreateRequest;
import com.dentalclinic.controller.dto.patient.PatientResponse;
import com.dentalclinic.controller.dto.patient.PatientUpdateRequest;
import com.dentalclinic.domain.patient.Patient;
import com.dentalclinic.exception.DuplicateResourceException;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.mapper.PatientMapper;
import com.dentalclinic.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    private PatientCreateRequest createRequest;
    private Patient patient;
    private PatientResponse patientResponse;

    @BeforeEach
    void setUp() {
        createRequest = PatientCreateRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .email("john@email.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("MALE")
                .build();

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setPhone("+1234567890");
        patient.setEmail("john@email.com");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setGender(Patient.Gender.MALE);

        patientResponse = PatientResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .phone("+1234567890")
                .email("john@email.com")
                .build();
    }

    @Test
    void create_patient_success() {
        when(patientRepository.existsByPhone(anyString())).thenReturn(false);
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientMapper.toEntity(any(PatientCreateRequest.class))).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(patientResponse);

        PatientResponse result = patientService.create(createRequest);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void create_patient_duplicate_phone_throws() {
        when(patientRepository.existsByPhone("+1234567890")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> patientService.create(createRequest));
    }

    @Test
    void get_by_id_success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(patientResponse);

        PatientResponse result = patientService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void get_by_id_not_found_throws() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.getById(999L));
    }

    @Test
    void update_patient_success() {
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder()
                .firstName("Jane")
                .build();

        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setFirstName("Jane");
        updatedPatient.setLastName("Doe");

        PatientResponse updatedResponse = PatientResponse.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(updatedResponse);

        PatientResponse result = patientService.update(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
    }

    @Test
    void delete_patient_deactivates() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.delete(1L);

        assertFalse(patient.isActive());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }
}
