package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryTypeMapper;
import com.comedor.backend.application.ports.in.ChangeStatusBeneficiaryTypeUseCase;

import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeInUseException;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.domain.model.enums.ChangeStatus;
import com.comedor.backend.domain.model.enums.Status;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;

import java.util.Map;

public class ChangeStatusBeneficiaryTypeService implements ChangeStatusBeneficiaryTypeUseCase {

    private final BeneficiaryTypeRepositoryPort repository;
    private final BeneficiaryRepositoryPort beneficiaryRepository;
    private final BeneficiaryTypeMapper mapper;
    private final RegisterAuditUseCase registerAuditUseCase;

    public ChangeStatusBeneficiaryTypeService(BeneficiaryTypeRepositoryPort repository, BeneficiaryRepositoryPort beneficiaryRepository, BeneficiaryTypeMapper mapper, RegisterAuditUseCase registerAuditUseCase) {
        this.repository = repository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.mapper = mapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public BeneficiaryTypeResponseDTO changeStatus(Integer id, ChangeStatus status) {

        BeneficiaryType domain = repository.findById(id);

        Status newStatus = status.toEstado();

        if(domain.getStatus() == newStatus){
            return mapper.convertToDTO(domain);
        }

        if(newStatus == Status.INACTIVO &&
                beneficiaryRepository.CategoryisItAssignedToBeneficiary(id)) {

            throw new BeneficiaryTypeInUseException(
                    "No se puede desactivar el tipo porque tiene beneficiarios activos asociados."
            );
        }


        domain.setStatus(newStatus);

        BeneficiaryType saved = repository.update(domain);

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Tipo Beneficiario",
                        id,
                        domain.getName(),
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", "estado",
                                "previousValue",  domain.getStatus().toString(),
                                "newValue", newStatus.toString()
                        )
                )
        );

        return mapper.convertToDTO(saved);
    }

}
