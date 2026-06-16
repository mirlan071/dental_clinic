package com.dentalclinic.repository;

import com.dentalclinic.domain.doctor.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    List<WorkSchedule> findByDoctorIdAndActiveTrue(Long doctorId);

    List<WorkSchedule> findByDoctorIdAndDayOfWeekAndActiveTrue(Long doctorId, DayOfWeek dayOfWeek);

    @Query("SELECT ws FROM WorkSchedule ws WHERE ws.doctor.id = :doctorId AND ws.active = true " +
           "AND ws.dayOfWeek = :dayOfWeek AND ws.startTime <= :time AND ws.endTime > :time")
    List<WorkSchedule> findActiveScheduleAtTime(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") java.time.LocalTime time);
}
