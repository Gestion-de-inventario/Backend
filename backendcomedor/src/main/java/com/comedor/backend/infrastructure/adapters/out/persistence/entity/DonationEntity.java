package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.EstadoOrden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden status;

    @Column(nullable = false)
    private LocalDate donationDate;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL)
    private List<DonationDetailEntity> details = new ArrayList<>();

}
