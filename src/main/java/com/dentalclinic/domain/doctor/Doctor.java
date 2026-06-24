package com.dentalclinic.domain.doctor;

import com.dentalclinic.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors", indexes = {
        @Index(name = "idx_doctor_specialization", columnList = "specialization")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 150)
    private String specialization;

    @Column(name = "license_number", unique = true, length = 50)
    private String licenseNumber;

    @Column(length = 2000)
    private String biography;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkSchedule> workSchedules = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
