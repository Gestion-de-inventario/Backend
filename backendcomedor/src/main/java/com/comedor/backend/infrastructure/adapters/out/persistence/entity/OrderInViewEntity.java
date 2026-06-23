package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.domain.model.enums.ProductSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Immutable
@Subselect("""
    SELECT
        CONCAT('COMPRA-', p.purchase_id) AS reference,
        p.purchase_id AS id,
        'COMPRA' AS source,
        p.purchase_date AS date,
        p.status AS status,
        COALESCE(p.total_spent, 0) AS total_spent
    FROM purchase p

    UNION ALL

    SELECT
        CONCAT('DONACION-', d.donation_id) AS reference,
        d.donation_id AS id,
        'DONACION' AS source,
        d.donation_date AS date,
        d.status AS status,
        CAST(0 AS numeric(10,2)) AS total_spent
    FROM donation d
""")
@Synchronize({"purchase", "donation"})
@Getter
@Setter
@NoArgsConstructor
public class OrderInViewEntity {

    @Id
    private String reference;

    private Integer id;

    @Enumerated(EnumType.STRING)
    private ProductSource source;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private StatusOrder status;

    @Column(name = "total_spent")
    private BigDecimal totalSpent;
}