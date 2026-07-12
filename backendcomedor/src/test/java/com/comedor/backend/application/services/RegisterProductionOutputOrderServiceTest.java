package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryControlMapper;
import com.comedor.backend.application.ports.in.RecalculateSummaryReportUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryNotFoundException;
import com.comedor.backend.domain.exceptions.MenuPriceInvalidException;
import com.comedor.backend.domain.exceptions.QuantityMenuInvalidException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryRecordResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-3.4 Registrar orden de salida")
class RegisterProductionOutputOrderServiceTest {

    @Mock
    private BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;

    @Mock
    private BeneficiaryRepositoryPort beneficiaryRepositoryPort;

    @Mock
    private RecalculateSummaryReportUseCase recalcularResumenReporteUseCase;

    @Mock
    private MenuReportRepositoryPort menuReportRepositoryPort;

    @Mock
    private RegisterTransactionUseCase registerTransactionUseCase;

    @Mock
    private CurrentUserService currentUserService;

    private BeneficiaryControlMapper beneficiaryControlMapper;

    private AddRecordBeneficiaryService service;

    @BeforeEach
    void setUp() {
        beneficiaryControlMapper = new BeneficiaryControlMapper();

        service = new AddRecordBeneficiaryService(
                beneficiaryControlRepositoryPort,
                beneficiaryControlMapper,
                beneficiaryRepositoryPort,
                recalcularResumenReporteUseCase,
                menuReportRepositoryPort,
                registerTransactionUseCase,
                currentUserService
        );
    }

    @Test
    @DisplayName("Escenario 1: Registrar salida de menú correctamente")
    void registrarSalida_conDatosValidos_debeRegistrarBeneficiarioDescontarMenuRegistrarTransaccionYRecalcularResumen() {
        // given
        int reporteId = 1;

        MenuReport report = crearReporteConMenusDisponibles(20);
        Beneficiary beneficiario = crearBeneficiario(5, "Luis", "Ramirez");

        ControlBeneficiarioRequestDTO request = crearRequestValido(5, 3, new BigDecimal("4.00"));

        User usuarioActual = new User();
        usuarioActual.setId(99);

        when(menuReportRepositoryPort.findById(reporteId)).thenReturn(report);
        when(beneficiaryRepositoryPort.findById(5)).thenReturn(Optional.of(beneficiario));
        when(beneficiaryControlRepositoryPort.agregarBeneficiario(any(Integer.class), any(BeneficiaryControl.class)))
                .thenAnswer(invocation -> {
                    BeneficiaryControl control = invocation.getArgument(1);
                    control.setId(100);
                    return control;
                });
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);

        // when
        BeneficiaryRecordResponseDTO response =
                service.agregarRegistroBeneficiario(reporteId, request);

        // then
        assertEquals(100, response.getId());
        assertEquals(5, response.getBeneficiaryId());
        assertEquals("Luis", response.getName());
        assertEquals("Ramirez", response.getLastName());
        assertEquals(3, response.getCantidad());
        assertEquals(new BigDecimal("12.00"), response.getTotal());

        assertEquals(17, report.getQuantityRemaining());

        ArgumentCaptor<BeneficiaryControl> controlCaptor =
                ArgumentCaptor.forClass(BeneficiaryControl.class);

        verify(beneficiaryControlRepositoryPort)
                .agregarBeneficiario(any(Integer.class), controlCaptor.capture());

        BeneficiaryControl controlRegistrado = controlCaptor.getValue();

        assertEquals(beneficiario, controlRegistrado.getBeneficiario());
        assertEquals(3, controlRegistrado.getMenusAmount());
        assertEquals(new BigDecimal("4.00"), controlRegistrado.getMenuPrice());

        verify(menuReportRepositoryPort).update(report);
        verify(recalcularResumenReporteUseCase).recalcular(reporteId);

        ArgumentCaptor<TransactionRequestDTO> transactionCaptor =
                ArgumentCaptor.forClass(TransactionRequestDTO.class);

        verify(registerTransactionUseCase)
                .registrarTransaccion(transactionCaptor.capture());

        TransactionRequestDTO transaccion = transactionCaptor.getValue();

