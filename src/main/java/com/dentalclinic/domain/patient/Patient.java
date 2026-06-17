package com.dentalclinic.domain.patient;

import com.dentalclinic.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients", indexes = {
        @Index(name = "idx_patient_full_name", columnList = "last_name, first_name"),
        @Index(name = "idx_patient_phone", columnList = "phone"),
        @Index(name = "idx_patient_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "patronymic", length = 100)
    private String patronymic;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(length = 500)
    private String address;

    @Column(name = "insurance_policy", length = 50)
    private String insurancePolicy;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<com.dentalclinic.domain.appointment.Appointment> appointments = new ArrayList<>();

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
