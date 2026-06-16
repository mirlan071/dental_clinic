package com.dentalclinic.repository;

import com.dentalclinic.domain.appointment.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByPatientIdOrderByStartTimeDesc(Long patientId, Pageable pageable);

    Page<Appointment> findByDoctorIdOrderByStartTimeDesc(Long doctorId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status != 'CANCELLED' " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findConflictingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status != 'CANCELLED' " +
           "AND a.startTime < :endTime AND a.endTime > :startTime AND a.id != :excludeId")
    List<Appointment> findConflictingAppointmentsExcluding(
            @Param("doctorId") Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.status != 'CANCELLED' " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findPatientConflictingAppointments(
            @Param("patientId") Long patientId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    Page<Appointment> findByStatusAndStartTimeBetween(
            Appointment.AppointmentStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.startTime BETWEEN :start AND :end AND a.status != 'CANCELLED' " +
           "ORDER BY a.startTime")
    List<Appointment> findAppointmentsBetweenDates(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    boolean existsByDoctorIdAndStatusAndStartTimeBetween(
            Long doctorId,
            Appointment.AppointmentStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime);
}
