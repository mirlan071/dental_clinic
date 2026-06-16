package com.dentalclinic.repository;

import com.dentalclinic.domain.patient.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    @Query("SELECT p FROM Patient p WHERE p.active = true AND (" +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "CAST(p.id AS string) LIKE CONCAT('%', :query, '%'))")
    Page<Patient> search(@Param("query") String query, Pageable pageable);

    Page<Patient> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.active = true AND " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    Page<Patient> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName, Pageable pageable);
}
