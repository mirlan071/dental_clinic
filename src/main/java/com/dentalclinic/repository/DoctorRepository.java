package com.dentalclinic.repository;

import com.dentalclinic.domain.doctor.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Page<Doctor> findByActiveTrue(Pageable pageable);

    Page<Doctor> findBySpecializationIgnoreCase(String specialization, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.active = true AND (" +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Doctor> search(@Param("query") String query, Pageable pageable);

    Optional<Doctor> findByLicenseNumber(String licenseNumber);
}
