package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.common.mapper.BeneficiarioMapper;
import com.comedor.backend.application.ports.in.ConsultarDatosPorDniUseCase;
import com.comedor.backend.application.ports.in.ConsultarYRegistrarReniecUseCase;
import com.comedor.backend.application.ports.in.EditarBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RegistrarBeneficiarioUseCase;
import com.comedor.backend.domain.exceptions.BeneficiarioNoEncontradoException;
import com.comedor.backend.domain.exceptions.DniYaRegistradoException;
import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.domain.model.DatosPersonales;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequest;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiarioResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DatosPersonalesResponseDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.BeneficiarioPersistenceMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/beneficiario")
@RequiredArgsConstructor
public class BenficiarioController {

    private final RegistrarBeneficiarioUseCase registrarBeneficiarioUseCase;
    private final BeneficiarioMapper beneficiarioMapper;

    private final ConsultarDatosPorDniUseCase consultarDatosPorDniUseCase;
    private final ConsultarYRegistrarReniecUseCase consultarYRegistrarReniecUseCase;

    private final EditarBeneficiarioUseCase editarBeneficiarioUseCase;

    @PreAuthorize("hasRole('PRESIDENTA')")
    @PostMapping("/create")
    public ResponseEntity<BeneficiarioResponseDTO> registrar(@Valid @RequestBody BeneficiarioRequestDTO beneficiarioRequestDTO) {

        Beneficiario beneficiario = beneficiarioMapper.convertToDomain(beneficiarioRequestDTO);

        Beneficiario beneficiarioRegistrado = registrarBeneficiarioUseCase.registrarBeneficiario(beneficiario);

        BeneficiarioResponseDTO beneficiarioResponseDTO = beneficiarioMapper.convertToDTO(beneficiarioRegistrado);

        return new ResponseEntity<>(beneficiarioResponseDTO, HttpStatus.CREATED);

    }

    @GetMapping("/reniec/{dni}")
    public ResponseEntity<?> consultaPorDni(@PathVariable String dni) {
        try {
            DatosPersonales datosPersonales = consultarDatosPorDniUseCase.consultar(dni);

            DatosPersonalesResponseDTO datosPersonalesResponseDTO = beneficiarioMapper.convertDatosPersonalesToDTO(datosPersonales);

            return ResponseEntity.ok(datosPersonalesResponseDTO);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar el DNI.");
        }
    }

    @PostMapping("/reniec/{dni}")
    public ResponseEntity<?> consultarYRegistrar(@PathVariable String dni) {
        try {
            Beneficiario beneficiario = consultarYRegistrarReniecUseCase.consultarYRegistrar(dni);

            return new ResponseEntity<>(beneficiarioMapper.convertToDTO(beneficiario),HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar Beneficiario");
        }
    }

    @PreAuthorize("hasRole('PRESIDENTA')")
    @PutMapping("editar/{id}")
    public ResponseEntity<?> editar(@PathVariable int id, @Valid @RequestBody EditarBeneficiarioRequest editarBeneficiarioRequest) {
        try {
            Beneficiario beneficiarioActualizado = editarBeneficiarioUseCase.editar(id, editarBeneficiarioRequest);
            BeneficiarioResponseDTO responseDTO = beneficiarioMapper.convertToDTO(beneficiarioActualizado);

            return ResponseEntity.ok(responseDTO);
        } catch (BeneficiarioNoEncontradoException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DniYaRegistradoException e) {
            return  ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al editar el Beneficiario");
        }
    }

}