        assertEquals(TransactionReferenceType.MENU, transaccion.getReferenceType());
        assertNull(transaccion.getReferenceId());
        assertEquals("Arroz con pollo", transaccion.getItemName());
        assertEquals(MovementType.SALIDA, transaccion.getType());
        assertEquals(new BigDecimal("3"), transaccion.getAmount());
        assertEquals(new BigDecimal("20"), transaccion.getCurrentStock());
        assertEquals(TransactionSource.INVENTARIO, transaccion.getSource());
        assertEquals(99, transaccion.getUserId());
        assertNotNull(transaccion.getDateTime());
    }

    @Test
    @DisplayName("Escenario 2: Calcular total a pagar del beneficiario")
    void registrarSalida_conCantidadYPrecio_debeCalcularTotalAPagar() {
        // given
        int reporteId = 1;

        MenuReport report = crearReporteConMenusDisponibles(20);
        Beneficiary beneficiario = crearBeneficiario(5, "Ana", "Lopez");

        ControlBeneficiarioRequestDTO request =
                crearRequestValido(5, 4, new BigDecimal("3.50"));

        User usuarioActual = new User();
        usuarioActual.setId(99);

        when(menuReportRepositoryPort.findById(reporteId)).thenReturn(report);
        when(beneficiaryRepositoryPort.findById(5)).thenReturn(Optional.of(beneficiario));
        when(beneficiaryControlRepositoryPort.agregarBeneficiario(any(Integer.class), any(BeneficiaryControl.class)))
                .thenAnswer(invocation -> {
                    BeneficiaryControl control = invocation.getArgument(1);
                    control.setId(101);
                    return control;
                });
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);

        // when
        BeneficiaryRecordResponseDTO response =
                service.agregarRegistroBeneficiario(reporteId, request);

        // then
        assertEquals(4, response.getCantidad());
        assertEquals(new BigDecimal("14.00"), response.getTotal());
    }

    @Test
    @DisplayName("Escenario 3: Rechazar cantidad de menús nula")
    void registrarSalida_conCantidadNula_debeLanzarQuantityMenuInvalidException() {
        // given
        ControlBeneficiarioRequestDTO request = crearRequestValido(5, 1, new BigDecimal("4.00"));
        request.setMenusAmount(null);

        // when
        QuantityMenuInvalidException exception = assertThrows(
                QuantityMenuInvalidException.class,
                () -> service.agregarRegistroBeneficiario(1, request)
        );

        // then
        assertEquals("La cantidad de menus es obligatoria", exception.getMessage());

        verifyNoInteractions(menuReportRepositoryPort);
        verifyNoInteractions(beneficiaryRepositoryPort);
        verifyNoInteractions(beneficiaryControlRepositoryPort);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(recalcularResumenReporteUseCase);
    }

    @Test
    @DisplayName("Escenario 4: Rechazar cantidad de menús menor o igual a cero")
    void registrarSalida_conCantidadCero_debeLanzarQuantityMenuInvalidException() {
        // given
        ControlBeneficiarioRequestDTO request =
                crearRequestValido(5, 0, new BigDecimal("4.00"));

        // when
        QuantityMenuInvalidException exception = assertThrows(
                QuantityMenuInvalidException.class,
                () -> service.agregarRegistroBeneficiario(1, request)
        );

        // then
        assertEquals("La cantidad de menus debe ser mayor a 0", exception.getMessage());

        verifyNoInteractions(menuReportRepositoryPort);
        verifyNoInteractions(beneficiaryRepositoryPort);
        verifyNoInteractions(beneficiaryControlRepositoryPort);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(recalcularResumenReporteUseCase);
    }

    @Test
    @DisplayName("Escenario 5: Rechazar precio de menú nulo")
    void registrarSalida_conPrecioNulo_debeLanzarMenuPriceInvalidException() {
        // given
        ControlBeneficiarioRequestDTO request = crearRequestValido(5, 2, new BigDecimal("4.00"));
        request.setMenuPrice(null);

        // when
        MenuPriceInvalidException exception = assertThrows(
                MenuPriceInvalidException.class,
                () -> service.agregarRegistroBeneficiario(1, request)
        );

        // then
        assertEquals("El precio del menu es obligatorio", exception.getMessage());

        verifyNoInteractions(menuReportRepositoryPort);
        verifyNoInteractions(beneficiaryRepositoryPort);
        verifyNoInteractions(beneficiaryControlRepositoryPort);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(recalcularResumenReporteUseCase);
    }

    @Test
    @DisplayName("Escenario 6: Rechazar precio de menú negativo")
    void registrarSalida_conPrecioNegativo_debeLanzarMenuPriceInvalidException() {
        // given
        ControlBeneficiarioRequestDTO request =
                crearRequestValido(5, 2, new BigDecimal("-1.00"));

        // when
        MenuPriceInvalidException exception = assertThrows(
                MenuPriceInvalidException.class,
                () -> service.agregarRegistroBeneficiario(1, request)
        );

        // then
        assertEquals("El precio no puede ser negativo", exception.getMessage());

        verifyNoInteractions(menuReportRepositoryPort);
        verifyNoInteractions(beneficiaryRepositoryPort);
        verifyNoInteractions(beneficiaryControlRepositoryPort);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(recalcularResumenReporteUseCase);
    }

    @Test
    @DisplayName("Escenario 7: Rechazar salida con menús insuficientes")
    void registrarSalida_conMenusInsuficientes_debeLanzarRuntimeExceptionSinRegistrarSalida() {
        // given
        int reporteId = 1;

        MenuReport report = crearReporteConMenusDisponibles(2);

        ControlBeneficiarioRequestDTO request =
                crearRequestValido(5, 5, new BigDecimal("4.00"));

        when(menuReportRepositoryPort.findById(reporteId)).thenReturn(report);

        // when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.agregarRegistroBeneficiario(reporteId, request)
        );

        // then
        assertEquals("No hay suficientes menus disponibles. Quedan: 2", exception.getMessage());

        assertEquals(2, report.getQuantityRemaining());

        verify(menuReportRepositoryPort).findById(reporteId);

        verifyNoInteractions(beneficiaryRepositoryPort);
        verifyNoInteractions(beneficiaryControlRepositoryPort);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(recalcularResumenReporteUseCase);

        verify(menuReportRepositoryPort, never()).update(any(MenuReport.class));
    }

    @Test
    @DisplayName("Escenario 8: Rechazar beneficiario inexistente")
    void registrarSalida_conBeneficiarioInexistente_debeLanzarBeneficiaryNotFoundExceptionSinDescontarMenus() {
        // given
        int reporteId = 1;

        MenuReport report = crearReporteConMenusDisponibles(20);

        ControlBeneficiarioRequestDTO request =
                crearRequestValido(999, 3, new BigDecimal("4.00"));

        when(menuReportRepositoryPort.findById(reporteId)).thenReturn(report);
        when(beneficiaryRepositoryPort.findById(999)).thenReturn(Optional.empty());

        // when
        BeneficiaryNotFoundException exception = assertThrows(
                BeneficiaryNotFoundException.class,
                () -> service.agregarRegistroBeneficiario(reporteId, request)
        );

        // then
        assertEquals(
                "Beneficiario No Encontrado: Beneficiario no encontrado",
                exception.getMessage()
        );

        assertEquals(20, report.getQuantityRemaining());

        verify(menuReportRepositoryPort).findById(reporteId);
        verify(beneficiaryRepositoryPort).findById(999);

        verifyNoInteractions(beneficiaryControlRepositoryPort);
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(registerTransactionUseCase);
        verifyNoInteractions(recalcularResumenReporteUseCase);

        verify(menuReportRepositoryPort, never()).update(any(MenuReport.class));
    }

    private ControlBeneficiarioRequestDTO crearRequestValido(
            int beneficiarioId,
            Integer menusAmount,
            BigDecimal menuPrice
    ) {
        ControlBeneficiarioRequestDTO request = new ControlBeneficiarioRequestDTO();
        request.setBeneficiarioId(beneficiarioId);
        request.setMenusAmount(menusAmount);
        request.setMenuPrice(menuPrice);
        request.setPago(true);
        request.setEntregado(true);
        return request;
    }

    private MenuReport crearReporteConMenusDisponibles(int quantityRemaining) {
        DishMenu dishMenu = new DishMenu();
        dishMenu.setId(10);
        dishMenu.setName("Arroz con pollo");

        MenuReport report = new MenuReport();
        report.setId(1);
        report.setDishMenu(dishMenu);
        report.setQuantityPrepared(20);
        report.setQuantityRemaining(quantityRemaining);

        return report;
    }

    private Beneficiary crearBeneficiario(
            int id,
            String name,
            String lastname
    ) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setId(id);
        beneficiary.setName(name);
        beneficiary.setLastname(lastname);
        beneficiary.setDni("12345678");
        return beneficiary;
    }
}