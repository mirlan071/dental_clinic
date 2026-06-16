package com.dentalclinic.config;

import com.dentalclinic.domain.doctor.Doctor;
import com.dentalclinic.domain.doctor.WorkSchedule;
import com.dentalclinic.domain.patient.Patient;
import com.dentalclinic.domain.service.DentalService;
import com.dentalclinic.domain.user.User;
import com.dentalclinic.repository.DoctorRepository;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.repository.DentalServiceRepository;
import com.dentalclinic.repository.UserRepository;
import com.dentalclinic.repository.WorkScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final PatientRepository patientRepository;
    private final DentalServiceRepository dentalServiceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }
        seedUsers();
        seedDoctors();
        seedWorkSchedules();
        seedServices();
        seedPatients();
        log.info("Database seeding completed successfully");
    }

    private void seedUsers() {
        createUser("admin", "admin123", "admin@dentalclinic.com", "System", "Administrator", User.Role.ADMIN);
        createUser("dr_smith", "doctor123", "smith@dentalclinic.com", "John", "Smith", User.Role.DOCTOR);
        createUser("dr_jones", "doctor123", "jones@dentalclinic.com", "Emily", "Jones", User.Role.DOCTOR);
        createUser("reception1", "reception123", "reception@dentalclinic.com", "Maria", "Garcia", User.Role.RECEPTIONIST);
        log.info("Users seeded");
    }

    private void createUser(String username, String password, String email, String firstName, String lastName, User.Role role) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .active(true)
                .build();
        userRepository.save(user);
    }

    private void seedDoctors() {
        User smith = userRepository.findByUsername("dr_smith").orElseThrow();
        User jones = userRepository.findByUsername("dr_jones").orElseThrow();

        Doctor doctor1 = Doctor.builder()
                .user(smith)
                .specialization("General Dentistry")
                .licenseNumber("DEN-001")
                .biography("Experienced general dentist with 10 years of practice")
                .active(true)
                .build();
        doctorRepository.save(doctor1);

        Doctor doctor2 = Doctor.builder()
                .user(jones)
                .specialization("Orthodontics")
                .licenseNumber("DEN-002")
                .biography("Specialized in orthodontic treatments")
                .active(true)
                .build();
        doctorRepository.save(doctor2);
        log.info("Doctors seeded");
    }

    private void seedWorkSchedules() {
        Doctor doctor1 = doctorRepository.findByLicenseNumber("DEN-001").orElseThrow();
        Doctor doctor2 = doctorRepository.findByLicenseNumber("DEN-002").orElseThrow();

        List<DayOfWeek> weekdays = List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        );

        for (DayOfWeek day : weekdays) {
            LocalTime end = day == DayOfWeek.FRIDAY ? LocalTime.of(15, 0) : LocalTime.of(17, 0);
            WorkSchedule schedule = WorkSchedule.builder()
                    .doctor(doctor1)
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(end)
                    .active(true)
                    .build();
            workScheduleRepository.save(schedule);
        }

        workScheduleRepository.save(WorkSchedule.builder()
                .doctor(doctor2).dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(18, 0)).active(true).build());
        workScheduleRepository.save(WorkSchedule.builder()
                .doctor(doctor2).dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(18, 0)).active(true).build());
        workScheduleRepository.save(WorkSchedule.builder()
                .doctor(doctor2).dayOfWeek(DayOfWeek.FRIDAY)
                .startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(16, 0)).active(true).build());
        log.info("Work schedules seeded");
    }

    private void seedServices() {
        createService("General Consultation", "Initial dental consultation and examination", 50.00, 30, "Consultation");
        createService("Professional Cleaning", "Professional teeth cleaning and polishing", 80.00, 45, "Preventive");
        createService("Dental Filling", "Composite or amalgam dental filling", 120.00, 45, "Restorative");
        createService("Root Canal Treatment", "Endodontic root canal therapy", 500.00, 90, "Endodontics");
        createService("Teeth Whitening", "Professional teeth whitening procedure", 250.00, 60, "Cosmetic");
        createService("Dental Crown", "Porcelain or metal crown placement", 800.00, 60, "Prosthetics");
        createService("Dental Implant", "Single tooth implant placement", 2000.00, 120, "Implantology");
        createService("Orthodontic Consultation", "Orthodontic assessment and treatment planning", 100.00, 45, "Orthodontics");
        createService("Braces Adjustment", "Orthodontic braces adjustment", 150.00, 30, "Orthodontics");
        createService("Emergency Consultation", "Urgent dental care consultation", 75.00, 30, "Emergency");
        log.info("Dental services seeded");
    }

    private void createService(String name, String description, double price, int duration, String category) {
        DentalService service = DentalService.builder()
                .name(name)
                .description(description)
                .price(BigDecimal.valueOf(price))
                .durationMinutes(duration)
                .category(category)
                .active(true)
                .build();
        dentalServiceRepository.save(service);
    }

    private void seedPatients() {
        createPatient("Alice", "Brown", "Jane", "+1234567890", "alice@email.com",
                LocalDate.of(1990, 5, 15), Patient.Gender.FEMALE, "123 Main St");
        createPatient("Bob", "Wilson", "Robert", "+0987654321", "bob@email.com",
                LocalDate.of(1985, 8, 22), Patient.Gender.MALE, "456 Oak Ave");
        createPatient("Carol", "Davis", "Ann", "+1122334455", "carol@email.com",
                LocalDate.of(1995, 12, 3), Patient.Gender.FEMALE, "789 Pine Rd");
        log.info("Patients seeded");
    }

    private void createPatient(String firstName, String lastName, String patronymic,
                               String phone, String email, LocalDate dob,
                               Patient.Gender gender, String address) {
        Patient patient = Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .patronymic(patronymic)
                .phone(phone)
                .email(email)
                .dateOfBirth(dob)
                .gender(gender)
                .address(address)
                .active(true)
                .build();
        patientRepository.save(patient);
    }
}
