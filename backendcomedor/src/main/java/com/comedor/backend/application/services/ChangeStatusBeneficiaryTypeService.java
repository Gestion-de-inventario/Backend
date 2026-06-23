package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryTypeMapper;
import com.comedor.backend.application.ports.in.ChangeStatusBeneficiaryTypeUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeInUseException;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.ChangeStatus;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;

public class ChangeStatusBeneficiaryTypeService implements ChangeStatusBeneficiaryTypeUseCase {

    private final BeneficiaryTypeRepositoryPort repository;
    private final BeneficiaryRepositoryPort beneficiaryRepository;
    private final BeneficiaryTypeMapper mapper;
    private final RegisterModificationUseCase registerModificationUseCase;

    public ChangeStatusBeneficiaryTypeService(BeneficiaryTypeRepositoryPort repository, BeneficiaryRepositoryPort beneficiaryRepository, BeneficiaryTypeMapper mapper, RegisterModificationUseCase registerModificationUseCase) {
        this.repository = repository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.mapper = mapper;
        this.registerModificationUseCase = registerModificationUseCase;
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

        registerModificationUseCase.registrar(
                new ModificationsRequestDTO(
                        "Tipo Beneficiario",
                        "estado",
                        domain.getStatus().toString(),
                        newStatus.toString()
                )
        );

        domain.setStatus(newStatus);

        return mapper.convertToDTO(
                repository.update(domain)
        );
    }

}
