package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int id;

    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(optional = true)
    @JoinColumn(name = "tag_id", nullable = true)
    private TagEntity tag;

    private String unit;

    @Enumerated(EnumType.STRING)
    private Estado status;

    @Column(precision = 10, scale = 3)
    private BigDecimal stock;

    @Column(name = "reorder_point",precision = 10, scale = 3)
    private BigDecimal reorderPoint;
}