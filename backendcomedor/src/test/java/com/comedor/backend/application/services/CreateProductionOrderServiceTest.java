package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;

import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.*;

import com.comedor.backend.domain.exceptions.DateException;
import com.comedor.backend.domain.exceptions.InsufficientStockException;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.domain.model.InventoryLot;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.domain.model.enums.StatusMenuReport;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.MenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MenuReportResponseDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-3.2 Crear orden de producción")
class CreateProductionOrderServiceTest {

    @Mock
    private MenuReportRepositoryPort repository;

    @Mock
    private DishMenuRepositoryPort dishMenuRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private InventoryLotRepositoryPort inventoryLotRepository;

    @Mock
    private MenuReportMapper mapper;

    @Mock
    private RegisterTransactionUseCase registerTransactionUseCase;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    private CreateMenuReportService service;

    @BeforeEach
    void setUp() {
        service = new CreateMenuReportService(
                repository,
                dishMenuRepository,
                productRepository,
                inventoryLotRepository,
                mapper,
                registerTransactionUseCase,
                currentUserService,
                userRepositoryPort
        );
    }

    @Test
    @DisplayName("Escenario 1: Crear orden con stock suficiente debe crear reporte, descontar stock y registrar transacciones")
    void crearOrden_conStockSuficiente_debeCrearReporteDescontarStockActualizarLotesYRegistrarTransacciones() {
        // given
        Integer dishMenuId = 10;
        LocalDate fechaCreacion = PeruTime.today();

        User usuarioActual = new User();
        usuarioActual.setId(99);

        Product arroz = new Product();
        arroz.setId(1);
        arroz.setName("Arroz");
        arroz.setUnit("kg");
        arroz.setStock(new BigDecimal("100"));

        DishSupply suministroArroz = new DishSupply();
        suministroArroz.setProduct(arroz);
        suministroArroz.setQuantityNeeded(BigDecimal.ONE);

        DishMenu menu = new DishMenu();
        menu.setId(dishMenuId);
        menu.setName("Arroz con pollo");
        menu.setSupplies(List.of(suministroArroz));

        InventoryLot loteArroz = new InventoryLot();
        loteArroz.setId(100);
        loteArroz.setProduct(arroz);
        loteArroz.setRemainingQuantity(new BigDecimal("100"));
        loteArroz.setUnitCost(new BigDecimal("2.00"));

        MenuReportRequestDTO request = new MenuReportRequestDTO();
        request.setDishMenuId(dishMenuId);
        request.setQuantityPrepared(30);
        request.setCooks(List.of(1, 2));
        request.setCreateDate(fechaCreacion);

        MenuReportResponseDTO responseEsperado = new MenuReportResponseDTO();
        responseEsperado.setId(1);
        responseEsperado.setDishId(dishMenuId);
        responseEsperado.setDishName("Arroz con pollo");
        responseEsperado.setQuantityPrepared(30);
        responseEsperado.setQuantityRemaining(30);

        responseEsperado.setCooks(List.of(1, 2));
        responseEsperado.setStatus(StatusMenuReport.ABIERTO);

        when(dishMenuRepository.findById(dishMenuId)).thenReturn(menu);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepository.findAvailableByProduct(1)).thenReturn(List.of(loteArroz));
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));
        when(repository.create(any(MenuReport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any(MenuReport.class))).thenReturn(responseEsperado);

        // when
        MenuReportResponseDTO response = service.crearReporteMenu(request);

        // then
        assertSame(responseEsperado, response);

        assertEquals(new BigDecimal("70"), arroz.getStock());
        assertEquals(new BigDecimal("70"), loteArroz.getRemainingQuantity());

        ArgumentCaptor<MenuReport> reporteCaptor =
                ArgumentCaptor.forClass(MenuReport.class);

        verify(repository).create(reporteCaptor.capture());

        MenuReport reporteCreado = reporteCaptor.getValue();

        assertEquals(fechaCreacion, reporteCreado.getDate());
        assertEquals(List.of(1, 2), reporteCreado.getCooks());
        assertSame(menu, reporteCreado.getDishMenu());
        assertEquals(30, reporteCreado.getQuantityPrepared());
        assertEquals(30, reporteCreado.getQuantityRemaining());
        assertEquals(StatusMenuReport.ABIERTO, reporteCreado.getStatus());
        assertEquals(new BigDecimal("60.00"), reporteCreado.getTotalSpent());
        assertEquals(1, reporteCreado.getStockMovements().size());

        verify(productRepository).updateStock(arroz);
        verify(inventoryLotRepository).update(loteArroz);
        verify(mapper).toDto(reporteCreado);

        ArgumentCaptor<TransactionRequestDTO> transactionCaptor =
                ArgumentCaptor.forClass(TransactionRequestDTO.class);

        verify(registerTransactionUseCase, times(2))
                .registrarTransaccion(transactionCaptor.capture());

        List<TransactionRequestDTO> transacciones = transactionCaptor.getAllValues();

        TransactionRequestDTO salidaIngrediente = transacciones.get(0);
        assertEquals(TransactionReferenceType.INGREDIENTE, salidaIngrediente.getReferenceType());
        assertEquals(1, salidaIngrediente.getReferenceId());
        assertEquals("ARROZ", salidaIngrediente.getItemName());
        assertEquals(MovementType.SALIDA, salidaIngrediente.getType());
        assertEquals(new BigDecimal("30"), salidaIngrediente.getAmount());
        assertEquals(new BigDecimal("100"), salidaIngrediente.getCurrentStock());
        assertEquals(99, salidaIngrediente.getUserId());
        assertEquals(fechaCreacion.atStartOfDay(), salidaIngrediente.getDateTime());

        TransactionRequestDTO entradaMenu = transacciones.get(1);
        assertEquals(TransactionReferenceType.MENU, entradaMenu.getReferenceType());
        assertEquals("Arroz con pollo", entradaMenu.getItemName());
        assertEquals(MovementType.ENTRADA, entradaMenu.getType());
        assertEquals(new BigDecimal("30"), entradaMenu.getAmount());
        assertEquals(BigDecimal.ZERO, entradaMenu.getCurrentStock());
        assertEquals(99, entradaMenu.getUserId());
        assertEquals(fechaCreacion.atStartOfDay(), entradaMenu.getDateTime());
    }

    @Test
    @DisplayName("Escenario 2: Crear orden con productos faltantes debe lanzar InsufficientStockException")
    void crearOrden_conProductosFaltantes_debeLanzarInsufficientStockExceptionYNoCrearReporte() {
        // given
        Integer dishMenuId = 10;

        User usuarioActual = new User();
        usuarioActual.setId(99);

        Product arroz = new Product();
        arroz.setId(1);
        arroz.setName("Arroz");
        arroz.setUnit("kg");
        arroz.setStock(new BigDecimal("10"));

        DishSupply suministroArroz = new DishSupply();
        suministroArroz.setProduct(arroz);
        suministroArroz.setQuantityNeeded(BigDecimal.ONE);

        DishMenu menu = new DishMenu();
        menu.setId(dishMenuId);
        menu.setName("Arroz con pollo");
        menu.setSupplies(List.of(suministroArroz));

        MenuReportRequestDTO request = new MenuReportRequestDTO();
        request.setDishMenuId(dishMenuId);
        request.setQuantityPrepared(30);
        request.setCooks(List.of(1, 2));
        request.setCreateDate(PeruTime.today());

        when(dishMenuRepository.findById(dishMenuId)).thenReturn(menu);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));

        // when
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> service.crearReporteMenu(request)
        );

        // then
        assertEquals("Stock insuficiente", exception.getMessage());
        assertEquals(1, exception.getFaltantes().size());
        assertEquals(1, exception.getFaltantes().get(0).getProductId());
        assertEquals("ARROZ", exception.getFaltantes().get(0).getProductName());
        assertEquals("KG", exception.getFaltantes().get(0).getProductUnit());
        assertEquals(new BigDecimal("20"), exception.getFaltantes().get(0).getQuantityNeeded());

        assertEquals(new BigDecimal("10"), arroz.getStock());

        verify(repository, never()).create(any(MenuReport.class));
        verify(productRepository, never()).updateStock(any(Product.class));
        verify(inventoryLotRepository, never()).findAvailableByProduct(any());
        verify(inventoryLotRepository, never()).update(any(InventoryLot.class));
        verify(registerTransactionUseCase, never()).registrarTransaccion(any());
        verify(mapper, never()).toDto(any(MenuReport.class));
    }

    @Test
    @DisplayName("Escenario 3: Crear orden con lotes insuficientes debe lanzar inconsistencia de inventario")
    void crearOrden_conStockSuficientePeroLotesInsuficientes_debeLanzarRuntimeExceptionYNoCrearReporte() {
        // given
        Integer dishMenuId = 10;

        User usuarioActual = new User();
        usuarioActual.setId(99);

        Product arroz = new Product();
        arroz.setId(1);
        arroz.setName("Arroz");
        arroz.setUnit("kg");
        arroz.setStock(new BigDecimal("100"));

        DishSupply suministroArroz = new DishSupply();
        suministroArroz.setProduct(arroz);
        suministroArroz.setQuantityNeeded(BigDecimal.ONE);

        DishMenu menu = new DishMenu();
        menu.setId(dishMenuId);
        menu.setName("Arroz con pollo");
        menu.setSupplies(List.of(suministroArroz));

        InventoryLot loteInsuficiente = new InventoryLot();
        loteInsuficiente.setId(100);
        loteInsuficiente.setProduct(arroz);
        loteInsuficiente.setRemainingQuantity(new BigDecimal("10"));
        loteInsuficiente.setUnitCost(new BigDecimal("2.00"));

        MenuReportRequestDTO request = new MenuReportRequestDTO();
        request.setDishMenuId(dishMenuId);
        request.setQuantityPrepared(30);
        request.setCooks(List.of(1, 2));
        request.setCreateDate(PeruTime.today());

        when(dishMenuRepository.findById(dishMenuId)).thenReturn(menu);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));
        when(inventoryLotRepository.findAvailableByProduct(1)).thenReturn(List.of(loteInsuficiente));

        // when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.crearReporteMenu(request)
        );

        // then
        assertEquals(
                "Inconsistencia de inventario para ARROZ",
                exception.getMessage()
        );

        verify(repository, never()).create(any(MenuReport.class));
        verify(mapper, never()).toDto(any(MenuReport.class));

        verify(productRepository).updateStock(arroz);
        verify(inventoryLotRepository).update(loteInsuficiente);

        verify(registerTransactionUseCase, times(1))
                .registrarTransaccion(any(TransactionRequestDTO.class));
    }

    @Test
    @DisplayName("Escenario 4: Crear orden con fecha anterior debe lanzar DateException sin modificar inventario")
    void crearOrden_conFechaAnterior_debeLanzarDateExceptionSinModificarInventario() {
        // given
        MenuReportRequestDTO request = new MenuReportRequestDTO();
        request.setDishMenuId(10);
        request.setQuantityPrepared(30);
        request.setCooks(List.of(1, 2));
        request.setCreateDate(PeruTime.today().minusDays(1));

        // when
        DateException exception = assertThrows(
                DateException.class,
                () -> service.crearReporteMenu(request)
        );

        // then
        assertEquals(
                "Error al crear orden : La fecha de creación no puede ser menor a la actual ",
                exception.getMessage()
        );

        verifyNoInteractions(dishMenuRepository);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(productRepository);
        verifyNoInteractions(inventoryLotRepository);
        verifyNoInteractions(repository);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Escenario 5: Crear orden con varios insumos debe descontar stock, actualizar lotes y calcular costo total")
    void crearOrden_conVariosInsumos_debeDescontarCadaProductoActualizarLotesYCalcularTotalSpent() {
        // given
        Integer dishMenuId = 10;
        LocalDate fechaCreacion = PeruTime.today();

        User usuarioActual = new User();
        usuarioActual.setId(99);

        Product arroz = new Product();
        arroz.setId(1);
        arroz.setName("Arroz");
        arroz.setUnit("kg");
        arroz.setStock(new BigDecimal("100"));

        Product pollo = new Product();
        pollo.setId(2);
        pollo.setName("Pollo");
        pollo.setUnit("kg");
        pollo.setStock(new BigDecimal("50"));

        DishSupply suministroArroz = new DishSupply();
        suministroArroz.setProduct(arroz);
        suministroArroz.setQuantityNeeded(BigDecimal.ONE);

        DishSupply suministroPollo = new DishSupply();
        suministroPollo.setProduct(pollo);
        suministroPollo.setQuantityNeeded(new BigDecimal("2"));

        DishMenu menu = new DishMenu();
        menu.setId(dishMenuId);
        menu.setName("Arroz con pollo");
        menu.setSupplies(List.of(suministroArroz, suministroPollo));

        InventoryLot loteArroz = new InventoryLot();
        loteArroz.setId(100);
        loteArroz.setProduct(arroz);
        loteArroz.setRemainingQuantity(new BigDecimal("100"));
        loteArroz.setUnitCost(new BigDecimal("2.00"));

        InventoryLot lotePollo = new InventoryLot();
        lotePollo.setId(200);
        lotePollo.setProduct(pollo);
        lotePollo.setRemainingQuantity(new BigDecimal("50"));
        lotePollo.setUnitCost(new BigDecimal("5.00"));

        MenuReportRequestDTO request = new MenuReportRequestDTO();
        request.setDishMenuId(dishMenuId);
        request.setQuantityPrepared(10);
        request.setCooks(List.of(1, 2));
        request.setCreateDate(fechaCreacion);

        MenuReportResponseDTO responseEsperado = new MenuReportResponseDTO();
        responseEsperado.setId(1);
        responseEsperado.setDishId(dishMenuId);
        responseEsperado.setDishName("Arroz con pollo");
        responseEsperado.setQuantityPrepared(10);
        responseEsperado.setQuantityRemaining(10);
        responseEsperado.setCooks(List.of(1, 2));
        responseEsperado.setStatus(StatusMenuReport.ABIERTO);

        when(dishMenuRepository.findById(dishMenuId)).thenReturn(menu);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));
        when(inventoryLotRepository.findAvailableByProduct(1)).thenReturn(List.of(loteArroz));
        when(inventoryLotRepository.findAvailableByProduct(2)).thenReturn(List.of(lotePollo));
        when(repository.create(any(MenuReport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any(MenuReport.class))).thenReturn(responseEsperado);

        // when
        MenuReportResponseDTO response = service.crearReporteMenu(request);

        // then
        assertSame(responseEsperado, response);

        assertEquals(new BigDecimal("90"), arroz.getStock());
        assertEquals(new BigDecimal("30"), pollo.getStock());

        assertEquals(new BigDecimal("90"), loteArroz.getRemainingQuantity());
        assertEquals(new BigDecimal("30"), lotePollo.getRemainingQuantity());

        ArgumentCaptor<MenuReport> reporteCaptor =
                ArgumentCaptor.forClass(MenuReport.class);

        verify(repository).create(reporteCaptor.capture());

        MenuReport reporteCreado = reporteCaptor.getValue();

        assertEquals(new BigDecimal("120.00"), reporteCreado.getTotalSpent());
        assertEquals(2, reporteCreado.getStockMovements().size());

        verify(productRepository).updateStock(arroz);
        verify(productRepository).updateStock(pollo);

        verify(inventoryLotRepository).update(loteArroz);
        verify(inventoryLotRepository).update(lotePollo);

        ArgumentCaptor<TransactionRequestDTO> transactionCaptor =
                ArgumentCaptor.forClass(TransactionRequestDTO.class);

        verify(registerTransactionUseCase, times(3))
                .registrarTransaccion(transactionCaptor.capture());

        List<TransactionRequestDTO> transacciones = transactionCaptor.getAllValues();

        TransactionRequestDTO salidaArroz = transacciones.get(0);
        assertEquals(TransactionReferenceType.INGREDIENTE, salidaArroz.getReferenceType());
        assertEquals(1, salidaArroz.getReferenceId());
        assertEquals("ARROZ", salidaArroz.getItemName());
        assertEquals(MovementType.SALIDA, salidaArroz.getType());
        assertEquals(new BigDecimal("10"), salidaArroz.getAmount());
        assertEquals(new BigDecimal("100"), salidaArroz.getCurrentStock());

        TransactionRequestDTO salidaPollo = transacciones.get(1);
        assertEquals(TransactionReferenceType.INGREDIENTE, salidaPollo.getReferenceType());
        assertEquals(2, salidaPollo.getReferenceId());
        assertEquals("POLLO", salidaPollo.getItemName());
        assertEquals(MovementType.SALIDA, salidaPollo.getType());
        assertEquals(new BigDecimal("20"), salidaPollo.getAmount());
        assertEquals(new BigDecimal("50"), salidaPollo.getCurrentStock());

        TransactionRequestDTO entradaMenu = transacciones.get(2);
        assertEquals(TransactionReferenceType.MENU, entradaMenu.getReferenceType());
        assertEquals(MovementType.ENTRADA, entradaMenu.getType());
        assertEquals("Arroz con pollo", entradaMenu.getItemName());
        assertEquals(new BigDecimal("10"), entradaMenu.getAmount());
    }

    @Test
    @DisplayName("Escenario 6: Crear orden debe registrar entrada del menú producido")
    void crearOrden_exitosa_debeRegistrarEntradaDelMenuProducido() {
        // given
        Integer dishMenuId = 10;
        LocalDate fechaCreacion = PeruTime.today();

        User usuarioActual = new User();
        usuarioActual.setId(99);

        Product arroz = new Product();
        arroz.setId(1);
        arroz.setName("Arroz");
        arroz.setUnit("kg");
        arroz.setStock(new BigDecimal("100"));

        DishSupply suministroArroz = new DishSupply();
        suministroArroz.setProduct(arroz);
        suministroArroz.setQuantityNeeded(BigDecimal.ONE);

        DishMenu menu = new DishMenu();
        menu.setId(dishMenuId);
        menu.setName("Arroz con pollo");
        menu.setSupplies(List.of(suministroArroz));

        InventoryLot loteArroz = new InventoryLot();
        loteArroz.setId(100);
        loteArroz.setProduct(arroz);
        loteArroz.setRemainingQuantity(new BigDecimal("100"));
        loteArroz.setUnitCost(new BigDecimal("2.00"));

        MenuReportRequestDTO request = new MenuReportRequestDTO();
        request.setDishMenuId(dishMenuId);
        request.setQuantityPrepared(25);
        request.setCooks(List.of(1, 2));
        request.setCreateDate(fechaCreacion);

        MenuReportResponseDTO responseEsperado = new MenuReportResponseDTO();
        responseEsperado.setId(1);
        responseEsperado.setDishId(dishMenuId);
        responseEsperado.setDishName("Arroz con pollo");
        responseEsperado.setQuantityPrepared(25);
        responseEsperado.setQuantityRemaining(25);
        responseEsperado.setCooks(List.of(1, 2));
        responseEsperado.setStatus(StatusMenuReport.ABIERTO);

        when(dishMenuRepository.findById(dishMenuId)).thenReturn(menu);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepository.findAvailableByProduct(1)).thenReturn(List.of(loteArroz));
        when(repository.create(any(MenuReport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any(MenuReport.class))).thenReturn(responseEsperado);
        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(2))
                .thenReturn(Optional.of(new User()));

        // when
        MenuReportResponseDTO response = service.crearReporteMenu(request);

        // then
        assertSame(responseEsperado, response);

        ArgumentCaptor<TransactionRequestDTO> transactionCaptor =
                ArgumentCaptor.forClass(TransactionRequestDTO.class);

        verify(registerTransactionUseCase, times(2))
                .registrarTransaccion(transactionCaptor.capture());

        List<TransactionRequestDTO> transacciones = transactionCaptor.getAllValues();

        TransactionRequestDTO entradaMenu = transacciones.get(1);

        assertEquals(TransactionReferenceType.MENU, entradaMenu.getReferenceType());
        assertEquals(null, entradaMenu.getReferenceId());
        assertEquals("Arroz con pollo", entradaMenu.getItemName());
        assertEquals(MovementType.ENTRADA, entradaMenu.getType());
        assertEquals(new BigDecimal("25"), entradaMenu.getAmount());
        assertEquals(BigDecimal.ZERO, entradaMenu.getCurrentStock());
        assertEquals(99, entradaMenu.getUserId());
        assertEquals(fechaCreacion.atStartOfDay(), entradaMenu.getDateTime());

        verify(repository).create(any(MenuReport.class));
    }
}