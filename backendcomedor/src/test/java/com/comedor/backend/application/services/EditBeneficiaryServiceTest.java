package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryNotFoundException;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeInactiveException;
import com.comedor.backend.domain.exceptions.DniAlreadyRegisteredException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditBeneficiaryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-1.6 Editar información de beneficiarios")
class EditBeneficiaryServiceTest {

    @Mock
    private BeneficiaryRepositoryPort beneficiaryRepositoryPort;

    @Mock
    private RegisterModificationUseCase registerModificationUseCase;

    @Mock
    private BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;

    private EditBeneficiaryService service;

    private DeactivateBeneficiaryService deactivateBeneficiaryService;

    private ActivateBeneficiaryService activateBeneficiaryService;

    @BeforeEach
    void setUp() {
        service = new EditBeneficiaryService(
                beneficiaryRepositoryPort,
                registerModificationUseCase,
                beneficiaryTypeRepositoryPort
        );
        deactivateBeneficiaryService = new DeactivateBeneficiaryService(
                beneficiaryRepositoryPort,
                registerModificationUseCase
        );

        activateBeneficiaryService = new ActivateBeneficiaryService(
                beneficiaryRepositoryPort,
                registerModificationUseCase
        );
    }

    @Test
    @DisplayName("Escenario 1: Editar datos principales del beneficiario")
    void editarBeneficiario_conDatosPrincipales_debeActualizarDatosYRegistrarAuditoria() {
        // given
        int beneficiaryId = 1;

        BeneficiaryType tipoActual =
                crearTipoBeneficiario(1, "Adulto mayor", Status.ACTIVO);

        Beneficiary beneficiary = crearBeneficiario(
                beneficiaryId,
                "12345678",
                "MARIA",
                "LOPEZ",
                tipoActual,
                Status.ACTIVO
        );

        EditBeneficiaryRequestDTO request = new EditBeneficiaryRequestDTO();
        request.setDni("87654321");
        request.setName("Rosa");
        request.setLastname("García");

        when(beneficiaryRepositoryPort.findById(beneficiaryId))
                .thenReturn(Optional.of(beneficiary));

        when(beneficiaryRepositoryPort.existePorDni("87654321"))
                .thenReturn(false);

        when(beneficiaryRepositoryPort.guardar(beneficiary))
                .thenReturn(beneficiary);

        // when
        Beneficiary response = service.editar(beneficiaryId, request);

        // then
        assertEquals("87654321", response.getDni());
        assertEquals("Rosa", response.getName());
        assertEquals("García", response.getLastname());
        assertSame(tipoActual, response.getBeneficiaryType());

        verify(beneficiaryRepositoryPort).guardar(beneficiary);

        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase, times(3))
                .registrar(captor.capture());

        List<ModificationsRequestDTO> modificaciones = captor.getAllValues();

        assertEquals("dni", modificaciones.get(0).getEditedAttribute());
        assertEquals("12345678", modificaciones.get(0).getPreviousValue());
        assertEquals("87654321", modificaciones.get(0).getNewValue());

        assertEquals("name", modificaciones.get(1).getEditedAttribute());
        assertEquals("MARIA", modificaciones.get(1).getPreviousValue());
        assertEquals("Rosa", modificaciones.get(1).getNewValue());

