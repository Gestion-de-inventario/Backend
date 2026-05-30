package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "modifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModificationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "edited_class", nullable = false, length = 25)
    private String editedClass;

    @Column(name = "edited_attribute", nullable = false, length = 35)
    private String editedAttribute;

    @Column(name = "previous_value", length = 80)
    private String previousValue;

    @Column(name = "new_value", length = 80)
    private String newValue;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
}