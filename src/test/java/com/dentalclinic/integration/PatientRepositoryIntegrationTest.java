package com.dentalclinic.integration;

import com.dentalclinic.domain.patient.Patient;
import com.dentalclinic.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PatientRepositoryIntegrationTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void create_and_find_patient() {
        Patient patient = Patient.builder()
                .firstName("Test")
                .lastName("User")
                .phone("+1111111111")
                .email("test@email.com")
                .dateOfBirth(LocalDate.of(1995, 6, 15))
                .gender(Patient.Gender.MALE)
                .active(true)
                .build();

        Patient saved = patientRepository.save(patient);
        assertNotNull(saved.getId());

        Patient found = patientRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Test", found.getFirstName());
        assertEquals("+1111111111", found.getPhone());
    }

    @Test
    void search_by_name() {
        Patient patient = Patient.builder()
                .firstName("Searchable")
                .lastName("Person")
                .phone("+2222222222")
                .dateOfBirth(LocalDate.of(1990, 3, 10))
                .gender(Patient.Gender.FEMALE)
                .active(true)
                .build();
        patientRepository.save(patient);

        var results = patientRepository.search("searchable", 
                org.springframework.data.domain.PageRequest.of(0, 10));
        assertFalse(results.isEmpty());
        assertEquals("Searchable", results.getContent().get(0).getFirstName());
    }

    @Test
    void search_by_phone() {
        Patient patient = Patient.builder()
                .firstName("Phone")
                .lastName("Search")
                .phone("+3333333333")
                .dateOfBirth(LocalDate.of(1988, 7, 20))
                .gender(Patient.Gender.MALE)
                .active(true)
                .build();
        patientRepository.save(patient);

        var results = patientRepository.search("3333333333", 
                org.springframework.data.domain.PageRequest.of(0, 10));
        assertFalse(results.isEmpty());
    }

    @Test
    void exists_by_phone() {
        Patient patient = Patient.builder()
                .firstName("Unique")
                .lastName("Phone")
                .phone("+4444444444")
                .dateOfBirth(LocalDate.of(1992, 11, 5))
                .gender(Patient.Gender.MALE)
                .active(true)
                .build();
        patientRepository.save(patient);

        assertTrue(patientRepository.existsByPhone("+4444444444"));
        assertFalse(patientRepository.existsByPhone("+5555555555"));
    }

    @Test
    void find_active_patients() {
        Patient active = Patient.builder()
                .firstName("Active")
                .lastName("Patient")
                .phone("+6666666666")
                .dateOfBirth(LocalDate.of(1993, 2, 28))
                .gender(Patient.Gender.MALE)
                .active(true)
                .build();
        Patient inactive = Patient.builder()
                .firstName("Inactive")
                .lastName("Patient")
                .phone("+7777777777")
                .dateOfBirth(LocalDate.of(1994, 8, 12))
                .gender(Patient.Gender.FEMALE)
                .active(false)
                .build();
        patientRepository.save(active);
        patientRepository.save(inactive);

        var results = patientRepository.findByActiveTrue(
                org.springframework.data.domain.PageRequest.of(0, 100));
        assertTrue(results.getContent().stream()
                .allMatch(Patient::isActive));
    }
}
