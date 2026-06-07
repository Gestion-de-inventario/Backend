package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRotacionDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ResumenMensualDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DashboardJpaRepository extends JpaRepository<ProductEntity,Integer> {

    // CA 1: Top 5 productos con mayor rotación (Salidas/Consumos)
    @Query(value = "SELECT new com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRotacionDTO(p.name, p.unit, SUM(m.quantityUsed)) " +
            "FROM StockMovementEntity m " +
            "JOIN m.inventoryLot l " +
            "JOIN l.product p " +
            "WHERE m.movementDate >= :inicio AND m.movementDate <= :fin " +
            "GROUP BY p.name, p.unit " +
            "ORDER BY SUM(m.quantityUsed) DESC")
    List<ProductoRotacionDTO> findTop5ProductosMasRotados(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // CA 2: Valor total estimado del inventario actual
    @Query("SELECT SUM(p.stock) FROM ProductEntity p WHERE p.stock > 0")
    BigDecimal findValorTotalInventario();

    // CA 3: Consolidar gastos diarios en resumen mensual (Agrupado por día del mes)
    @Query(value = "SELECT new com.comedor.backend.infrastructure.adapters.in.web.dto.request.ResumenMensualDTO(CAST(g.purchaseDate AS string), SUM(g.totalSpent)) " +
            "FROM PurchaseEntity g " +
            "WHERE EXTRACT(YEAR FROM g.purchaseDate) = :anio AND EXTRACT(MONTH FROM g.purchaseDate) = :mes " +
            "GROUP BY g.purchaseDate " +
            "ORDER BY g.purchaseDate ASC")
    List<ResumenMensualDTO> findGastosConsolidadosMensuales(@Param("anio") int anio, @Param("mes") int mes);
}
