package com.comedor.backend.chatbot.persistence;

import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.domain.model.enums.StatusMenuReport;
import com.comedor.backend.domain.model.enums.StatusOrder;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Consultas de SOLO LECTURA que usa el asistente. Todas están escritas a mano: el asistente
 * nunca genera SQL, solo elige cuál de estas consultas ejecutar y con qué parámetros.
 *
 * Los rangos de fechas son inclusivos; null en ambos extremos significa "todo el historial".
 */
@Repository
@RequiredArgsConstructor
public class ChatbotReadRepository {

    private static final LocalDate MIN = LocalDate.of(1900, 1, 1);
    private static final LocalDate MAX = LocalDate.of(9999, 12, 31);

    private final EntityManager entityManager;

    // ---------------------------------------------------------------- productos

    public List<ProductStockRow> findProducts() {
        return entityManager.createQuery("""
                        SELECT p.id, p.name, p.unit, p.stock, p.reorderPoint, p.status
                        FROM ProductEntity p
                        ORDER BY p.name
                        """, Object[].class)
                .getResultList().stream()
                .map(ChatbotReadRepository::toProductStockRow)
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
                .map(ChatbotReadRepository::toProductStockRow)
                .toList();
    }

    /** Cuánto se usó de cada producto en los menús del periodo, de mayor a menor. */
    public List<ConsumptionRow> consumption(LocalDate start, LocalDate end, Integer productId) {
        var query = entityManager.createQuery("""
                        SELECT p.id, p.name, p.unit, COALESCE(SUM(m.quantityUsed), 0), COUNT(DISTINCT m.menuReport.id)
                        FROM StockMovementEntity m
                        JOIN m.inventoryLot lot
                        JOIN lot.product p
                        WHERE m.movementDate >= :start AND m.movementDate < :end
                        """ + (productId != null ? " AND p.id = :productId " : "") + """
                        GROUP BY p.id, p.name, p.unit
                        ORDER BY COALESCE(SUM(m.quantityUsed), 0) DESC
                        """, Object[].class)
                .setParameter("start", from(start).atStartOfDay())
                .setParameter("end", to(end).plusDays(1).atStartOfDay());
        if (productId != null) {
            query.setParameter("productId", productId);
        }
        return query.setMaxResults(30)
                .getResultList().stream()
                .map(r -> new ConsumptionRow(((Number) r[0]).intValue(), (String) r[1], (String) r[2],
                        (BigDecimal) r[3], ((Number) r[4]).longValue()))
                .toList();
    }

    // ---------------------------------------------------------------- recojos y beneficiarios

