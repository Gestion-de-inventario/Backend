package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRotacionDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenMensualDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    // CA 2 & 3: Obtener totales e ingresos diarios desde Reportes de Menú
    @Query("SELECT m.date, SUM(m.totalEarned), SUM(m.totalSpent) " +
            "FROM MenuReportEntity m " +
            "WHERE EXTRACT(YEAR FROM m.date) = :anio AND EXTRACT(MONTH FROM m.date) = :mes " +
            "GROUP BY m.date")
    List<Object[]> findDailyMenuReportSummary(@Param("anio") int anio, @Param("mes") int mes);

    // CA 2 & 3: Obtener egresos diarios desde Órdenes de Compra
    @Query("SELECT p.purchaseDate, SUM(p.totalSpent) " +
            "FROM PurchaseEntity p " +
            "WHERE EXTRACT(YEAR FROM p.purchaseDate) = :anio AND EXTRACT(MONTH FROM p.purchaseDate) = :mes " +
            "AND p.status = 'RECIBIDO'"+
            "GROUP BY p.purchaseDate")
    List<Object[]> findDailyPurchaseSummary(@Param("anio") int anio, @Param("mes") int mes);
}
