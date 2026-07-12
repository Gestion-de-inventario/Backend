package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.PurchaseMapper;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.domain.exceptions.DateException;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreatePurchaseDetailRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreatePurchaseRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-3.6 Creación de orden de compra")
class CreatePurchaseOrderServiceTest {

    @Mock
    private PurchaseRepositoryPort purchaseRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    private PurchaseMapper purchaseMapper;

    private CreatePurchaseService service;

    @BeforeEach
    void setUp() {
        purchaseMapper = new PurchaseMapper();

        service = new CreatePurchaseService(
                purchaseRepository,
                productRepository,
                purchaseMapper
        );
    }

    @Test
    @DisplayName("Escenario 1: Crear orden de compra con un producto")
    void crearOrdenCompra_conUnProducto_debeCrearOrdenPendienteConSubtotalYTotal() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");

        CreatePurchaseRequestDTO request = crearRequest(
                fecha,
                List.of(crearDetalleRequest(1, new BigDecimal("5"), new BigDecimal("4.00")))
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(purchaseRepository.save(any(Purchase.class)))
                .thenAnswer(invocation -> {
                    Purchase purchase = invocation.getArgument(0);
                    purchase.setId(100);
                    return purchase;
                });

        // when
        PurchaseResponseDTO response = service.create(request);

        // then
        assertEquals(100, response.getId());
        assertEquals(fecha, response.getPurchaseDate());
        assertEquals(StatusOrder.PENDIENTE.name(), response.getStatus());
        assertEquals(new BigDecimal("20.00"), response.getTotalSpent());

        assertEquals(1, response.getDetails().size());
        assertEquals(1, response.getDetails().get(0).getProductId());
        assertEquals("ARROZ", response.getDetails().get(0).getProductName());
        assertEquals("KG", response.getDetails().get(0).getProductUnit());
        assertEquals(new BigDecimal("5"), response.getDetails().get(0).getQuantity());
        assertEquals(new BigDecimal("4.00"), response.getDetails().get(0).getUnitPrice());
        assertEquals(new BigDecimal("20.00"), response.getDetails().get(0).getSubTotal());

        ArgumentCaptor<Purchase> captor =
                ArgumentCaptor.forClass(Purchase.class);

        verify(purchaseRepository).save(captor.capture());

        Purchase purchaseCreada = captor.getValue();

        assertEquals(fecha, purchaseCreada.getPurchaseDate());
        assertEquals(StatusOrder.PENDIENTE, purchaseCreada.getStatus());
        assertEquals(new BigDecimal("20.00"), purchaseCreada.getTotalSpent());
        assertEquals(1, purchaseCreada.getDetails().size());

        PurchaseDetail detail = purchaseCreada.getDetails().get(0);

        assertSame(arroz, detail.getProduct());
        assertEquals(new BigDecimal("5"), detail.getQuantity());
        assertEquals(new BigDecimal("4.00"), detail.getUnitPrice());
        assertEquals(new BigDecimal("20.00"), detail.getSubTotal());
    }

    @Test
    @DisplayName("Escenario 2: Crear orden de compra con varios productos")
    void crearOrdenCompra_conVariosProductos_debeCalcularSubtotalesYTotalGastado() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");
        Product aceite = crearProducto(2, "ACEITE", "L");

        CreatePurchaseRequestDTO request = crearRequest(
                fecha,
                List.of(
                        crearDetalleRequest(1, new BigDecimal("10"), new BigDecimal("3.50")),
                        crearDetalleRequest(2, new BigDecimal("2"), new BigDecimal("8.00"))
                )
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(productRepository.getProductoById(2)).thenReturn(aceite);
        when(purchaseRepository.save(any(Purchase.class)))
                .thenAnswer(invocation -> {
                    Purchase purchase = invocation.getArgument(0);
                    purchase.setId(101);
                    return purchase;
                });

        // when
        PurchaseResponseDTO response = service.create(request);

        // then
        assertEquals(101, response.getId());
        assertEquals(StatusOrder.PENDIENTE.name(), response.getStatus());
        assertEquals(new BigDecimal("51.00"), response.getTotalSpent());

        assertEquals(2, response.getDetails().size());

        assertEquals(1, response.getDetails().get(0).getProductId());
        assertEquals(new BigDecimal("35.00"), response.getDetails().get(0).getSubTotal());

        assertEquals(2, response.getDetails().get(1).getProductId());
        assertEquals(new BigDecimal("16.00"), response.getDetails().get(1).getSubTotal());

        ArgumentCaptor<Purchase> captor =
                ArgumentCaptor.forClass(Purchase.class);

        verify(purchaseRepository).save(captor.capture());

        Purchase purchaseCreada = captor.getValue();

        assertEquals(new BigDecimal("51.00"), purchaseCreada.getTotalSpent());
        assertEquals(2, purchaseCreada.getDetails().size());
    }

    @Test
    @DisplayName("Escenario 3: Crear orden desde productos faltantes precargados y ajustados")
    void crearOrdenCompra_desdeProductosFaltantesPrecargados_debeUsarCantidadesYPreciosDelRequestAjustado() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");
        Product fideo = crearProducto(2, "FIDEO", "KG");

