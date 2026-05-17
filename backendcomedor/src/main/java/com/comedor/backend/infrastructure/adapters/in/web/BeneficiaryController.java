package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.common.mapper.BeneficiaryMapper;
import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.domain.exceptions.BeneficiarioNoEncontradoException;
import com.comedor.backend.domain.exceptions.DniYaRegistradoException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.PersonalDataReniec;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiarioResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DatosPersonalesResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/beneficiary")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final RegistrarBeneficiarioUseCase registrarBeneficiarioUseCase;
    private final BeneficiaryMapper beneficiaryMapper;

    private final ConsultarDatosPorDniUseCase consultarDatosPorDniUseCase;
    private final ConsultarYRegistrarReniecUseCase consultarYRegistrarReniecUseCase;

    private final EditarBeneficiarioUseCase editarBeneficiarioUseCase;
    private final ListarBeneficiariosPorEstadoUseCase listarBeneficiariosPorEstadoUseCase;

    @PreAuthorize("hasRole('PRESIDENTA')")
    @PostMapping("/create")
    public ResponseEntity<BeneficiarioResponseDTO> registrar(@Valid @RequestBody BeneficiarioRequestDTO beneficiarioRequestDTO) {

        Beneficiary beneficiary = beneficiaryMapper.convertToDomain(beneficiarioRequestDTO);

        Beneficiary beneficiaryRegistered = registrarBeneficiarioUseCase.registrarBeneficiario(beneficiary);

        BeneficiarioResponseDTO beneficiarioResponseDTO = beneficiaryMapper.convertToDTO(beneficiaryRegistered);

        return new ResponseEntity<>(beneficiarioResponseDTO, HttpStatus.CREATED);

    }
    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @GetMapping("/reniec/{dni}")
    public ResponseEntity<?> consultaPorDni(@PathVariable String dni) {
        try {
            PersonalDataReniec personalDataReniec = consultarDatosPorDniUseCase.consultar(dni);

            DatosPersonalesResponseDTO datosPersonalesResponseDTO = beneficiaryMapper.convertDatosPersonalesToDTO(personalDataReniec);

            return ResponseEntity.ok(datosPersonalesResponseDTO);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar el DNI.");
        }
    }
    @PreAuthorize("hasRole('PRESIDENTA')")
    @PostMapping("/reniec/{dni}")
    public ResponseEntity<?> consultarYRegistrar(@PathVariable String dni) {
        try {
            Beneficiary beneficiary = consultarYRegistrarReniecUseCase.consultarYRegistrar(dni);

            return new ResponseEntity<>(beneficiaryMapper.convertToDTO(beneficiary),HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar Beneficiario");
        }
    }

    @PreAuthorize("hasRole('PRESIDENTA')")
    @PutMapping("edit/{id}")
    public ResponseEntity<?> editar(@PathVariable int id, @Valid @RequestBody EditarBeneficiarioRequestDTO editarBeneficiarioRequest) {
        try {
            Beneficiary beneficiaryUpdated = editarBeneficiarioUseCase.editar(id, editarBeneficiarioRequest);
            BeneficiarioResponseDTO responseDTO = beneficiaryMapper.convertToDTO(beneficiaryUpdated);

            return ResponseEntity.ok(responseDTO);
        } catch (BeneficiarioNoEncontradoException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DniYaRegistradoException e) {
            return  ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al editar el Beneficiario");
        }
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @GetMapping("/list")
    public List<BeneficiarioResponseDTO> listarBeneficiaros(@RequestParam(required = false) Estado estado)
    {
        return listarBeneficiariosPorEstadoUseCase.listarBeneficiarioPorEstado(estado);
    }

}
