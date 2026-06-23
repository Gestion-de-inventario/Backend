package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ChangeStatusBeneficiaryTypeUseCase;
import com.comedor.backend.application.ports.in.CreateBeneficiaryTypeUseCase;
import com.comedor.backend.application.ports.in.EditBeneficiaryTypeUseCase;
import com.comedor.backend.application.ports.in.ListBeneficiariesTypesByStatusUseCase;
import com.comedor.backend.domain.model.enums.ChangeStatus;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryTypeRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/beneficiary/type")
@RequiredArgsConstructor
public class BeneficiaryTypeController {

    private final EditBeneficiaryTypeUseCase editBeneficiaryTypeUseCase;
    private final CreateBeneficiaryTypeUseCase createBeneficiaryTypeUseCase;
    private final ChangeStatusBeneficiaryTypeUseCase changeStatusBeneficiaryTypeUseCase;
    private final ListBeneficiariesTypesByStatusUseCase listBeneficiariesTypesByStatusUseCase;

    @PreAuthorize("hasAuthority('BENEFICIARY_TYPE_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<BeneficiaryTypeResponseDTO> create(@Valid @RequestBody BeneficiaryTypeRequestDTO requestDTO) {
        BeneficiaryTypeResponseDTO responseDTO = createBeneficiaryTypeUseCase.createBeneficiaryType(requestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('BENEFICIARY_TYPE_LIST_BY_STATUS')")
    public ResponseEntity<List<BeneficiaryTypeResponseDTO>>
    listBeneficiariesTypesByStatus(
            @RequestParam(required = false) Status status) {

        return ResponseEntity.ok(
                listBeneficiariesTypesByStatusUseCase
                        .listByStatus(status)
        );
    }

    @PutMapping("/changeStatus/{id}")
    @PreAuthorize("hasAuthority('BENEFICIARY_TYPE_CHANGE_STATUS')")
    public ResponseEntity<BeneficiaryTypeResponseDTO> changeStatus(
            @PathVariable Integer id,
            @RequestParam() ChangeStatus status) {

        return ResponseEntity.ok(
                changeStatusBeneficiaryTypeUseCase.changeStatus(id, status)
        );
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('BENEFICIARY_TYPE_EDIT')")
    public ResponseEntity<BeneficiaryTypeResponseDTO> edit(
            @PathVariable int id,
            @RequestBody BeneficiaryTypeRequestDTO dto
    ) {

        return ResponseEntity.ok(
                editBeneficiaryTypeUseCase.editBeneficiaryType(id, dto)
        );
    }


}
