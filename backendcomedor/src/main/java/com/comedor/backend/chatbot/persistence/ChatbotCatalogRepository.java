package com.comedor.backend.chatbot.persistence;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Vista de solo lectura del catálogo que mantiene toda la persistencia usada por
 * el asistente dentro del módulo chatbot.
 */
public interface ChatbotCatalogRepository extends JpaRepository<DishMenuEntity, Integer> {

    @Query("""
            SELECT DISTINCT dish
            FROM DishMenuEntity dish
            LEFT JOIN FETCH dish.supplies supply
            LEFT JOIN FETCH supply.product product
            WHERE dish.status = com.comedor.backend.domain.model.enums.Status.ACTIVO
            ORDER BY dish.name
            """)
    List<DishMenuEntity> findActiveDishesWithSupplies();
}