        assertEquals("lastname", modificaciones.get(2).getEditedAttribute());
        assertEquals("LOPEZ", modificaciones.get(2).getPreviousValue());
        assertEquals("García", modificaciones.get(2).getNewValue());
    }

    @Test
    @DisplayName("Escenario 2: Rechazar edición con DNI duplicado")
    void editarBeneficiario_conDniDuplicado_debeLanzarDniAlreadyRegisteredExceptionYNoGuardar() {
        // given
        int beneficiaryId = 1;

        BeneficiaryType tipoActual =
                crearTipoBeneficiario(1, "Adulto mayor", Status.ACTIVO);

        Beneficiary beneficiary = crearBeneficiario(
                beneficiaryId,
                "12345678",
                "MARIA",
                "LOPEZ",
                tipoActual,
                Status.ACTIVO
        );

        EditBeneficiaryRequestDTO request = new EditBeneficiaryRequestDTO();
        request.setDni("87654321");

        when(beneficiaryRepositoryPort.findById(beneficiaryId))
                .thenReturn(Optional.of(beneficiary));

        when(beneficiaryRepositoryPort.existePorDni("87654321"))
                .thenReturn(true);

        // when
        DniAlreadyRegisteredException exception = assertThrows(
                DniAlreadyRegisteredException.class,
                () -> service.editar(beneficiaryId, request)
        );

        // then
        assertEquals(
                "Ya existe un Beneficiario con el DNI: 87654321",
                exception.getMessage()
        );

        assertEquals("12345678", beneficiary.getDni());

        verify(beneficiaryRepositoryPort, never()).guardar(any(Beneficiary.class));
        verifyNoInteractions(registerModificationUseCase);
    }

    @Test
    @DisplayName("Escenario 3: Editar tipo de beneficiario")
    void editarBeneficiario_conTipoActivo_debeCambiarTipoYRegistrarAuditoria() {
        // given
        int beneficiaryId = 1;

        BeneficiaryType tipoActual =
                crearTipoBeneficiario(1, "Adulto mayor", Status.ACTIVO);

        BeneficiaryType tipoNuevo =
                crearTipoBeneficiario(2, "Caso social", Status.ACTIVO);

        Beneficiary beneficiary = crearBeneficiario(
                beneficiaryId,
                "12345678",
                "MARIA",
                "LOPEZ",
                tipoActual,
                Status.ACTIVO
        );

        EditBeneficiaryRequestDTO request = new EditBeneficiaryRequestDTO();
        request.setBeneficiaryTypeId(2);

        when(beneficiaryRepositoryPort.findById(beneficiaryId))
                .thenReturn(Optional.of(beneficiary));

        when(beneficiaryTypeRepositoryPort.findById(2))
                .thenReturn(tipoNuevo);

        when(beneficiaryRepositoryPort.guardar(beneficiary))
                .thenReturn(beneficiary);

        // when
        Beneficiary response = service.editar(beneficiaryId, request);

        // then
        assertSame(tipoNuevo, response.getBeneficiaryType());
        assertEquals(2, response.getBeneficiaryType().getId());
        assertEquals("CASO SOCIAL", response.getBeneficiaryType().getName());

        verify(beneficiaryRepositoryPort).guardar(beneficiary);
        verify(beneficiaryTypeRepositoryPort).findById(2);

        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase).registrar(captor.capture());

        ModificationsRequestDTO modification = captor.getValue();

        assertEquals("Beneficiario", modification.getEditedClass());
        assertEquals("TIPO DE BENEFICIARIO", modification.getName());
        assertEquals("MARIA LOPEZ", modification.getEditedAttribute());
        assertEquals("ADULTO MAYOR", modification.getPreviousValue());
        assertEquals("CASO SOCIAL", modification.getNewValue());
    }

    @Test
    @DisplayName("Escenario 4: Rechazar tipo de beneficiario inactivo")
    void editarBeneficiario_conTipoInactivo_debeLanzarBeneficiaryTypeInactiveExceptionYNoGuardar() {
        // given
        int beneficiaryId = 1;

        BeneficiaryType tipoActual =
                crearTipoBeneficiario(1, "Adulto mayor", Status.ACTIVO);

        BeneficiaryType tipoInactivo =
                crearTipoBeneficiario(2, "Caso social", Status.INACTIVO);

        Beneficiary beneficiary = crearBeneficiario(
                beneficiaryId,
                "12345678",
                "MARIA",
                "LOPEZ",
                tipoActual,
                Status.ACTIVO
        );

        EditBeneficiaryRequestDTO request = new EditBeneficiaryRequestDTO();
        request.setBeneficiaryTypeId(2);

        when(beneficiaryRepositoryPort.findById(beneficiaryId))
                .thenReturn(Optional.of(beneficiary));

        when(beneficiaryTypeRepositoryPort.findById(2))
                .thenReturn(tipoInactivo);

        // when
        BeneficiaryTypeInactiveException exception = assertThrows(
                BeneficiaryTypeInactiveException.class,
                () -> service.editar(beneficiaryId, request)
        );

        // then
        assertEquals(
                "No puedes elegir un tipo inactivo. El tipo de beneficiario : CASO SOCIAL ,esta inactivo.",
                exception.getMessage()
        );

        assertSame(tipoActual, beneficiary.getBeneficiaryType());

        verify(beneficiaryRepositoryPort, never()).guardar(any(Beneficiary.class));
        verifyNoInteractions(registerModificationUseCase);
    }

    @Test
    @DisplayName("Escenario 5: Rechazar beneficiario inexistente")
    void editarBeneficiario_inexistente_debeLanzarBeneficiaryNotFoundException() {
        // given
        int beneficiaryId = 999;

        EditBeneficiaryRequestDTO request = new EditBeneficiaryRequestDTO();
        request.setName("Rosa");

        when(beneficiaryRepositoryPort.findById(beneficiaryId))
                .thenReturn(Optional.empty());

        // when
        BeneficiaryNotFoundException exception = assertThrows(
                BeneficiaryNotFoundException.class,
                () -> service.editar(beneficiaryId, request)
        );

        // then
        assertEquals(
                "Beneficiario No Encontrado: Usuario No Encontrado: 999",
                exception.getMessage()
        );

        verify(beneficiaryRepositoryPort, never()).guardar(any(Beneficiary.class));
        verifyNoInteractions(registerModificationUseCase);
        verifyNoInteractions(beneficiaryTypeRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 6: Editar parcialmente sin cambiar campos nulos")
    void editarBeneficiario_requestParcial_debeModificarSoloCamposEnviados() {
        // given
        int beneficiaryId = 1;

        BeneficiaryType tipoActual =
                crearTipoBeneficiario(1, "Adulto mayor", Status.ACTIVO);

        Beneficiary beneficiary = crearBeneficiario(
                beneficiaryId,
                "12345678",
                "MARIA",
                "LOPEZ",
                tipoActual,
                Status.ACTIVO
        );

        EditBeneficiaryRequestDTO request = new EditBeneficiaryRequestDTO();
        request.setName("Rosa");

        when(beneficiaryRepositoryPort.findById(beneficiaryId))
                .thenReturn(Optional.of(beneficiary));

        when(beneficiaryRepositoryPort.guardar(beneficiary))
                .thenReturn(beneficiary);

        // when
        Beneficiary response = service.editar(beneficiaryId, request);

        // then
        assertEquals("12345678", response.getDni());
        assertEquals("Rosa", response.getName());
        assertEquals("LOPEZ", response.getLastname());
        assertSame(tipoActual, response.getBeneficiaryType());

        verify(beneficiaryRepositoryPort).guardar(beneficiary);

        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase).registrar(captor.capture());

        ModificationsRequestDTO modification = captor.getValue();

        assertEquals("name", modification.getEditedAttribute());
        assertEquals("MARIA", modification.getPreviousValue());
        assertEquals("Rosa", modification.getNewValue());

        verify(beneficiaryTypeRepositoryPort, never()).findById(any());
    }

    @Test
    @DisplayName("Escenario 7: Desactivar beneficiario")
    void desactivarBeneficiario_existente_debeCambiarEstadoAInactivoYRegistrarAuditoria() {
        // given
        int beneficiaryId = 1;

        Beneficiary beneficiary = crearBeneficiario(
                beneficiaryId,
                "12345678",
                "MARIA",
                "LOPEZ",
                Status.INACTIVO
        );

        when(beneficiaryRepositoryPort.desactivar(beneficiaryId))
                .thenReturn(beneficiary);

        // when
        Beneficiary response =
                deactivateBeneficiaryService.desactivar(beneficiaryId);

        // then
        assertEquals(beneficiaryId, response.getId());
        assertEquals(Status.INACTIVO, response.getStatus());

        verify(beneficiaryRepositoryPort).desactivar(beneficiaryId);

        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase).registrar(captor.capture());

        ModificationsRequestDTO modification = captor.getValue();

        assertEquals("Beneficiario", modification.getEditedClass());
        assertEquals("MARIA LOPEZ", modification.getName());
        assertEquals("status", modification.getEditedAttribute());
        assertEquals("ACTIVO", modification.getPreviousValue());
        assertEquals("INACTIVO", modification.getNewValue());
    }

    @Test
    @DisplayName("Escenario 8: Activar beneficiario")
    void activarBeneficiario_existente_debeCambiarEstadoAActivoYRegistrarAuditoria() {
        // given
        int beneficiaryId = 1;

        Beneficiary beneficiary = crearBeneficiario(
                beneficiaryId,
                "12345678",
                "MARIA",
                "LOPEZ",
                Status.ACTIVO
        );

        when(beneficiaryRepositoryPort.activar(beneficiaryId))
                .thenReturn(beneficiary);

        // when
        Beneficiary response =
                activateBeneficiaryService.activar(beneficiaryId);

        // then
        assertEquals(beneficiaryId, response.getId());
        assertEquals(Status.ACTIVO, response.getStatus());

        verify(beneficiaryRepositoryPort).activar(beneficiaryId);

        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase).registrar(captor.capture());

        ModificationsRequestDTO modification = captor.getValue();

        assertEquals("Beneficiario", modification.getEditedClass());
        assertEquals("MARIA LOPEZ", modification.getName());
        assertEquals("status", modification.getEditedAttribute());
        assertEquals("INACTIVO", modification.getPreviousValue());
        assertEquals("ACTIVO", modification.getNewValue());
    }

    private Beneficiary crearBeneficiario(
            int id,
            String dni,
            String name,
            String lastname,
            Status status
    ) {
        BeneficiaryType type = new BeneficiaryType();
        type.setId(1);
        type.setName("Adulto mayor");
        type.setDesc("Tipo de beneficiario");
        type.setMenu_cost(new BigDecimal("3.50"));
        type.setStatus(Status.ACTIVO);

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setId(id);
        beneficiary.setDni(dni);
        beneficiary.setName(name);
        beneficiary.setLastname(lastname);
        beneficiary.setBeneficiaryType(type);
        beneficiary.setStatus(status);

        return beneficiary;
    }

    private Beneficiary crearBeneficiario(
            int id,
            String dni,
            String name,
            String lastname,
            BeneficiaryType beneficiaryType,
            Status status
    ) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setId(id);
        beneficiary.setDni(dni);
        beneficiary.setName(name);
        beneficiary.setLastname(lastname);
        beneficiary.setBeneficiaryType(beneficiaryType);
        beneficiary.setStatus(status);
        return beneficiary;
    }

    private BeneficiaryType crearTipoBeneficiario(
            Integer id,
            String name,
            Status status
    ) {
        BeneficiaryType type = new BeneficiaryType();
        type.setId(id);
        type.setName(name);
        type.setDesc("Tipo de beneficiario " + name);
        type.setMenu_cost(new BigDecimal("3.50"));
        type.setStatus(status);
        return type;
    }
}