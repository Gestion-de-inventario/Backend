package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.*;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.domain.model.InventoryLot;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditMenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.MenuReportRequestDTO;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
//./mvnw.cmd test "-Dtest=*E2ETest" "-Dtest.password=12345678"
@ExtendWith(MockitoExtension.class)
@DisplayName("HU-3.3 Asignar encargado a la orden de producción")
class AssignUserToProductionOrderServiceTest {

    @Mock
    private MenuReportRepositoryPort menuReportRepositoryPort;

    @Mock
    private DishMenuRepositoryPort dishMenuRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private InventoryLotRepositoryPort inventoryLotRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private MenuReportMapper menuReportMapper;

    @Mock
    private RegisterTransactionUseCase registerTransactionUseCase;

    @Mock
    private CurrentUserService currentUserService;

    private CreateMenuReportService createMenuReportService;
    private EditMenuReportService editMenuReportService;

    @BeforeEach
    void setUp() {
        createMenuReportService = new CreateMenuReportService(
                menuReportRepositoryPort,
                dishMenuRepositoryPort,
                productRepositoryPort,
                inventoryLotRepositoryPort,
                menuReportMapper,
                registerTransactionUseCase,
                currentUserService,
                userRepositoryPort
        );

        editMenuReportService = new EditMenuReportService(
                menuReportRepositoryPort,
                dishMenuRepositoryPort,
                productRepositoryPort,
                inventoryLotRepositoryPort,
                userRepositoryPort,
                menuReportMapper,
                registerTransactionUseCase,
                currentUserService
        );
    }

