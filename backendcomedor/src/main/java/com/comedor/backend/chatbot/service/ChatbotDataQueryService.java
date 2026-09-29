package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.model.ChatbotResolution;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.PickupBeneficiaryRow;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.PickupSummary;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.ProductStockRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotDataQueryService {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");
    private static final int MAX_LISTED_PRODUCTS = 20;
    private static final int MAX_LISTED_BENEFICIARIES = 20;

    private final ChatbotReadRepository repository;
    private final ChatbotAuthorizationService authorizationService;
    private final ChatDateParser dateParser;

    @Transactional(readOnly = true)
    public ChatbotResolution productStock(String message) {
        if (!authorizationService.hasAny("PRODUCT_LIST_BY_STATUS")) {
            return denied("consultar el inventario de productos");
        }

        List<ProductStockRow> products = repository.findProducts();
        if (products.isEmpty()) {
            return ChatbotResolution.text("No hay productos registrados en el inventario.");
        }

        String normalizedMessage = ChatIntentDetector.normalize(message);
        List<ProductStockRow> matches = products.stream()
                .filter(product -> matchesProduct(normalizedMessage, product.name()))
                .sorted(Comparator.comparing(ProductStockRow::name, String.CASE_INSENSITIVE_ORDER))
                .toList();

        if (matches.isEmpty() && asksForGeneralInventory(normalizedMessage)) {
            String rows = products.stream()
                    .limit(MAX_LISTED_PRODUCTS)
                    .map(this::formatStock)
                    .collect(Collectors.joining("\n"));
            String suffix = products.size() > MAX_LISTED_PRODUCTS
                    ? "\nSe muestran 20 de " + products.size() + " productos activos. Indícame un nombre para consultar uno en particular."
                    : "";
            return ChatbotResolution.text("Inventario registrado:\n" + rows + suffix);
        }

        if (matches.isEmpty()) {
            String examples = products.stream().limit(8).map(ProductStockRow::name).collect(Collectors.joining(", "));
            return ChatbotResolution.text("No identifiqué un producto registrado en la consulta. "
                    + "Indícame su nombre. Algunos productos disponibles son: " + examples + ".");
        }

        return ChatbotResolution.text(matches.stream().map(this::formatStock).collect(Collectors.joining("\n")));
    }

    @Transactional(readOnly = true)
    public ChatbotResolution lowStock() {
        if (!authorizationService.hasAny("PRODUCT_ALERT_STOCK_VIEW")) {
            return denied("consultar las alertas de stock");
        }

        List<ProductStockRow> products = repository.findLowStockProducts();
        if (products.isEmpty()) {
            return ChatbotResolution.text("No hay productos activos con stock bajo en este momento.");
        }

        String rows = products.stream()
                .limit(MAX_LISTED_PRODUCTS)
                .map(product -> "• %s: %s %s disponibles; límite %s %s."
                        .formatted(product.name(), decimal(product.stock()), safeUnit(product.unit()),
                                decimal(product.reorderPoint()), safeUnit(product.unit())))
                .collect(Collectors.joining("\n"));
        String suffix = products.size() > MAX_LISTED_PRODUCTS
                ? "\nSe muestran 20 de " + products.size() + " productos con stock bajo."
                : "";
        return ChatbotResolution.text("Hay " + products.size() + " producto(s) con stock bajo:\n" + rows + suffix);
    }

    @Transactional(readOnly = true)
    public ChatbotResolution pickupCount(String message) {
        if (!canReadPickups()) {
            return denied("consultar los recojos de menú");
        }

        ChatDateParser.QueryPeriod period = dateParser.parse(message, LocalDate.now(LIMA));
        PickupSummary summary = period.allTime()
                ? repository.pickupSummaryAllTime()
                : repository.pickupSummary(period.date());

        if (summary.records() == 0) {
            return ChatbotResolution.text("No hay recojos de menú registrados " + period.label() + ".");
        }

        return ChatbotResolution.text("Hay %d registro(s) de recojo %s, correspondientes a %d menú(s) entregado(s)."
                .formatted(summary.records(), period.label(), summary.menus()));
    }

    @Transactional(readOnly = true)
    public ChatbotResolution pickupBeneficiaries(String message) {
        if (!canReadPickups()) {
            return denied("consultar beneficiarios con recojo registrado");
        }

        ChatDateParser.QueryPeriod period = dateParser.parse(message, LocalDate.now(LIMA));
        List<PickupBeneficiaryRow> beneficiaries = period.allTime()
                ? repository.findPickupBeneficiariesAllTime()
                : repository.findPickupBeneficiaries(period.date());

        if (beneficiaries.isEmpty()) {
            return ChatbotResolution.text("No hay beneficiarios con recojo registrado " + period.label() + ".");
        }

        String rows = beneficiaries.stream()
                .limit(MAX_LISTED_BENEFICIARIES)
                .map(item -> "• %s %s — DNI %s — %s — %d menú(s)."
                        .formatted(item.name(), item.lastname(), item.dni(), item.beneficiaryType(), item.menus()))
                .collect(Collectors.joining("\n"));
        String suffix = beneficiaries.size() > MAX_LISTED_BENEFICIARIES
                ? "\nSe muestran los primeros 20 resultados. Usa una fecha concreta para reducir la consulta."
                : "";
        return ChatbotResolution.text("Beneficiarios con recojo registrado " + period.label() + ":\n" + rows + suffix);
    }

    private boolean canReadPickups() {
        return authorizationService.hasAny(
                "MENU_REPORT_GET_BY_DATE",
                "MENU_REPORT_LIST_ALL",
                "MENU_REPORT_GET_SUMMARY",
                "MENU_REPORT_EXPORT"
        );
    }

    private ChatbotResolution denied(String action) {
        return ChatbotResolution.text("Tu usuario no tiene permisos para " + action + ". "
                + "Solicita acceso al responsable de roles y permisos.");
    }

    private boolean asksForGeneralInventory(String message) {
        return message.contains("todos") || message.contains("productos")
                || message.equals("stock") || message.contains("inventario");
    }

    private boolean matchesProduct(String message, String productName) {
        String normalizedName = ChatIntentDetector.normalize(productName);
        if (normalizedName.length() >= 3 && message.contains(normalizedName)) {
            return true;
        }
        List<String> relevantTokens = List.of(normalizedName.split(" ")).stream()
                .filter(token -> token.length() >= 4)
                .toList();
        return !relevantTokens.isEmpty() && relevantTokens.stream().allMatch(message::contains);
    }

    private String formatStock(ProductStockRow product) {
        String level = product.status() == com.comedor.backend.domain.model.enums.Status.ACTIVO
                && product.reorderPoint() != null
                && zero(product.stock()).compareTo(product.reorderPoint()) <= 0
                ? " (stock bajo)"
                : "";
        String status = product.status() == com.comedor.backend.domain.model.enums.Status.INACTIVO
                ? " (producto inactivo)"
                : "";
        return "• %s: %s %s disponibles%s%s."
                .formatted(product.name(), decimal(product.stock()), safeUnit(product.unit()), level, status);
    }

    private BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String decimal(BigDecimal value) {
        return zero(value).stripTrailingZeros().toPlainString();
    }

    private String safeUnit(String unit) {
        return unit == null || unit.isBlank() ? "unidades" : unit.toLowerCase(Locale.ROOT);
    }
}
