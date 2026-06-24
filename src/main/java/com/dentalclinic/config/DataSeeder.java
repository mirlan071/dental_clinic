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
        createUser("admin", "admin123", "admin@dentalclinic.com", "Админ", "Главный");
        log.info("Users seeded");
    }

    private void createUser(String username, String password, String email, String firstName, String lastName) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .active(true)
                .build();
        userRepository.save(user);
    }

    private void seedDoctors() {
        Doctor doctor1 = Doctor.builder()
                .firstName("Джон")
                .lastName("Смит")
                .specialization("Терапевт")
                .licenseNumber("DEN-001")
                .biography("Опытный стоматолог-терапевт, 10 лет стажа")
                .active(true)
                .build();
        doctorRepository.save(doctor1);

        Doctor doctor2 = Doctor.builder()
                .firstName("Эмили")
                .lastName("Джонс")
                .specialization("Ортодонт")
                .licenseNumber("DEN-002")
                .biography("Специалист по ортодонтии")
                .active(true)
                .build();
        doctorRepository.save(doctor2);

        Doctor doctor3 = Doctor.builder()
                .firstName("Азамат")
                .lastName("Козлов")
                .specialization("Хирург")
                .licenseNumber("DEN-003")
                .biography("Стоматолог-хирург")
                .active(true)
                .build();
        doctorRepository.save(doctor3);
        log.info("Doctors seeded");
    }

    private void seedWorkSchedules() {
        Doctor doctor1 = doctorRepository.findByLicenseNumber("DEN-001").orElseThrow();
        Doctor doctor2 = doctorRepository.findByLicenseNumber("DEN-002").orElseThrow();
        Doctor doctor3 = doctorRepository.findByLicenseNumber("DEN-003").orElseThrow();

        List<DayOfWeek> weekdays = List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        );

        for (DayOfWeek day : weekdays) {
            LocalTime end = day == DayOfWeek.FRIDAY ? LocalTime.of(15, 0) : LocalTime.of(17, 0);
            workScheduleRepository.save(WorkSchedule.builder()
                    .doctor(doctor1).dayOfWeek(day)
                    .startTime(LocalTime.of(9, 0)).endTime(end).active(true).build());
            workScheduleRepository.save(WorkSchedule.builder()
                    .doctor(doctor3).dayOfWeek(day)
                    .startTime(LocalTime.of(9, 0)).endTime(end).active(true).build());
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
        createService("Осмотр и консультация", "Первичный осмотр, диагностика", 2000, 30, "Диагностика");
        createService("Профессиональная чистка", "Чистка зубов ультразвуком + полировка", 3500, 45, "Гигиена");
        createService("Лечение кариеса", "Пломбирование зубов", 5000, 45, "Лечение");
        createService("Лечение каналов", "Эндодонтическое лечение", 8000, 90, "Лечение");
        createService("Отбел зубов", "Профессиональное отбеливание", 15000, 60, "Эстетика");
        createService("Коронка", "Установка коронки", 12000, 60, "Протезирование");
        createService("Имплант", "Установка импланта", 50000, 120, "Хирургия");
        createService("Брекеты", "Установка брекет-системы", 80000, 60, "Ортодонтия");
        createService("Удаление зуба", "Простое удаление", 5000, 30, "Хирургия");
        createService("Экстренная помощь", "Срочная стоматологическая помощь", 3000, 30, "Экстренная");
        log.info("Services seeded");
    }

    private void createService(String name, String description, double price, int duration, String category) {
        dentalServiceRepository.save(DentalService.builder()
                .name(name).description(description)
                .price(BigDecimal.valueOf(price)).durationMinutes(duration)
                .category(category).active(true).build());
    }

    private void seedPatients() {
        createPatient("Азамат", "Токтосунов", "+996700123456", "azamat@email.com",
                LocalDate.of(1990, 5, 15), Patient.Gender.MALE, "г. Бишкек, ул. Ленина 10");
        createPatient("Нурзат", "Кадырова", "+996777345678", "nurzat@email.com",
                LocalDate.of(1992, 11, 10), Patient.Gender.FEMALE, "г. Бишкек, ул. Курманжан Датки 25");
        createPatient("Бегимай", "Асанова", "+996555567890", "begimai@email.com",
                LocalDate.of(1995, 9, 28), Patient.Gender.FEMALE, "г. Бишкек, пр. Манас 50");
        log.info("Patients seeded");
    }

    private void createPatient(String firstName, String lastName, String phone, String email,
                               LocalDate dob, Patient.Gender gender, String address) {
        patientRepository.save(Patient.builder()
                .firstName(firstName).lastName(lastName)
                .phone(phone).email(email).dateOfBirth(dob)
                .gender(gender).address(address).active(true).build());
    }
}
