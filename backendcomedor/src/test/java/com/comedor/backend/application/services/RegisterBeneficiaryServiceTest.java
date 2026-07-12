package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryMapper;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryRequestDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-1.4 Registro de beneficiarios")
class RegisterBeneficiaryServiceTest {

    @Mock
    private BeneficiaryRepositoryPort beneficiaryRepositoryPort;

    @Mock
    private BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;

    private BeneficiaryMapper beneficiaryMapper;

    private RegisterBeneficiaryService service;

    @BeforeEach
    void setUp() {
        beneficiaryMapper = new BeneficiaryMapper();

        service = new RegisterBeneficiaryService(
                beneficiaryRepositoryPort,
                beneficiaryTypeRepositoryPort,
                beneficiaryMapper
        );
    }

    @Test
    @DisplayName("Escenario 1: Registrar beneficiario correctamente")
    void registrarBeneficiario_conDatosValidos_debeGuardarBeneficiario() {
        // given
        BeneficiaryRequestDTO request = crearRequest(
                "12345678",
                "María",
                "López Ramos",
                1
        );

        BeneficiaryType tipo = crearTipoBeneficiario(
                1,
                "Adulto mayor",
                new BigDecimal("3.50")
        );

        when(beneficiaryRepositoryPort.existePorDni("12345678"))
                .thenReturn(false);

        when(beneficiaryTypeRepositoryPort.findById(1))
                .thenReturn(tipo);

        when(beneficiaryRepositoryPort.guardar(org.mockito.ArgumentMatchers.any(Beneficiary.class)))
                .thenAnswer(invocation -> {
                    Beneficiary beneficiary = invocation.getArgument(0);
                    beneficiary.setId(100);
                    return beneficiary;
                });

        // when
        Beneficiary response = service.registrarBeneficiario(request);

        // then
        assertEquals(100, response.getId());
        assertEquals("12345678", response.getDni());
        assertEquals("María", response.getName());
        assertEquals("López Ramos", response.getLastname());
        assertEquals(Status.ACTIVO, response.getStatus());
        assertSame(tipo, response.getBeneficiaryType());

        ArgumentCaptor<Beneficiary> captor =
                ArgumentCaptor.forClass(Beneficiary.class);

        verify(beneficiaryRepositoryPort).guardar(captor.capture());

        Beneficiary beneficiaryGuardado = captor.getValue();

        assertEquals("12345678", beneficiaryGuardado.getDni());
        assertEquals("María", beneficiaryGuardado.getName());
        assertEquals("López Ramos", beneficiaryGuardado.getLastname());
        assertSame(tipo, beneficiaryGuardado.getBeneficiaryType());
    }

    @Test
    @DisplayName("Escenario 2: Registrar beneficiario con estado activo")
    void registrarBeneficiario_nuevo_debeRegistrarseConEstadoActivo() {
        // given
        BeneficiaryRequestDTO request = crearRequest(
                "87654321",
                "Juan",
                "Pérez Soto",
                2
        );

        BeneficiaryType tipo = crearTipoBeneficiario(
                2,
                "General",
                new BigDecimal("4.00")
        );

        when(beneficiaryRepositoryPort.existePorDni("87654321"))
                .thenReturn(false);

        when(beneficiaryTypeRepositoryPort.findById(2))
                .thenReturn(tipo);

        when(beneficiaryRepositoryPort.guardar(org.mockito.ArgumentMatchers.any(Beneficiary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Beneficiary response = service.registrarBeneficiario(request);

        // then
        assertEquals(Status.ACTIVO, response.getStatus());

        ArgumentCaptor<Beneficiary> captor =
                ArgumentCaptor.forClass(Beneficiary.class);

        verify(beneficiaryRepositoryPort).guardar(captor.capture());

        Beneficiary beneficiaryGuardado = captor.getValue();

        assertEquals(Status.ACTIVO, beneficiaryGuardado.getStatus());
    }

    @Test
    @DisplayName("Escenario 3: Rechazar beneficiario duplicado por DNI")
    void registrarBeneficiario_conDniDuplicado_debeLanzarIllegalArgumentExceptionYNoGuardar() {
        // given
        BeneficiaryRequestDTO request = crearRequest(
                "12345678",
                "María",
                "López Ramos",
                1
        );

        when(beneficiaryRepositoryPort.existePorDni("12345678"))
                .thenReturn(true);

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarBeneficiario(request)
        );

        // then
        assertEquals(
                "Ya existe un beneficiario registrado con el DNI 12345678",
                exception.getMessage()
        );

        verify(beneficiaryRepositoryPort).existePorDni("12345678");
        verify(beneficiaryRepositoryPort, never()).guardar(org.mockito.ArgumentMatchers.any(Beneficiary.class));
        verifyNoInteractions(beneficiaryTypeRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 4: Asociar tipo de beneficiario seleccionado")
    void registrarBeneficiario_conTipoSeleccionado_debeConsultarTipoYAsignarloAntesDeGuardar() {
        // given
        BeneficiaryRequestDTO request = crearRequest(
                "11223344",
                "Rosa",
                "García Torres",
                3
        );

        BeneficiaryType tipo = crearTipoBeneficiario(
                3,
                "Caso social",
                new BigDecimal("2.00")
        );

        when(beneficiaryRepositoryPort.existePorDni("11223344"))
                .thenReturn(false);

        when(beneficiaryTypeRepositoryPort.findById(3))
                .thenReturn(tipo);

        when(beneficiaryRepositoryPort.guardar(org.mockito.ArgumentMatchers.any(Beneficiary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Beneficiary response = service.registrarBeneficiario(request);

        // then
        assertSame(tipo, response.getBeneficiaryType());
        assertEquals(3, response.getBeneficiaryType().getId());
        assertEquals("CASO SOCIAL", response.getBeneficiaryType().getName());

        ArgumentCaptor<Beneficiary> captor =
                ArgumentCaptor.forClass(Beneficiary.class);

        verify(beneficiaryRepositoryPort).guardar(captor.capture());

        Beneficiary beneficiaryGuardado = captor.getValue();

        assertSame(tipo, beneficiaryGuardado.getBeneficiaryType());

        verify(beneficiaryTypeRepositoryPort).findById(3);
    }

    private BeneficiaryRequestDTO crearRequest(
            String dni,
            String name,
            String lastname,
            Integer beneficiaryTypeId
    ) {
        BeneficiaryRequestDTO request = new BeneficiaryRequestDTO();
        request.setDni(dni);
        request.setName(name);
        request.setLastname(lastname);
        request.setBeneficiaryTypeId(beneficiaryTypeId);
        return request;
    }

    private BeneficiaryType crearTipoBeneficiario(
            Integer id,
            String name,
            BigDecimal menuCost
    ) {
        BeneficiaryType type = new BeneficiaryType();
        type.setId(id);
        type.setName(name);
        type.setDesc("Tipo de beneficiario " + name);
        type.setMenu_cost(menuCost);
        type.setStatus(Status.ACTIVO);
        return type;
    }
}