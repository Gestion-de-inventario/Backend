package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.persistence.ChatbotReadRepository;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.PickupSummary;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.ProductStockRow;
import com.comedor.backend.domain.model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotDataQueryServiceTest {

    @Mock
    private ChatbotReadRepository repository;
    @Mock
    private ChatbotAuthorizationService authorizationService;
    @Mock
    private ChatDateParser dateParser;

    private ChatbotDataQueryService service;

    @BeforeEach
    void setUp() {
        service = new ChatbotDataQueryService(repository, authorizationService, dateParser);
    }

    @Test
    void returnsStockForAnyRegisteredActiveProduct() {
        when(authorizationService.hasAny("PRODUCT_LIST_BY_STATUS")).thenReturn(true);
        when(repository.findProducts()).thenReturn(List.of(
                new ProductStockRow(1, "Arroz extra", "KG", new BigDecimal("18.500"), new BigDecimal("5"), Status.ACTIVO),
                new ProductStockRow(2, "Aceite", "L", new BigDecimal("3"), new BigDecimal("4"), Status.INACTIVO)
        ));

        var answer = service.productStock("¿Cuánto stock de arroz extra hay?");
        var inventory = service.productStock("muéstrame el inventario");

        assertTrue(answer.reply().contains("Arroz extra: 18.5 kg"));
        assertTrue(inventory.reply().contains("Aceite: 3 l disponibles (producto inactivo)"));
    }

    @Test
    void doesNotReadProductsWithoutPermission() {
        when(authorizationService.hasAny("PRODUCT_LIST_BY_STATUS")).thenReturn(false);

        var answer = service.productStock("stock de arroz");

        assertTrue(answer.reply().contains("no tiene permisos"));
        verify(repository, never()).findProducts();
    }

    @Test
    void listsLowStockUsingTheSpecificAlertPermission() {
        when(authorizationService.hasAny("PRODUCT_ALERT_STOCK_VIEW")).thenReturn(true);
        when(repository.findLowStockProducts()).thenReturn(List.of(
                new ProductStockRow(2, "Aceite", "L", new BigDecimal("3"), new BigDecimal("4"), Status.ACTIVO)
        ));

        var answer = service.lowStock();

        assertTrue(answer.reply().contains("Aceite: 3 l disponibles; límite 4 l"));
    }

    @Test
    void reportsPickupRecordsAndMenusSeparately() {
        when(authorizationService.hasAny(any(String[].class))).thenReturn(true);
        LocalDate date = LocalDate.of(2026, 9, 29);
        when(dateParser.parse(any(), any())).thenReturn(new ChatDateParser.QueryPeriod(date, false));
        when(repository.pickupSummary(date)).thenReturn(new PickupSummary(12, 17));

        var answer = service.pickupCount("recojos de hoy");

        assertTrue(answer.reply().contains("12 registro(s)"));
        assertTrue(answer.reply().contains("17 menú(s)"));
    }
}
