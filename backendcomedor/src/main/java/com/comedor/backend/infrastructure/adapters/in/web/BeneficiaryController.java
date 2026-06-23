package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.common.mapper.BeneficiaryMapper;
import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.domain.exceptions.BeneficiaryNotFoundException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.PersonalDataReniec;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditBeneficiaryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PersonalDataResponseDTO;
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

    private final RegisterBeneficiaryUseCase registerBeneficiaryUseCase;
    private final BeneficiaryMapper beneficiaryMapper;

    private final GetDataByDniUseCase getDataByDniUseCase;
    private final GetAndRegisterByReniecUseCase getAndRegisterByReniecUseCase;

    private final EditBeneficiaryUseCase editBeneficiaryUseCase;
    private final ListBeneficiariesByStatusUseCase listBeneficiariesByStatusUseCase;

    private final ActivateBeneficiaryUseCase activateBeneficiaryUseCase;
    private final DeactivateBeneficiaryUseCase deactivateBeneficiaryUseCase;

    @PreAuthorize("hasAuthority('BENEFICIARY_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<BeneficiaryResponseDTO> registrar(@Valid @RequestBody BeneficiaryRequestDTO beneficiaryRequestDTO) {


        Beneficiary beneficiaryRegistered = registerBeneficiaryUseCase.registrarBeneficiario(beneficiaryRequestDTO);

        BeneficiaryResponseDTO beneficiaryResponseDTO = beneficiaryMapper.convertToDTO(beneficiaryRegistered);

        return new ResponseEntity<>(beneficiaryResponseDTO, HttpStatus.CREATED);

    }
    @PreAuthorize("hasAuthority('BENEFICIARY_SEARCH_BY_DNI')")
    @GetMapping("/reniec/{dni}")
    public ResponseEntity<?> consultaPorDni(@PathVariable String dni) {
        try {
            PersonalDataReniec personalDataReniec = getDataByDniUseCase.consultar(dni);

            PersonalDataResponseDTO personalDataResponseDTO = beneficiaryMapper.convertDatosPersonalesToDTO(personalDataReniec);

            return ResponseEntity.ok(personalDataResponseDTO);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar el DNI.");
        }
    }

    @PreAuthorize("hasAuthority('BENEFICIARY_SEARCH_BY_DNI')")
    @GetMapping("/{dni}")
    public ResponseEntity<?> searchBeneficiary(@PathVariable String dni) {
        try {
            Beneficiary beneficiary = getDataByDniUseCase.consultarBeneficiary(dni);

            BeneficiaryResponseDTO beneficiaryResponseDTO = beneficiaryMapper.convertToDTO(beneficiary);

            return ResponseEntity.ok(beneficiaryResponseDTO);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar el DNI.");
        }
    }

    @PreAuthorize("hasAuthority('BENEFICIARY_CREATE_BY_DNI')")
    @PostMapping("/reniec/{dni}")
    public ResponseEntity<?> consultarYRegistrar(@PathVariable String dni) {
        try {
            Beneficiary beneficiary = getAndRegisterByReniecUseCase.consultarYRegistrar(dni);

            return new ResponseEntity<>(beneficiaryMapper.convertToDTO(beneficiary),HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar Beneficiario");
        }
    }

    @PreAuthorize("hasAuthority('BENEFICIARY_EDIT')")
    @PutMapping("edit/{id}")
    public ResponseEntity<?> editar(@PathVariable int id, @Valid @RequestBody EditBeneficiaryRequestDTO editarBeneficiarioRequest) {

            Beneficiary beneficiaryUpdated = editBeneficiaryUseCase.editar(id, editarBeneficiarioRequest);
            BeneficiaryResponseDTO responseDTO = beneficiaryMapper.convertToDTO(beneficiaryUpdated);
            return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAuthority('BENEFICIARY_LIST_BY_STATUS')")
    @GetMapping("/list")
    public List<BeneficiaryResponseDTO> listarBeneficiaros(@RequestParam(required = false) Status status)
    {
        return listBeneficiariesByStatusUseCase.listarBeneficiarioPorEstado(status);
    }

    @PreAuthorize("hasAuthority('BENEFICIARY_CHANGE_STATUS')")
    @PostMapping("/changeStatus/{id}")
    public ResponseEntity<?> cambiarEstado(@PathVariable int id, @RequestParam Status status) {
        try {
            Beneficiary beneficiary = switch (status) {
                case ACTIVO -> activateBeneficiaryUseCase.activar(id);
                case INACTIVO -> deactivateBeneficiaryUseCase.desactivar(id);
            };
            return ResponseEntity.ok(beneficiaryMapper.convertToDTO(beneficiary));
        } catch (BeneficiaryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cambiar estado");
        }
    }

}