    @Test
    @DisplayName("Escenario 1: Asignar una cocinera válida")
    void crearOrden_conUnaCocineraValida_debeGuardarIdDeCocineraEnLaOrdenCreada() {
        // given
        Integer cookId = 1;

        stubCocinerasExistentes(List.of(cookId));

        DishMenu menu = crearMenuConUnInsumo();
        InventoryLot lote = crearLoteDisponible(menu);

        MenuReportRequestDTO request = crearRequest(List.of(cookId));

        User usuarioActual = new User();
        usuarioActual.setId(99);

        MenuReportResponseDTO responseEsperado = new MenuReportResponseDTO();
        responseEsperado.setId(1);
        responseEsperado.setCooks(List.of(cookId));

        when(dishMenuRepositoryPort.findById(10)).thenReturn(menu);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepositoryPort.findAvailableByProduct(1)).thenReturn(List.of(lote));
        when(menuReportRepositoryPort.create(any(MenuReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(menuReportMapper.toDto(any(MenuReport.class))).thenReturn(responseEsperado);

        // when
        createMenuReportService.crearReporteMenu(request);

        // then
        ArgumentCaptor<MenuReport> captor =
                ArgumentCaptor.forClass(MenuReport.class);

        verify(menuReportRepositoryPort).create(captor.capture());

        MenuReport reporteCreado = captor.getValue();

        assertEquals(List.of(cookId), reporteCreado.getCooks());
    }

    @Test
    @DisplayName("Escenario 2: Asignar varias cocineras válidas")
    void crearOrden_conVariasCocinerasValidas_debeGuardarTodosLosIdsEnLaOrdenCreada() {
        // given
        List<Integer> cooks = List.of(1, 2, 3);

        stubCocinerasExistentes(cooks);

        DishMenu menu = crearMenuConUnInsumo();
        InventoryLot lote = crearLoteDisponible(menu);

        MenuReportRequestDTO request = crearRequest(cooks);

        User usuarioActual = new User();
        usuarioActual.setId(99);

        MenuReportResponseDTO responseEsperado = new MenuReportResponseDTO();
        responseEsperado.setId(1);
        responseEsperado.setCooks(cooks);

        when(dishMenuRepositoryPort.findById(10)).thenReturn(menu);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(inventoryLotRepositoryPort.findAvailableByProduct(1)).thenReturn(List.of(lote));
        when(menuReportRepositoryPort.create(any(MenuReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(menuReportMapper.toDto(any(MenuReport.class))).thenReturn(responseEsperado);


        // when
        createMenuReportService.crearReporteMenu(request);

        // then
        ArgumentCaptor<MenuReport> captor =
                ArgumentCaptor.forClass(MenuReport.class);

        verify(menuReportRepositoryPort).create(captor.capture());

        MenuReport reporteCreado = captor.getValue();

        assertEquals(cooks, reporteCreado.getCooks());
    }

    @Test
    @DisplayName("Escenario 3: Rechazar creación sin cocineras")
    void crearOrden_sinCocineras_debeLanzarIllegalArgumentExceptionSinModificarInventario() {
        // given
        MenuReportRequestDTO request = crearRequest(List.of());

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createMenuReportService.crearReporteMenu(request)
        );

        // then
        assertEquals(
                "Debe seleccionar al menos una cocinera",
                exception.getMessage()
        );

        verifyNoInteractions(userRepositoryPort);
        verifyNoInteractions(dishMenuRepositoryPort);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(productRepositoryPort);
        verifyNoInteractions(inventoryLotRepositoryPort);
        verifyNoInteractions(menuReportRepositoryPort);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(menuReportMapper);
    }

    @Test
    @DisplayName("Escenario 4: Rechazar creación con ID de cocinera inexistente")
    void crearOrden_conCocineraInexistente_debeLanzarUserNotFoundExceptionSinModificarInventario() {
        // given
        MenuReportRequestDTO request = crearRequest(List.of(1, 999));

        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(999))
                .thenReturn(Optional.empty());

        // when
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> createMenuReportService.crearReporteMenu(request)
        );

        // then
        assertEquals(
                "Usuario no encontrado: ID 999",
                exception.getMessage()
        );

        verify(userRepositoryPort).findById(1);
        verify(userRepositoryPort).findById(999);

        verifyNoInteractions(dishMenuRepositoryPort);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(productRepositoryPort);
        verifyNoInteractions(inventoryLotRepositoryPort);
        verifyNoInteractions(menuReportRepositoryPort);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(menuReportMapper);
    }

    @Test
    @DisplayName("Escenario 5: Rechazar edición con ID de cocinera inexistente")
    void editarOrden_conCocineraInexistente_debeLanzarUserNotFoundExceptionSinGuardarOrden() {
        // given
        EditMenuReportRequestDTO request = new EditMenuReportRequestDTO();
        request.setDishMenuId(10);
        request.setQuantityPrepared(100);
        request.setCooks(List.of(1, 999));

        when(userRepositoryPort.findById(1))
                .thenReturn(Optional.of(new User()));

        when(userRepositoryPort.findById(999))
                .thenReturn(Optional.empty());

        // when
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> editMenuReportService.editMenuReport(1, request)
        );

        // then
        assertEquals(
                "Usuario no encontrado: ID 999",
                exception.getMessage()
        );

        verify(userRepositoryPort).findById(1);
        verify(userRepositoryPort).findById(999);

        verifyNoInteractions(menuReportRepositoryPort);
        verifyNoInteractions(dishMenuRepositoryPort);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(productRepositoryPort);
        verifyNoInteractions(inventoryLotRepositoryPort);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(menuReportMapper);
    }

    private void stubCocinerasExistentes(List<Integer> cooks) {
        for (Integer cookId : cooks) {
            when(userRepositoryPort.findById(cookId))
                    .thenReturn(Optional.of(new User()));
        }
    }

    private MenuReportRequestDTO crearRequest(List<Integer> cooks) {
        MenuReportRequestDTO request = new MenuReportRequestDTO();
        request.setDishMenuId(10);
        request.setQuantityPrepared(10);
        request.setCooks(cooks);
        request.setCreateDate(PeruTime.today());
        return request;
    }

    private DishMenu crearMenuConUnInsumo() {
        Product arroz = new Product();
        arroz.setId(1);
        arroz.setName("Arroz");
        arroz.setUnit("kg");
        arroz.setStock(new BigDecimal("100"));

        DishSupply suministroArroz = new DishSupply();
        suministroArroz.setProduct(arroz);
        suministroArroz.setQuantityNeeded(BigDecimal.ONE);

        DishMenu menu = new DishMenu();
        menu.setId(10);
        menu.setName("Arroz con pollo");
        menu.setSupplies(List.of(suministroArroz));

        return menu;
    }

    private InventoryLot crearLoteDisponible(DishMenu menu) {
        Product producto = menu.getSupplies().get(0).getProduct();

        InventoryLot lote = new InventoryLot();
        lote.setId(100);
        lote.setProduct(producto);
        lote.setRemainingQuantity(new BigDecimal("100"));
        lote.setUnitCost(new BigDecimal("2.00"));

        return lote;
    }
}