    public PickupSummary pickupSummary(LocalDate start, LocalDate end) {
        Object[] row = entityManager.createQuery("""
                        SELECT COUNT(control.id), COALESCE(SUM(control.menusAmount), 0)
                        FROM BeneficiaryControlEntity control
                        WHERE control.received = true
                          AND control.report.date BETWEEN :start AND :end
                        """, Object[].class)
                .setParameter("start", from(start))
                .setParameter("end", to(end))
                .getSingleResult();
        return new PickupSummary(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
    }

    public List<PickupBeneficiaryRow> pickupBeneficiaries(LocalDate start, LocalDate end) {
        return entityManager.createQuery("""
                        SELECT beneficiary.id, beneficiary.dni, beneficiary.name, beneficiary.lastname,
                               beneficiary.beneficiaryType.name, SUM(control.menusAmount), MAX(control.report.date)
                        FROM BeneficiaryControlEntity control
                        JOIN control.beneficiary beneficiary
                        WHERE control.received = true
                          AND control.report.date BETWEEN :start AND :end
                        GROUP BY beneficiary.id, beneficiary.dni, beneficiary.name,
                                 beneficiary.lastname, beneficiary.beneficiaryType.name
                        ORDER BY beneficiary.lastname, beneficiary.name
                        """, Object[].class)
                .setParameter("start", from(start))
                .setParameter("end", to(end))
                .setMaxResults(60)
                .getResultList().stream()
                .map(r -> new PickupBeneficiaryRow(((Number) r[0]).intValue(), (String) r[1], (String) r[2],
                        (String) r[3], (String) r[4], ((Number) r[5]).longValue(), (LocalDate) r[6]))
                .toList();
    }

    public List<BeneficiaryRow> findBeneficiaries() {
        return entityManager.createQuery("""
                        SELECT b.id, b.dni, b.name, b.lastname, t.name, b.status
                        FROM BeneficiaryEntity b
                        LEFT JOIN b.beneficiaryType t
                        ORDER BY b.lastname, b.name
                        """, Object[].class)
                .getResultList().stream()
                .map(ChatbotReadRepository::toBeneficiaryRow)
                .toList();
    }

    public Optional<BeneficiaryRow> findBeneficiaryByDni(String dni) {
        return entityManager.createQuery("""
                        SELECT b.id, b.dni, b.name, b.lastname, t.name, b.status
                        FROM BeneficiaryEntity b
                        LEFT JOIN b.beneficiaryType t
                        WHERE b.dni = :dni
                        """, Object[].class)
                .setParameter("dni", dni)
                .setMaxResults(1)
                .getResultList().stream()
                .map(ChatbotReadRepository::toBeneficiaryRow)
                .findFirst();
    }

    public Optional<LocalDate> lastPickup(int beneficiaryId) {
        return Optional.ofNullable(entityManager.createQuery("""
                        SELECT MAX(control.report.date)
                        FROM BeneficiaryControlEntity control
                        WHERE control.received = true AND control.beneficiary.id = :id
                        """, LocalDate.class)
                .setParameter("id", beneficiaryId)
                .getSingleResult());
    }

    // ---------------------------------------------------------------- menús preparados

    public List<MenuReportRow> menuReports(LocalDate start, LocalDate end) {
        return entityManager.createQuery("""
                        SELECT r.date, d.name, r.quantityPrepared, r.quantityRemaining, r.totalEarned, r.status
                        FROM MenuReportEntity r
                        LEFT JOIN r.dishMenu d
                        WHERE r.date BETWEEN :start AND :end
                        ORDER BY r.date DESC
                        """, Object[].class)
                .setParameter("start", from(start))
                .setParameter("end", to(end))
                .setMaxResults(40)
                .getResultList().stream()
                .map(r -> new MenuReportRow((LocalDate) r[0], (String) r[1], intValue(r[2]), intValue(r[3]),
                        (BigDecimal) r[4], (StatusMenuReport) r[5]))
                .toList();
    }

    // ---------------------------------------------------------------- compras y donaciones

    public List<OrderRow> purchases(LocalDate start, LocalDate end) {
        return entityManager.createQuery("""
                        SELECT p.id, p.purchaseDate, p.status, p.totalSpent, SIZE(p.details)
                        FROM PurchaseEntity p
                        WHERE p.purchaseDate BETWEEN :start AND :end
                        ORDER BY p.purchaseDate DESC, p.id DESC
                        """, Object[].class)
                .setParameter("start", from(start))
                .setParameter("end", to(end))
                .setMaxResults(40)
                .getResultList().stream()
                .map(r -> new OrderRow(((Number) r[0]).intValue(), (LocalDate) r[1], (StatusOrder) r[2],
                        (BigDecimal) r[3], intValue(r[4])))
                .toList();
    }

    public List<OrderRow> donations(LocalDate start, LocalDate end) {
        return entityManager.createQuery("""
                        SELECT d.id, d.donationDate, d.status, SIZE(d.details)
                        FROM DonationEntity d
                        WHERE d.donationDate BETWEEN :start AND :end
                        ORDER BY d.donationDate DESC, d.id DESC
                        """, Object[].class)
                .setParameter("start", from(start))
                .setParameter("end", to(end))
                .setMaxResults(40)
                .getResultList().stream()
                .map(r -> new OrderRow(((Number) r[0]).intValue(), (LocalDate) r[1], (StatusOrder) r[2],
                        null, intValue(r[3])))
                .toList();
    }

    /** Productos que entraron por compras o donaciones (no canceladas) en el periodo. */
    public List<ProductQuantityRow> incomingProducts(LocalDate start, LocalDate end, boolean donations) {
        String query = donations ? """
                SELECT p.name, p.unit, SUM(item.quantity)
                FROM DonationDetailEntity item
                JOIN item.donation o
                JOIN item.product p
                WHERE o.donationDate BETWEEN :start AND :end
                  AND o.status <> com.comedor.backend.domain.model.enums.StatusOrder.CANCELADO
                GROUP BY p.name, p.unit
                ORDER BY SUM(item.quantity) DESC
                """ : """
                SELECT p.name, p.unit, SUM(item.quantity)
                FROM PurchaseDetailEntity item
                JOIN item.purchase o
                JOIN item.product p
                WHERE o.purchaseDate BETWEEN :start AND :end
                  AND o.status <> com.comedor.backend.domain.model.enums.StatusOrder.CANCELADO
                GROUP BY p.name, p.unit
                ORDER BY SUM(item.quantity) DESC
                """;
        return entityManager.createQuery(query, Object[].class)
                .setParameter("start", from(start))
                .setParameter("end", to(end))
                .setMaxResults(15)
                .getResultList().stream()
                .map(r -> new ProductQuantityRow((String) r[0], (String) r[1], (BigDecimal) r[2]))
                .toList();
    }

    // ---------------------------------------------------------------- utilidades

    private static LocalDate from(LocalDate date) {
        return date == null ? MIN : date;
    }

    private static LocalDate to(LocalDate date) {
        return date == null ? MAX : date;
    }

    private static int intValue(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }

    private static ProductStockRow toProductStockRow(Object[] row) {
        return new ProductStockRow(((Number) row[0]).intValue(), (String) row[1], (String) row[2],
                (BigDecimal) row[3], (BigDecimal) row[4], (Status) row[5]);
    }

    private static BeneficiaryRow toBeneficiaryRow(Object[] row) {
        return new BeneficiaryRow(((Number) row[0]).intValue(), (String) row[1], (String) row[2],
                (String) row[3], (String) row[4], (Status) row[5]);
    }

    public record ProductStockRow(int id, String name, String unit, BigDecimal stock, BigDecimal reorderPoint,
                                  Status status) {
    }

    public record ConsumptionRow(int productId, String name, String unit, BigDecimal quantity, long menus) {
    }

    public record PickupSummary(long records, long menus) {
    }

    public record PickupBeneficiaryRow(int id, String dni, String name, String lastname, String beneficiaryType,
                                       long menus, LocalDate lastDate) {
    }

    public record BeneficiaryRow(int id, String dni, String name, String lastname, String type, Status status) {
    }

    public record MenuReportRow(LocalDate date, String dish, int prepared, int remaining, BigDecimal earned,
                                StatusMenuReport status) {
    }

    public record OrderRow(int id, LocalDate date, StatusOrder status, BigDecimal total, int items) {
    }

    public record ProductQuantityRow(String name, String unit, BigDecimal quantity) {
    }
}
