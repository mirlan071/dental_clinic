package com.dentalclinic.domain.doctor;

import com.dentalclinic.domain.common.BaseEntity;
import com.dentalclinic.domain.user.User;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

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
        return user.getFirstName() + " " + user.getLastName();
    }
}
