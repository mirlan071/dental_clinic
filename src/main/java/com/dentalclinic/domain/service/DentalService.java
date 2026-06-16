package com.dentalclinic.domain.service;

import com.dentalclinic.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "dental_services", indexes = {
        @Index(name = "idx_service_category", columnList = "category"),
        @Index(name = "idx_service_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DentalService extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private boolean active = true;
}
