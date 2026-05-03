package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.common.mapper.BeneficiarioMapper;
import com.comedor.backend.application.ports.in.RegistrarBeneficiarioUseCase;
import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiarioResponseDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.BeneficiarioPersistenceMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/create")
    public ResponseEntity<BeneficiarioResponseDTO> registrar(@Valid @RequestBody BeneficiarioRequestDTO beneficiarioRequestDTO) {

        Beneficiario beneficiario = beneficiarioMapper.convertToDomain(beneficiarioRequestDTO);

        Beneficiario beneficiarioRegistrado = registrarBeneficiarioUseCase.registrarBeneficiario(beneficiario);

        BeneficiarioResponseDTO beneficiarioResponseDTO = beneficiarioMapper.convertToDTO(beneficiarioRegistrado);

        return new ResponseEntity<>(beneficiarioResponseDTO, HttpStatus.CREATED);

    }

}
