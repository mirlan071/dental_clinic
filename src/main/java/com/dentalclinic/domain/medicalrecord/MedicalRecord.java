package com.dentalclinic.domain.medicalrecord;

import com.dentalclinic.domain.appointment.Appointment;
import com.dentalclinic.domain.common.BaseEntity;
import com.dentalclinic.domain.doctor.Doctor;
import com.dentalclinic.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_records", indexes = {
        @Index(name = "idx_medrec_patient", columnList = "patient_id"),
        @Index(name = "idx_medrec_doctor", columnList = "doctor_id"),
        @Index(name = "idx_medrec_appointment", columnList = "appointment_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(length = 5000)
    private String diagnosis;

    @Column(length = 5000)
    private String treatment;

    @Column(length = 2000)
    private String recommendations;

    @Column(length = 5000)
    private String notes;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentAttachment> documents = new ArrayList<>();
}
