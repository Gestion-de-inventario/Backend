package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.domain.model.records.TopBeneficiarySummary;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryControlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BeneficiaryControlJpaRepository extends JpaRepository<BeneficiaryControlEntity, Integer> {
    List<BeneficiaryControlEntity> findByReportId(int reporteId);
    boolean existsByReportIdAndBeneficiaryId(int reportId, int beneficiaryId);

    @Query("SELECT new com.comedor.backend.domain.model.records.TopBeneficiarySummary(" +
            "       b.id, b.dni, b.name, b.lastname, SUM(bc.menusAmount)) " +
            "FROM BeneficiaryControlEntity bc " +
            "JOIN bc.beneficiary b " +
            "JOIN bc.report r " +
            "WHERE r.date BETWEEN :startDate AND :endDate " +
            "  AND bc.menusAmount > 0 " +
            "GROUP BY b.id, b.dni, b.name, b.lastname " +
            "ORDER BY SUM(bc.menusAmount) DESC " +
            "LIMIT 5")
    List<TopBeneficiarySummary> findTop5ConsumersByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
