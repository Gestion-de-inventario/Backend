package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.PurchaseMapper;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.InventoryLotRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.domain.exceptions.DateException;
import com.comedor.backend.domain.model.InventoryLot;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-3.14 Marcar orden de compra como entregada")
class MarkPurchaseAsDeliveredServiceTest {

    @Mock
    private PurchaseRepositoryPort purchaseRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private RegisterTransactionUseCase registerTransactionUseCase;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private InventoryLotRepositoryPort inventoryLotRepository;

    private PurchaseMapper purchaseMapper;

    private ConfirmPurchaseService confirmPurchaseService;

    private ListPurchaseService listPurchaseService;

    @BeforeEach
    void setUp() {
        purchaseMapper = new PurchaseMapper();

        confirmPurchaseService = new ConfirmPurchaseService(
                purchaseRepository,
                productRepository,
                purchaseMapper,
                registerTransactionUseCase,
                currentUserService,
                inventoryLotRepository
        );

        listPurchaseService = new ListPurchaseService(
                purchaseRepository,
                purchaseMapper
        );
    }

    @Test
    @DisplayName("Escenario 1: Confirmar orden de compra pendiente")
    void confirmarCompra_pendiente_debeAumentarStockCrearLoteRegistrarTransaccionYCambiarEstadoARecibido() {
        // given
        Integer purchaseId = 1;

        Product arrozReferencia = crearProducto(1, "ARROZ", "KG", BigDecimal.ZERO);
        Product arrozPersistido = crearProducto(1, "ARROZ", "KG", new BigDecimal("5.00"));

        PurchaseDetail detail = crearDetalle(arrozReferencia, new BigDecimal("10.00"), new BigDecimal("3.00"));

        Purchase purchase = crearCompra(
                purchaseId,
                PeruTime.today(),
                StatusOrder.PENDIENTE,
                List.of(detail)
        );

        Purchase updatedPurchase = crearCompra(
                purchaseId,
                PeruTime.today(),
                StatusOrder.RECIBIDO,
                List.of(detail)
        );
        updatedPurchase.setTotalSpent(new BigDecimal("30.00"));

        User usuarioActual = new User();
        usuarioActual.setId(99);

        when(purchaseRepository.findById(purchaseId)).thenReturn(purchase);
        when(productRepository.getProductoById(1)).thenReturn(arrozPersistido);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepository.create(any(InventoryLot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(purchaseRepository.updateStatus(purchaseId, StatusOrder.RECIBIDO))
                .thenReturn(updatedPurchase);

        // when
        PurchaseResponseDTO response = confirmPurchaseService.confirm(purchaseId);

        // then
        assertEquals(purchaseId, response.getId());
        assertEquals(StatusOrder.RECIBIDO.name(), response.getStatus());
        assertEquals(new BigDecimal("30.00"), response.getTotalSpent());

        assertEquals(new BigDecimal("15.00"), arrozPersistido.getStock());

        ArgumentCaptor<InventoryLot> lotCaptor =
                ArgumentCaptor.forClass(InventoryLot.class);

        verify(inventoryLotRepository).create(lotCaptor.capture());

        InventoryLot lotCreado = lotCaptor.getValue();

        assertEquals(arrozReferencia, lotCreado.getProduct());
        assertEquals(new BigDecimal("10.00"), lotCreado.getQuantity());
        assertEquals(new BigDecimal("10.00"), lotCreado.getRemainingQuantity());
        assertEquals(new BigDecimal("3.00"), lotCreado.getUnitCost());
        assertNotNull(lotCreado.getEntryDate());

        assertEquals(lotCreado, detail.getInventoryLot());

        verify(productRepository).updateStock(arrozPersistido);
        verify(purchaseRepository).updateStatus(purchaseId, StatusOrder.RECIBIDO);

        ArgumentCaptor<TransactionRequestDTO> transactionCaptor =
                ArgumentCaptor.forClass(TransactionRequestDTO.class);

        verify(registerTransactionUseCase)
                .registrarTransaccion(transactionCaptor.capture());

        TransactionRequestDTO transaccion = transactionCaptor.getValue();

        assertEquals(TransactionReferenceType.INGREDIENTE, transaccion.getReferenceType());
        assertEquals(1, transaccion.getReferenceId());
        assertEquals("ARROZ", transaccion.getItemName());
        assertEquals(MovementType.ENTRADA, transaccion.getType());
        assertEquals(new BigDecimal("10.00"), transaccion.getAmount());
        assertEquals(new BigDecimal("5.00"), transaccion.getCurrentStock());
        assertEquals(TransactionSource.COMPRA, transaccion.getSource());
        assertEquals(99, transaccion.getUserId());
        assertNotNull(transaccion.getDateTime());
    }

    @Test
    @DisplayName("Escenario 2: Confirmar orden con varios productos")
    void confirmarCompra_conVariosProductos_debeProcesarCadaDetalle() {
        // given
        Integer purchaseId = 1;

        Product arrozReferencia = crearProducto(1, "ARROZ", "KG", BigDecimal.ZERO);
        Product aceiteReferencia = crearProducto(2, "ACEITE", "L", BigDecimal.ZERO);

        Product arrozPersistido = crearProducto(1, "ARROZ", "KG", new BigDecimal("5.00"));
        Product aceitePersistido = crearProducto(2, "ACEITE", "L", new BigDecimal("2.00"));

        PurchaseDetail detalleArroz =
                crearDetalle(arrozReferencia, new BigDecimal("10.00"), new BigDecimal("3.00"));

        PurchaseDetail detalleAceite =
                crearDetalle(aceiteReferencia, new BigDecimal("4.00"), new BigDecimal("8.00"));

        Purchase purchase = crearCompra(
                purchaseId,
                PeruTime.today(),
                StatusOrder.PENDIENTE,
                List.of(detalleArroz, detalleAceite)
        );

        Purchase updatedPurchase = crearCompra(
                purchaseId,
                PeruTime.today(),
                StatusOrder.RECIBIDO,
                List.of(detalleArroz, detalleAceite)
        );
        updatedPurchase.setTotalSpent(new BigDecimal("62.00"));

        User usuarioActual = new User();
        usuarioActual.setId(99);

        when(purchaseRepository.findById(purchaseId)).thenReturn(purchase);
        when(productRepository.getProductoById(1)).thenReturn(arrozPersistido);
        when(productRepository.getProductoById(2)).thenReturn(aceitePersistido);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepository.create(any(InventoryLot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(purchaseRepository.updateStatus(purchaseId, StatusOrder.RECIBIDO))
                .thenReturn(updatedPurchase);

        // when
        PurchaseResponseDTO response = confirmPurchaseService.confirm(purchaseId);

        // then
        assertEquals(StatusOrder.RECIBIDO.name(), response.getStatus());

        assertEquals(new BigDecimal("15.00"), arrozPersistido.getStock());
        assertEquals(new BigDecimal("6.00"), aceitePersistido.getStock());

        verify(inventoryLotRepository, times(2)).create(any(InventoryLot.class));
        verify(productRepository).updateStock(arrozPersistido);
        verify(productRepository).updateStock(aceitePersistido);
        verify(registerTransactionUseCase, times(2)).registrarTransaccion(any(TransactionRequestDTO.class));
        verify(purchaseRepository).updateStatus(purchaseId, StatusOrder.RECIBIDO);
    }

    @Test
    @DisplayName("Escenario 3: Rechazar orden de compra futura")
    void confirmarCompra_conFechaFutura_debeLanzarDateExceptionSinModificarStockNiEstado() {
        // given
        Integer purchaseId = 1;

        Purchase purchase = crearCompra(
                purchaseId,
                PeruTime.today().plusDays(1),
                StatusOrder.PENDIENTE,
                List.of()
        );

        when(purchaseRepository.findById(purchaseId)).thenReturn(purchase);

        // when
        DateException exception = assertThrows(
                DateException.class,
                () -> confirmPurchaseService.confirm(purchaseId)
        );

        // then
        assertEquals(
                "Error al confirmar orden : No se puede marcar como recibido una orden de compra futura",
                exception.getMessage()
        );

        verify(purchaseRepository).findById(purchaseId);
        verify(purchaseRepository, never()).updateStatus(any(Integer.class), any(StatusOrder.class));
        verifyNoInteractions(productRepository);
        verifyNoInteractions(inventoryLotRepository);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(currentUserService);
    }

    @Test
    @DisplayName("Escenario 4: Rechazar orden ya confirmada")
    void confirmarCompra_yaRecibida_debeLanzarRuntimeExceptionSinModificarStock() {
        // given
        Integer purchaseId = 1;

        Product arroz = crearProducto(1, "ARROZ", "KG", BigDecimal.ZERO);

        Purchase purchase = crearCompra(
                purchaseId,
                PeruTime.today(),
                StatusOrder.RECIBIDO,
                List.of(crearDetalle(arroz, new BigDecimal("10.00"), new BigDecimal("3.00")))
        );

        when(purchaseRepository.findById(purchaseId)).thenReturn(purchase);

        // when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> confirmPurchaseService.confirm(purchaseId)
        );

        // then
        assertEquals("La orden ya fue confirmada", exception.getMessage());

        verify(purchaseRepository).findById(purchaseId);
        verify(purchaseRepository, never()).updateStatus(any(Integer.class), any(StatusOrder.class));
        verifyNoInteractions(productRepository);
        verifyNoInteractions(inventoryLotRepository);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(currentUserService);
    }

    @Test
    @DisplayName("Escenario 5: Listar órdenes con filtros y orden descendente")
    void listarCompras_conFiltros_debeEnviarFiltrosYOrdenDescendenteAlRepositorio() {
        // given
        int page = 0;
        int size = 5;
        LocalDate startDate = PeruTime.today().minusDays(7);
        LocalDate endDate = PeruTime.today();
        StatusOrder status = StatusOrder.PENDIENTE;

        Purchase compraReciente = crearCompra(2, endDate, StatusOrder.PENDIENTE, List.of());
        compraReciente.setTotalSpent(new BigDecimal("50.00"));

        Purchase compraAntigua = crearCompra(1, startDate, StatusOrder.PENDIENTE, List.of());
        compraAntigua.setTotalSpent(new BigDecimal("30.00"));

        Page<Purchase> pageResult =
                new PageImpl<>(List.of(compraReciente, compraAntigua));

        when(purchaseRepository.showPurchase(
                eq(startDate),
                eq(endDate),
                eq(status),
                any(Pageable.class)
        )).thenReturn(pageResult);

        // when
        Page<PurchaseResponseDTO> response =
                listPurchaseService.list(page, size, startDate, endDate, status);

        // then
        assertEquals(2, response.getContent().size());
        assertEquals(2, response.getContent().get(0).getId());
        assertEquals(1, response.getContent().get(1).getId());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(purchaseRepository).showPurchase(
                eq(startDate),
                eq(endDate),
                eq(status),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());

        Sort.Order purchaseDateOrder =
                pageable.getSort().getOrderFor("purchaseDate");

        Sort.Order idOrder =
                pageable.getSort().getOrderFor("id");

        assertNotNull(purchaseDateOrder);
        assertNotNull(idOrder);

        assertEquals(Sort.Direction.DESC, purchaseDateOrder.getDirection());
        assertEquals(Sort.Direction.DESC, idOrder.getDirection());
    }

    private Purchase crearCompra(
            Integer id,
            LocalDate purchaseDate,
            StatusOrder status,
            List<PurchaseDetail> details
    ) {
        Purchase purchase = new Purchase();
        purchase.setId(id);
        purchase.setPurchaseDate(purchaseDate);
        purchase.setStatus(status);
        purchase.setDetails(details);

        BigDecimal total = details.stream()
                .map(PurchaseDetail::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        purchase.setTotalSpent(total);

        return purchase;
    }

    private PurchaseDetail crearDetalle(
            Product product,
            BigDecimal quantity,
            BigDecimal unitPrice
    ) {
        PurchaseDetail detail = new PurchaseDetail();
        detail.setProduct(product);
        detail.setQuantity(quantity);
        detail.setUnitPrice(unitPrice);
        detail.setSubTotal(quantity.multiply(unitPrice));
        return detail;
    }

    private Product crearProducto(
            Integer id,
            String name,
            String unit,
            BigDecimal stock
    ) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setUnit(unit);
        product.setStock(stock);
        product.setStatus(Status.ACTIVO);
        return product;
    }
}