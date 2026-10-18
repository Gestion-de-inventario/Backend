package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.domain.model.records.DailySoldMenusSummary;
import com.comedor.backend.domain.model.records.MonthlyFinancialSummary;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MenuReportJpaRepository extends
        JpaRepository<MenuReportEntity, Integer>,
        JpaSpecificationExecutor<MenuReportEntity> {

    boolean existsByDate(LocalDate date);
    MenuReportEntity findByDate(LocalDate date);
    Page<MenuReportEntity> findAll(Pageable pageable);

    @Query("SELECT new com.comedor.backend.domain.model.records.MonthlyFinancialSummary(" +
            "       MONTH(r.date), " +
            "       COALESCE(SUM(r.totalEarned), 0), " +
            "       COALESCE(SUM(r.totalSpent), 0)) " +
            "FROM MenuReportEntity r " +
            "WHERE YEAR(r.date) = :year " +
            "GROUP BY MONTH(r.date)")
    List<MonthlyFinancialSummary> getMonthlyFinancialSummaryByYear(@Param("year") int year);

    @Query("SELECT new com.comedor.backend.domain.model.records.DailySoldMenusSummary(" +
            "       r.date, " +
            "       SUM(r.quantityPrepared - r.quantityRemaining)) " +
            "FROM MenuReportEntity r " +
            "WHERE r.date BETWEEN :startDate AND :endDate " +
            "GROUP BY r.date " +
            "ORDER BY r.date ASC")
    List<DailySoldMenusSummary> getDailySoldMenusInRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
