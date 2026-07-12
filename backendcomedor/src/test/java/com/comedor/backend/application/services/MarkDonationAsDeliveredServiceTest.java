package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DonationMapper;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.DonationRepositoryPort;
import com.comedor.backend.application.ports.out.InventoryLotRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.DateException;
import com.comedor.backend.domain.model.Donation;
import com.comedor.backend.domain.model.DonationDetail;
import com.comedor.backend.domain.model.InventoryLot;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-3.18 Marcar orden de donación como entregada")
class MarkDonationAsDeliveredServiceTest {

    @Mock
    private DonationRepositoryPort donationRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private RegisterTransactionUseCase registerTransactionUseCase;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private InventoryLotRepositoryPort inventoryLotRepository;

    private DonationMapper donationMapper;

    private ConfirmDonationService service;

    @BeforeEach
    void setUp() {
        donationMapper = new DonationMapper();

        service = new ConfirmDonationService(
                donationRepository,
                donationMapper,
                productRepository,
                registerTransactionUseCase,
                currentUserService,
                inventoryLotRepository
        );
    }

    @Test
    @DisplayName("Escenario 1: Confirmar donación pendiente")
    void confirmarDonacion_pendiente_debeAumentarStockCrearLoteRegistrarTransaccionYCambiarEstadoARecibido() {
        // given
        Integer donationId = 1;

        Product arrozReferencia = crearProducto(1, "ARROZ", "KG", BigDecimal.ZERO);
        Product arrozPersistido = crearProducto(1, "ARROZ", "KG", new BigDecimal("5.00"));

        DonationDetail detail =
                crearDetalle(arrozReferencia, new BigDecimal("10.00"));

        Donation donation = crearDonacion(
                donationId,
                PeruTime.today(),
                StatusOrder.PENDIENTE,
                List.of(detail)
        );

        Donation updatedDonation = crearDonacion(
                donationId,
                PeruTime.today(),
                StatusOrder.RECIBIDO,
                List.of(detail)
        );

        User usuarioActual = new User();
        usuarioActual.setId(99);

        when(donationRepository.findById(donationId)).thenReturn(donation);
        when(productRepository.getProductoById(1)).thenReturn(arrozPersistido);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepository.create(any(InventoryLot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(donationRepository.changeStatus(donationId, StatusOrder.RECIBIDO))
                .thenReturn(updatedDonation);

        // when
        DonationResponseDTO response = service.confirm(donationId);

        // then
        assertEquals(donationId, response.getId());
        assertEquals(StatusOrder.RECIBIDO.name(), response.getStatus());
        assertEquals(1, response.getDetails().size());
        assertEquals(1, response.getDetails().get(0).getProductId());
        assertEquals("ARROZ", response.getDetails().get(0).getProductName());
        assertEquals(new BigDecimal("10.00"), response.getDetails().get(0).getQuantity());

        assertEquals(new BigDecimal("15.00"), arrozPersistido.getStock());

        ArgumentCaptor<InventoryLot> lotCaptor =
                ArgumentCaptor.forClass(InventoryLot.class);

        verify(inventoryLotRepository).create(lotCaptor.capture());

        InventoryLot lotCreado = lotCaptor.getValue();

        assertSame(arrozReferencia, lotCreado.getProduct());
        assertEquals(new BigDecimal("10.00"), lotCreado.getQuantity());
        assertEquals(new BigDecimal("10.00"), lotCreado.getRemainingQuantity());
        assertEquals(BigDecimal.ZERO, lotCreado.getUnitCost());
        assertNotNull(lotCreado.getEntryDate());

        assertSame(lotCreado, detail.getInventoryLot());

        verify(productRepository).updateStock(arrozPersistido);
        verify(donationRepository).changeStatus(donationId, StatusOrder.RECIBIDO);

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
        assertEquals(TransactionSource.DONACION, transaccion.getSource());
        assertEquals(99, transaccion.getUserId());
        assertNotNull(transaccion.getDateTime());
    }

    @Test
    @DisplayName("Escenario 2: Confirmar donación con varios productos")
    void confirmarDonacion_conVariosProductos_debeProcesarCadaDetalle() {
        // given
        Integer donationId = 1;

        Product arrozReferencia = crearProducto(1, "ARROZ", "KG", BigDecimal.ZERO);
        Product aceiteReferencia = crearProducto(2, "ACEITE", "L", BigDecimal.ZERO);

        Product arrozPersistido = crearProducto(1, "ARROZ", "KG", new BigDecimal("5.00"));
        Product aceitePersistido = crearProducto(2, "ACEITE", "L", new BigDecimal("2.00"));

        DonationDetail detalleArroz =
                crearDetalle(arrozReferencia, new BigDecimal("10.00"));

        DonationDetail detalleAceite =
                crearDetalle(aceiteReferencia, new BigDecimal("4.00"));

        Donation donation = crearDonacion(
                donationId,
                PeruTime.today(),
                StatusOrder.PENDIENTE,
                List.of(detalleArroz, detalleAceite)
        );

        Donation updatedDonation = crearDonacion(
                donationId,
                PeruTime.today(),
                StatusOrder.RECIBIDO,
                List.of(detalleArroz, detalleAceite)
        );

        User usuarioActual = new User();
        usuarioActual.setId(99);

        when(donationRepository.findById(donationId)).thenReturn(donation);
        when(productRepository.getProductoById(1)).thenReturn(arrozPersistido);
        when(productRepository.getProductoById(2)).thenReturn(aceitePersistido);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepository.create(any(InventoryLot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(donationRepository.changeStatus(donationId, StatusOrder.RECIBIDO))
                .thenReturn(updatedDonation);

        // when
        DonationResponseDTO response = service.confirm(donationId);

        // then
        assertEquals(StatusOrder.RECIBIDO.name(), response.getStatus());

        assertEquals(new BigDecimal("15.00"), arrozPersistido.getStock());
        assertEquals(new BigDecimal("6.00"), aceitePersistido.getStock());

        verify(inventoryLotRepository, times(2)).create(any(InventoryLot.class));
        verify(productRepository).updateStock(arrozPersistido);
        verify(productRepository).updateStock(aceitePersistido);
        verify(registerTransactionUseCase, times(2))
                .registrarTransaccion(any(TransactionRequestDTO.class));
        verify(donationRepository).changeStatus(donationId, StatusOrder.RECIBIDO);
    }

    @Test
    @DisplayName("Escenario 3: Rechazar donación futura")
    void confirmarDonacion_conFechaFutura_debeLanzarDateExceptionSinModificarStockNiEstado() {
        // given
        Integer donationId = 1;

        Donation donation = crearDonacion(
                donationId,
                PeruTime.today().plusDays(1),
                StatusOrder.PENDIENTE,
                List.of()
        );

        when(donationRepository.findById(donationId)).thenReturn(donation);

        // when
        DateException exception = assertThrows(
                DateException.class,
                () -> service.confirm(donationId)
        );

        // then
        assertEquals(
                "Error al confirmar orden : No se puede marcar como recibido una orden de donacion futura",
                exception.getMessage()
        );

        verify(donationRepository).findById(donationId);
        verify(donationRepository, never())
                .changeStatus(any(Integer.class), any(StatusOrder.class));

        verifyNoInteractions(productRepository);
        verifyNoInteractions(inventoryLotRepository);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(currentUserService);
    }

    @Test
    @DisplayName("Escenario 4: Rechazar donación ya confirmada")
    void confirmarDonacion_yaRecibida_debeLanzarRuntimeExceptionSinModificarStock() {
        // given
        Integer donationId = 1;

        Product arroz = crearProducto(1, "ARROZ", "KG", BigDecimal.ZERO);

        Donation donation = crearDonacion(
                donationId,
                PeruTime.today(),
                StatusOrder.RECIBIDO,
                List.of(crearDetalle(arroz, new BigDecimal("10.00")))
        );

        when(donationRepository.findById(donationId)).thenReturn(donation);

        // when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.confirm(donationId)
        );

        // then
        assertEquals("La orden ya fue confirmada", exception.getMessage());

        verify(donationRepository).findById(donationId);
        verify(donationRepository, never())
                .changeStatus(any(Integer.class), any(StatusOrder.class));

        verifyNoInteractions(productRepository);
        verifyNoInteractions(inventoryLotRepository);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(currentUserService);
    }

    @Test
    @DisplayName("Escenario 5: Crear lote sin costo unitario por donación")
    void confirmarDonacion_debeCrearLoteConCostoUnitarioCero() {
        // given
        Integer donationId = 1;

        Product fideoReferencia = crearProducto(3, "FIDEO", "KG", BigDecimal.ZERO);
        Product fideoPersistido = crearProducto(3, "FIDEO", "KG", new BigDecimal("7.00"));

        DonationDetail detail =
                crearDetalle(fideoReferencia, new BigDecimal("5.00"));

        Donation donation = crearDonacion(
                donationId,
                PeruTime.today(),
                StatusOrder.PENDIENTE,
                List.of(detail)
        );

        Donation updatedDonation = crearDonacion(
                donationId,
                PeruTime.today(),
                StatusOrder.RECIBIDO,
                List.of(detail)
        );

        User usuarioActual = new User();
        usuarioActual.setId(99);

        when(donationRepository.findById(donationId)).thenReturn(donation);
        when(productRepository.getProductoById(3)).thenReturn(fideoPersistido);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepository.create(any(InventoryLot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(donationRepository.changeStatus(donationId, StatusOrder.RECIBIDO))
                .thenReturn(updatedDonation);

        // when
        service.confirm(donationId);

        // then
        ArgumentCaptor<InventoryLot> lotCaptor =
                ArgumentCaptor.forClass(InventoryLot.class);

        verify(inventoryLotRepository).create(lotCaptor.capture());

        InventoryLot lotCreado = lotCaptor.getValue();

        assertEquals(BigDecimal.ZERO, lotCreado.getUnitCost());
        assertEquals(new BigDecimal("5.00"), lotCreado.getQuantity());
        assertEquals(new BigDecimal("5.00"), lotCreado.getRemainingQuantity());
    }

    private Donation crearDonacion(
            Integer id,
            LocalDate donationDate,
            StatusOrder status,
            List<DonationDetail> details
    ) {
        Donation donation = new Donation();
        donation.setId(id);
        donation.setDonationDate(donationDate);
        donation.setStatus(status);
        donation.setDetails(details);
        return donation;
    }

    private DonationDetail crearDetalle(
            Product product,
            BigDecimal quantity
    ) {
        DonationDetail detail = new DonationDetail();
        detail.setProduct(product);
        detail.setQuantity(quantity);
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