        CreatePurchaseRequestDTO request = crearRequest(
                fecha,
                List.of(
                        crearDetalleRequest(1, new BigDecimal("15"), new BigDecimal("3.20")),
                        crearDetalleRequest(2, new BigDecimal("8"), new BigDecimal("4.50"))
                )
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(productRepository.getProductoById(2)).thenReturn(fideo);
        when(purchaseRepository.save(any(Purchase.class)))
                .thenAnswer(invocation -> {
                    Purchase purchase = invocation.getArgument(0);
                    purchase.setId(102);
                    return purchase;
                });

        // when
        PurchaseResponseDTO response = service.create(request);

        // then
        assertEquals(102, response.getId());
        assertEquals(new BigDecimal("84.00"), response.getTotalSpent());

        assertEquals(2, response.getDetails().size());

        assertEquals(1, response.getDetails().get(0).getProductId());
        assertEquals(new BigDecimal("15"), response.getDetails().get(0).getQuantity());
        assertEquals(new BigDecimal("3.20"), response.getDetails().get(0).getUnitPrice());
        assertEquals(new BigDecimal("48.00"), response.getDetails().get(0).getSubTotal());

        assertEquals(2, response.getDetails().get(1).getProductId());
        assertEquals(new BigDecimal("8"), response.getDetails().get(1).getQuantity());
        assertEquals(new BigDecimal("4.50"), response.getDetails().get(1).getUnitPrice());
        assertEquals(new BigDecimal("36.00"), response.getDetails().get(1).getSubTotal());
    }

    @Test
    @DisplayName("Escenario 4: Asociar cada detalle con la orden de compra")
    void crearOrdenCompra_conDetalles_debeAsociarCadaDetalleALaCompra() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");
        Product aceite = crearProducto(2, "ACEITE", "L");

        CreatePurchaseRequestDTO request = crearRequest(
                fecha,
                List.of(
                        crearDetalleRequest(1, new BigDecimal("4"), new BigDecimal("5.00")),
                        crearDetalleRequest(2, new BigDecimal("3"), new BigDecimal("7.00"))
                )
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(productRepository.getProductoById(2)).thenReturn(aceite);
        when(purchaseRepository.save(any(Purchase.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        service.create(request);

        // then
        ArgumentCaptor<Purchase> captor =
                ArgumentCaptor.forClass(Purchase.class);

        verify(purchaseRepository).save(captor.capture());

        Purchase purchaseCreada = captor.getValue();

        assertEquals(2, purchaseCreada.getDetails().size());

        for (PurchaseDetail detail : purchaseCreada.getDetails()) {
            assertSame(purchaseCreada, detail.getPurchase());
        }
    }

    @Test
    @DisplayName("Escenario 5: Rechazar orden con fecha anterior")
    void crearOrdenCompra_conFechaAnterior_debeLanzarDateExceptionYNoGuardar() {
        // given
        CreatePurchaseRequestDTO request = crearRequest(
                PeruTime.today().minusDays(1),
                List.of(crearDetalleRequest(1, new BigDecimal("5"), new BigDecimal("4.00")))
        );

        // when
        DateException exception = assertThrows(
                DateException.class,
                () -> service.create(request)
        );

        // then
        assertEquals(
                "Error al crear orden : La fecha de creación no puede ser menor a la actual ",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
        verifyNoInteractions(purchaseRepository);
    }

    @Test
    @DisplayName("Escenario 6: Crear orden pendiente sin modificar stock de productos")
    void crearOrdenCompra_pendiente_noDebeModificarStockDeProductos() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");
        arroz.setStock(new BigDecimal("100.00"));

        CreatePurchaseRequestDTO request = crearRequest(
                fecha,
                List.of(crearDetalleRequest(1, new BigDecimal("10"), new BigDecimal("3.00")))
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(purchaseRepository.save(any(Purchase.class)))
                .thenAnswer(invocation -> {
                    Purchase purchase = invocation.getArgument(0);
                    purchase.setId(103);
                    return purchase;
                });

        // when
        PurchaseResponseDTO response = service.create(request);

        // then
        assertEquals(StatusOrder.PENDIENTE.name(), response.getStatus());
        assertEquals(new BigDecimal("100.00"), arroz.getStock());

        verify(productRepository).getProductoById(1);
        verify(productRepository, never()).updateStock(any(Product.class));
        verify(productRepository, never()).updateProducto(any(Product.class));
    }

    private CreatePurchaseRequestDTO crearRequest(
            LocalDate date,
            List<CreatePurchaseDetailRequestDTO> details
    ) {
        CreatePurchaseRequestDTO request = new CreatePurchaseRequestDTO();
        request.setDate(date);
        request.setDetails(details);
        return request;
    }

    private CreatePurchaseDetailRequestDTO crearDetalleRequest(
            Integer productId,
            BigDecimal quantity,
            BigDecimal unitPrice
    ) {
        CreatePurchaseDetailRequestDTO detail = new CreatePurchaseDetailRequestDTO();
        detail.setProductId(productId);
        detail.setQuantity(quantity);
        detail.setUnitPrice(unitPrice);
        return detail;
    }

    private Product crearProducto(
            Integer id,
            String name,
            String unit
    ) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setUnit(unit);
        product.setStatus(Status.ACTIVO);
        product.setStock(BigDecimal.ZERO);
        return product;
    }
}