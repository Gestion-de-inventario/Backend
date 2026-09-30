package com.comedor.backend.chatbot.persistence;

import com.comedor.backend.domain.model.enums.Status;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatbotReadRepository {

    private final EntityManager entityManager;

    public List<ProductStockRow> findProducts() {
        return entityManager.createQuery("""
                        SELECT p.id, p.name, p.unit, p.stock, p.reorderPoint, p.status
                        FROM ProductEntity p
                        ORDER BY p.name
                        """, Object[].class)
                .getResultList().stream()
                .map(this::toProductStockRow)
                .toList();
    }

    public List<ProductStockRow> findLowStockProducts() {
        return entityManager.createQuery("""
                        SELECT p.id, p.name, p.unit, p.stock, p.reorderPoint, p.status
                        FROM ProductEntity p
                        WHERE p.status = com.comedor.backend.domain.model.enums.Status.ACTIVO
                          AND COALESCE(p.stock, 0) <= COALESCE(p.reorderPoint, 0)
                        ORDER BY p.stock ASC, p.name ASC
                        """, Object[].class)
                .getResultList().stream()
                .map(this::toProductStockRow)
                .toList();
    }

    public PickupSummary pickupSummary(LocalDate date) {
        Object[] row = entityManager.createQuery("""
                        SELECT COUNT(control.id), COALESCE(SUM(control.menusAmount), 0)
                        FROM BeneficiaryControlEntity control
                        WHERE control.received = true
                          AND control.report.date = :date
                        """, Object[].class)
                .setParameter("date", date)
                .getSingleResult();
        return new PickupSummary(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
    }

    public PickupSummary pickupSummaryAllTime() {
        Object[] row = entityManager.createQuery("""
                        SELECT COUNT(control.id), COALESCE(SUM(control.menusAmount), 0)
                        FROM BeneficiaryControlEntity control
                        WHERE control.received = true
                        """, Object[].class)
                .getSingleResult();
        return new PickupSummary(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
    }

    public List<PickupBeneficiaryRow> findPickupBeneficiaries(LocalDate date) {
        return entityManager.createQuery("""
                        SELECT beneficiary.id, beneficiary.dni, beneficiary.name, beneficiary.lastname,
                               beneficiary.beneficiaryType.name, SUM(control.menusAmount)
                        FROM BeneficiaryControlEntity control
                        JOIN control.beneficiary beneficiary
                        WHERE control.received = true
                          AND control.report.date = :date
                        GROUP BY beneficiary.id, beneficiary.dni, beneficiary.name,
                                 beneficiary.lastname, beneficiary.beneficiaryType.name
                        ORDER BY beneficiary.lastname, beneficiary.name
                        """, Object[].class)
                .setParameter("date", date)
                .setMaxResults(50)
                .getResultList().stream()
                .map(this::toPickupBeneficiaryRow)
                .toList();
    }

    public List<PickupBeneficiaryRow> findPickupBeneficiariesAllTime() {
        return entityManager.createQuery("""
                        SELECT beneficiary.id, beneficiary.dni, beneficiary.name, beneficiary.lastname,
                               beneficiary.beneficiaryType.name, SUM(control.menusAmount)
                        FROM BeneficiaryControlEntity control
                        JOIN control.beneficiary beneficiary
                        WHERE control.received = true
                        GROUP BY beneficiary.id, beneficiary.dni, beneficiary.name,
                                 beneficiary.lastname, beneficiary.beneficiaryType.name
                        ORDER BY beneficiary.lastname, beneficiary.name
                        """, Object[].class)
                .setMaxResults(50)
                .getResultList().stream()
                .map(this::toPickupBeneficiaryRow)
                .toList();
    }

    private ProductStockRow toProductStockRow(Object[] row) {
        return new ProductStockRow(
                ((Number) row[0]).intValue(),
                (String) row[1],
                (String) row[2],
                (BigDecimal) row[3],
                (BigDecimal) row[4],
                (Status) row[5]
        );
    }

    private PickupBeneficiaryRow toPickupBeneficiaryRow(Object[] row) {
        return new PickupBeneficiaryRow(
                ((Number) row[0]).intValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                ((Number) row[5]).longValue()
        );
    }

    public record ProductStockRow(
            int id,
            String name,
            String unit,
            BigDecimal stock,
            BigDecimal reorderPoint,
            Status status
    ) {
    }

    public record PickupSummary(long records, long menus) {
    }

    public record PickupBeneficiaryRow(
            int id,
            String dni,
            String name,
            String lastname,
            String beneficiaryType,
            Long menus
    ) {
    }
}
