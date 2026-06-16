package com.dentalclinic.repository;

import com.dentalclinic.domain.service.DentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DentalServiceRepository extends JpaRepository<DentalService, Long> {

    Page<DentalService> findByActiveTrue(Pageable pageable);

    Page<DentalService> findByCategoryIgnoreCase(String category, Pageable pageable);

    @Query("SELECT ds FROM DentalService ds WHERE ds.active = true AND (" +
           "LOWER(ds.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(ds.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<DentalService> search(@Param("query") String query, Pageable pageable);
}
