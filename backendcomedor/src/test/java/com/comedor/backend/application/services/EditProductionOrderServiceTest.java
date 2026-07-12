package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.out.*;
import com.comedor.backend.domain.exceptions.InsufficientStockException;
import com.comedor.backend.domain.model.*;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditMenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MenuReportResponseDTO;

import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditProductionOrderServiceTest {

    @Mock
    private MenuReportRepositoryPort menuReportRepositoryPort;

    @Mock
    private DishMenuRepositoryPort dishMenuRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private InventoryLotRepositoryPort inventoryLotRepository;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private MenuReportMapper mapper;

    @Mock
    private RegisterTransactionUseCase registerTransactionUseCase;

    @Mock
    private CurrentUserService currentUserService;

    private EditMenuReportService service;

    @BeforeEach
    void setUp() {
        service = new EditMenuReportService(
                menuReportRepositoryPort,
                dishMenuRepositoryPort,
                productRepository,
                inventoryLotRepository,
                userRepositoryPort,
                mapper,
                registerTransactionUseCase,
                currentUserService
        );
    }

    @Test
    void editarOrden_sinCambios_debeRetornarReporteSinGuardarNiModificarInventario() {
        // given
        Integer reportId = 1;

        DishMenu menuActual = new DishMenu();
        menuActual.setId(10);
        menuActual.setName("Arroz con pollo");

        MenuReport reporteActual = new MenuReport();
        reporteActual.setId(reportId);
        reporteActual.setDate(LocalDate.now());
        reporteActual.setDishMenu(menuActual);
        reporteActual.setQuantityPrepared(100);
        reporteActual.setQuantityRemaining(100);
        reporteActual.setCooks(List.of(1, 2));
        reporteActual.setBeneficiaryControls(new ArrayList<>());

        EditMenuReportRequestDTO request = new EditMenuReportRequestDTO();
        request.setDishMenuId(10);
        request.setQuantityPrepared(100);
        request.setCooks(List.of(1, 2));

        MenuReportResponseDTO responseEsperado = new MenuReportResponseDTO();
        responseEsperado.setId(reportId);
        responseEsperado.setDishId(10);
        responseEsperado.setDishName("Arroz con pollo");
        responseEsperado.setQuantityPrepared(100);
        responseEsperado.setQuantityRemaining(100);
        responseEsperado.setCooks(List.of(1, 2));

        when(menuReportRepositoryPort.findById(reportId)).thenReturn(reporteActual);
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));
        when(mapper.toDto(reporteActual)).thenReturn(responseEsperado);

        // when
        MenuReportResponseDTO response = service.editMenuReport(reportId, request);

        // then
        assertSame(responseEsperado, response);

        verify(menuReportRepositoryPort).findById(reportId);
        verify(mapper).toDto(reporteActual);

        verify(menuReportRepositoryPort, never()).save(any(MenuReport.class));
        verify(dishMenuRepositoryPort, never()).findById(any());
        verify(productRepository, never()).updateStock(any());
        verify(inventoryLotRepository, never()).update(any());
        verify(registerTransactionUseCase, never()).registrarTransaccion(any());
        verifyNoInteractions(currentUserService);
    }

    @Test
    void editarOrden_soloCocineras_debeGuardarReporteSinModificarInventario() {
        // given
        Integer reportId = 1;

        DishMenu menuActual = new DishMenu();
        menuActual.setId(10);
        menuActual.setName("Arroz con pollo");

        MenuReport reporteActual = new MenuReport();
        reporteActual.setId(reportId);
        reporteActual.setDate(LocalDate.now());
        reporteActual.setDishMenu(menuActual);
        reporteActual.setQuantityPrepared(100);
        reporteActual.setQuantityRemaining(100);
        reporteActual.setCooks(List.of(1, 2));
        reporteActual.setBeneficiaryControls(new ArrayList<>());

        EditMenuReportRequestDTO request = new EditMenuReportRequestDTO();
        request.setDishMenuId(10);
        request.setQuantityPrepared(100);
        request.setCooks(List.of(3, 4));

        MenuReportResponseDTO responseEsperado = new MenuReportResponseDTO();
        responseEsperado.setId(reportId);
        responseEsperado.setDishId(10);
        responseEsperado.setDishName("Arroz con pollo");
        responseEsperado.setQuantityPrepared(100);
        responseEsperado.setQuantityRemaining(100);
        responseEsperado.setCooks(List.of(3, 4));

        when(menuReportRepositoryPort.findById(reportId)).thenReturn(reporteActual);
        when(menuReportRepositoryPort.save(reporteActual)).thenReturn(reporteActual);
        when(mapper.toDto(reporteActual)).thenReturn(responseEsperado);
        when(userRepositoryPort.findById(3))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(4))
                .thenReturn(Optional.of(new User()));

        // when
        MenuReportResponseDTO response = service.editMenuReport(reportId, request);

        // then
        assertSame(responseEsperado, response);
        assertEquals(List.of(3, 4), reporteActual.getCooks());

        verify(menuReportRepositoryPort).findById(reportId);
        verify(menuReportRepositoryPort).save(reporteActual);
        verify(mapper).toDto(reporteActual);

        verify(dishMenuRepositoryPort, never()).findById(any());
        verify(productRepository, never()).updateStock(any());
        verify(inventoryLotRepository, never()).update(any());
        verify(registerTransactionUseCase, never()).registrarTransaccion(any());
        verifyNoInteractions(currentUserService);
    }

    @Test
    void editarOrden_conBeneficiariosAsociadosYCambioCantidad_debeLanzarIllegalStateException() {
        // given
        Integer reportId = 1;

        DishMenu menuActual = new DishMenu();
        menuActual.setId(10);
        menuActual.setName("Arroz con pollo");

        MenuReport reporteActual = new MenuReport();
        reporteActual.setId(reportId);
        reporteActual.setDate(LocalDate.now());
        reporteActual.setDishMenu(menuActual);
        reporteActual.setQuantityPrepared(100);
        reporteActual.setQuantityRemaining(100);
        reporteActual.setCooks(List.of(1, 2));
        reporteActual.setBeneficiaryControls(List.of(new BeneficiaryControl()));

        EditMenuReportRequestDTO request = new EditMenuReportRequestDTO();
        request.setDishMenuId(10);
        request.setQuantityPrepared(120);
        request.setCooks(List.of(1, 2));

        when(menuReportRepositoryPort.findById(reportId)).thenReturn(reporteActual);
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));

        // when
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.editMenuReport(reportId, request)
        );

        // then
        assertEquals(
                "No se puede editar menú con beneficiarios asociados",
                exception.getMessage()
        );

        verify(menuReportRepositoryPort).findById(reportId);

        verify(menuReportRepositoryPort, never()).save(any(MenuReport.class));
        verify(dishMenuRepositoryPort, never()).findById(any());
        verify(productRepository, never()).updateStock(any());
        verify(inventoryLotRepository, never()).update(any());
        verify(registerTransactionUseCase, never()).registrarTransaccion(any());
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(mapper);
    }

    @Test
    void editarOrden_conStockInsuficiente_debeLanzarInsufficientStockExceptionSinGuardarCambios() {
        // given
        Integer reportId = 1;

        Product arroz = new Product();
        arroz.setId(1);
        arroz.setName("Arroz");
        arroz.setUnit("kg");
        arroz.setStock(BigDecimal.ZERO);

        DishSupply suministroArroz = new DishSupply();
        suministroArroz.setProduct(arroz);
        suministroArroz.setQuantityNeeded(BigDecimal.ONE);

        DishMenu menuActual = new DishMenu();
        menuActual.setId(10);
        menuActual.setName("Arroz con pollo");
        menuActual.setSupplies(List.of(suministroArroz));

        MenuReport reporteActual = new MenuReport();
        reporteActual.setId(reportId);
        reporteActual.setDate(LocalDate.now());
        reporteActual.setDishMenu(menuActual);
        reporteActual.setQuantityPrepared(100);
        reporteActual.setQuantityRemaining(100);
        reporteActual.setCooks(List.of(1, 2));
        reporteActual.setBeneficiaryControls(new ArrayList<>());
        reporteActual.setStockMovements(new ArrayList<>());

        EditMenuReportRequestDTO request = new EditMenuReportRequestDTO();
        request.setDishMenuId(10);
        request.setQuantityPrepared(200);
        request.setCooks(List.of(1, 2));

        when(menuReportRepositoryPort.findById(reportId)).thenReturn(reporteActual);
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));

        // when
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> service.editMenuReport(reportId, request)
        );

        // then
        assertEquals("Stock insuficiente", exception.getMessage());
        assertNotNull(exception.getFaltantes());
        assertEquals(1, exception.getFaltantes().size());
        assertEquals(1, exception.getFaltantes().get(0).getProductId());
        assertEquals("ARROZ", exception.getFaltantes().get(0).getProductName());
        assertEquals(new BigDecimal("100"), exception.getFaltantes().get(0).getQuantityNeeded());

        verify(menuReportRepositoryPort).findById(reportId);

        verify(menuReportRepositoryPort, never()).save(any(MenuReport.class));
        verify(dishMenuRepositoryPort, never()).findById(any());
        verify(productRepository, never()).updateStock(any());
        verify(inventoryLotRepository, never()).update(any());
        verify(registerTransactionUseCase, never()).registrarTransaccion(any());
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(mapper);
    }

    @Test
    void editarOrden_cambiandoCantidadConStockSuficiente_debeActualizarInventarioYRegistrarModificacion() {
        // given
        Integer reportId = 1;

        User usuarioActual = new User();
        usuarioActual.setId(99);

        Product arroz = new Product();
        arroz.setId(1);
        arroz.setName("Arroz");
        arroz.setUnit("kg");
        arroz.setStock(new BigDecimal("50"));

        DishSupply suministroArroz = new DishSupply();
        suministroArroz.setProduct(arroz);
        suministroArroz.setQuantityNeeded(BigDecimal.ONE);

        DishMenu menuActual = new DishMenu();
        menuActual.setId(10);
        menuActual.setName("Arroz con pollo");
        menuActual.setSupplies(List.of(suministroArroz));

        InventoryLot loteAnterior = new InventoryLot();
        loteAnterior.setId(100);
        loteAnterior.setProduct(arroz);
        loteAnterior.setRemainingQuantity(new BigDecimal("100"));
        loteAnterior.setUnitCost(new BigDecimal("2.00"));

        StockMovement movimientoAnterior = new StockMovement();
        movimientoAnterior.setInventoryLot(loteAnterior);
        movimientoAnterior.setQuantityUsed(new BigDecimal("100"));
        movimientoAnterior.setUnitCost(new BigDecimal("2.00"));
        movimientoAnterior.setTotalCost(new BigDecimal("200.00"));

        MenuReport reporteActual = new MenuReport();
        reporteActual.setId(reportId);
        reporteActual.setDate(LocalDate.now());
        reporteActual.setDishMenu(menuActual);
        reporteActual.setQuantityPrepared(100);
        reporteActual.setQuantityRemaining(100);
        reporteActual.setCooks(List.of(1, 2));
        reporteActual.setBeneficiaryControls(new ArrayList<>());
        reporteActual.setStockMovements(new ArrayList<>(List.of(movimientoAnterior)));

        EditMenuReportRequestDTO request = new EditMenuReportRequestDTO();
        request.setDishMenuId(10);
        request.setQuantityPrepared(120);
        request.setCooks(List.of(1, 2));

        MenuReportResponseDTO responseEsperado = new MenuReportResponseDTO();
        responseEsperado.setId(reportId);
        responseEsperado.setDishId(10);
        responseEsperado.setDishName("Arroz con pollo");
        responseEsperado.setQuantityPrepared(120);
        responseEsperado.setQuantityRemaining(120);
        responseEsperado.setCooks(List.of(1, 2));

        when(menuReportRepositoryPort.findById(reportId)).thenReturn(reporteActual);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepository.findAvailableByProduct(1)).thenReturn(List.of(loteAnterior));
        when(menuReportRepositoryPort.save(reporteActual)).thenReturn(reporteActual);
        when(mapper.toDto(reporteActual)).thenReturn(responseEsperado);
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));

        // when
        MenuReportResponseDTO response = service.editMenuReport(reportId, request);

        // then
        assertSame(responseEsperado, response);

        assertEquals(120, reporteActual.getQuantityPrepared());
        assertEquals(120, reporteActual.getQuantityRemaining());
        assertEquals(menuActual, reporteActual.getDishMenu());
        assertEquals(new BigDecimal("30"), arroz.getStock());
        assertEquals(new BigDecimal("80"), loteAnterior.getRemainingQuantity());
        assertEquals(new BigDecimal("240.00"), reporteActual.getTotalSpent());

        verify(menuReportRepositoryPort).findById(reportId);
        verify(currentUserService).getCurrentUser();
        verify(inventoryLotRepository, times(2)).update(loteAnterior);
        verify(productRepository, times(2)).updateStock(arroz);
        verify(menuReportRepositoryPort).save(reporteActual);
        verify(mapper).toDto(reporteActual);

        ArgumentCaptor<TransactionRequestDTO> captor =
                ArgumentCaptor.forClass(TransactionRequestDTO.class);

        verify(registerTransactionUseCase, times(3))
                .registrarTransaccion(captor.capture());

        List<TransactionRequestDTO> transacciones = captor.getAllValues();

        assertEquals(MovementType.ENTRADA, transacciones.get(0).getType());
        assertEquals(TransactionReferenceType.INGREDIENTE, transacciones.get(0).getReferenceType());
        assertEquals(new BigDecimal("100"), transacciones.get(0).getAmount());
        assertEquals(new BigDecimal("50"), transacciones.get(0).getCurrentStock());

        assertEquals(MovementType.SALIDA, transacciones.get(1).getType());
        assertEquals(TransactionReferenceType.INGREDIENTE, transacciones.get(1).getReferenceType());
        assertEquals(new BigDecimal("120"), transacciones.get(1).getAmount());
        assertEquals(new BigDecimal("150"), transacciones.get(1).getCurrentStock());

        assertEquals(MovementType.MODIFICACION, transacciones.get(2).getType());
        assertEquals(TransactionReferenceType.MENU, transacciones.get(2).getReferenceType());
        assertEquals("Arroz con pollo", transacciones.get(2).getItemName());
        assertEquals(new BigDecimal("120"), transacciones.get(2).getAmount());
        assertEquals(new BigDecimal("100"), transacciones.get(2).getCurrentStock());
    }

    @Test
    void editarOrden_cambiandoMenuConStockSuficiente_debeActualizarMenuInventarioYRegistrarModificacion() {
        // given
        Integer reportId = 1;

        User usuarioActual = new User();
        usuarioActual.setId(99);

        Product arroz = new Product();
        arroz.setId(1);
        arroz.setName("Arroz");
        arroz.setUnit("kg");
        arroz.setStock(new BigDecimal("50"));

        DishSupply suministroArroz = new DishSupply();
        suministroArroz.setProduct(arroz);
        suministroArroz.setQuantityNeeded(BigDecimal.ONE);

        DishMenu menuActual = new DishMenu();
        menuActual.setId(10);
        menuActual.setName("Arroz con pollo");
        menuActual.setSupplies(List.of(suministroArroz));

        Product fideo = new Product();
        fideo.setId(2);
        fideo.setName("Fideo");
        fideo.setUnit("kg");
        fideo.setStock(new BigDecimal("200"));

        DishSupply suministroFideo = new DishSupply();
        suministroFideo.setProduct(fideo);
        suministroFideo.setQuantityNeeded(new BigDecimal("2"));

        DishMenu nuevoMenu = new DishMenu();
        nuevoMenu.setId(20);
        nuevoMenu.setName("Tallarines rojos");
        nuevoMenu.setSupplies(List.of(suministroFideo));

        InventoryLot loteAnterior = new InventoryLot();
        loteAnterior.setId(100);
        loteAnterior.setProduct(arroz);
        loteAnterior.setRemainingQuantity(new BigDecimal("100"));
        loteAnterior.setUnitCost(new BigDecimal("2.00"));

        StockMovement movimientoAnterior = new StockMovement();
        movimientoAnterior.setInventoryLot(loteAnterior);
        movimientoAnterior.setQuantityUsed(new BigDecimal("100"));
        movimientoAnterior.setUnitCost(new BigDecimal("2.00"));
        movimientoAnterior.setTotalCost(new BigDecimal("200.00"));

        InventoryLot loteNuevo = new InventoryLot();
        loteNuevo.setId(200);
        loteNuevo.setProduct(fideo);
        loteNuevo.setRemainingQuantity(new BigDecimal("200"));
        loteNuevo.setUnitCost(new BigDecimal("3.00"));

        MenuReport reporteActual = new MenuReport();
        reporteActual.setId(reportId);
        reporteActual.setDate(LocalDate.now());
        reporteActual.setDishMenu(menuActual);
        reporteActual.setQuantityPrepared(100);
        reporteActual.setQuantityRemaining(100);
        reporteActual.setCooks(List.of(1, 2));
        reporteActual.setBeneficiaryControls(new ArrayList<>());
        reporteActual.setStockMovements(new ArrayList<>(List.of(movimientoAnterior)));

        EditMenuReportRequestDTO request = new EditMenuReportRequestDTO();
        request.setDishMenuId(20);
        request.setQuantityPrepared(100);
        request.setCooks(List.of(1, 2));

        MenuReportResponseDTO responseEsperado = new MenuReportResponseDTO();
        responseEsperado.setId(reportId);
        responseEsperado.setDishId(20);
        responseEsperado.setDishName("Tallarines rojos");
        responseEsperado.setQuantityPrepared(100);
        responseEsperado.setQuantityRemaining(100);
        responseEsperado.setCooks(List.of(1, 2));

        when(menuReportRepositoryPort.findById(reportId)).thenReturn(reporteActual);
        when(dishMenuRepositoryPort.findById(20)).thenReturn(nuevoMenu);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepository.findAvailableByProduct(2)).thenReturn(List.of(loteNuevo));
        when(menuReportRepositoryPort.save(reporteActual)).thenReturn(reporteActual);
        when(mapper.toDto(reporteActual)).thenReturn(responseEsperado);
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));

        // when
        MenuReportResponseDTO response = service.editMenuReport(reportId, request);

        // then
        assertSame(responseEsperado, response);

        assertEquals(nuevoMenu, reporteActual.getDishMenu());
        assertEquals(100, reporteActual.getQuantityPrepared());
        assertEquals(100, reporteActual.getQuantityRemaining());

        assertEquals(new BigDecimal("150"), arroz.getStock());
        assertEquals(BigDecimal.ZERO, fideo.getStock());
        assertEquals(BigDecimal.ZERO, loteNuevo.getRemainingQuantity());
        assertEquals(new BigDecimal("600.00"), reporteActual.getTotalSpent());

        verify(menuReportRepositoryPort).findById(reportId);
        verify(dishMenuRepositoryPort).findById(20);
        verify(currentUserService).getCurrentUser();

        verify(inventoryLotRepository).update(loteAnterior);
        verify(inventoryLotRepository).update(loteNuevo);

        verify(productRepository).updateStock(arroz);
        verify(productRepository).updateStock(fideo);

        verify(menuReportRepositoryPort).save(reporteActual);
        verify(mapper).toDto(reporteActual);

        ArgumentCaptor<TransactionRequestDTO> captor =
                ArgumentCaptor.forClass(TransactionRequestDTO.class);

        verify(registerTransactionUseCase, times(3))
                .registrarTransaccion(captor.capture());

        List<TransactionRequestDTO> transacciones = captor.getAllValues();

        assertEquals(MovementType.ENTRADA, transacciones.get(0).getType());
        assertEquals(TransactionReferenceType.INGREDIENTE, transacciones.get(0).getReferenceType());
        assertEquals("ARROZ", transacciones.get(0).getItemName());

        assertEquals(MovementType.SALIDA, transacciones.get(1).getType());
        assertEquals(TransactionReferenceType.INGREDIENTE, transacciones.get(1).getReferenceType());
        assertEquals("FIDEO", transacciones.get(1).getItemName());

        assertEquals(MovementType.MODIFICACION, transacciones.get(2).getType());
        assertEquals(TransactionReferenceType.MENU, transacciones.get(2).getReferenceType());
        assertEquals("Tallarines rojos", transacciones.get(2).getItemName());
        assertEquals(new BigDecimal("100"), transacciones.get(2).getAmount());
        assertEquals(new BigDecimal("100"), transacciones.get(2).getCurrentStock());
    }
}
