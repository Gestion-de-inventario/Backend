package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryTypeMapper;
import com.comedor.backend.application.ports.in.ChangeStatusBeneficiaryTypeUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeInUseException;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.CambioEstado;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;

public class ChangeStatusBeneficiaryTypeService implements ChangeStatusBeneficiaryTypeUseCase {

    private final BeneficiaryTypeRepositoryPort repository;
    private final BeneficiaryRepositoryPort beneficiaryRepository;
    private final BeneficiaryTypeMapper mapper;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public ChangeStatusBeneficiaryTypeService(BeneficiaryTypeRepositoryPort repository, BeneficiaryRepositoryPort beneficiaryRepository, BeneficiaryTypeMapper mapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.repository = repository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.mapper = mapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public BeneficiaryTypeResponseDTO changeStatus(Integer id, CambioEstado status) {

        BeneficiaryType domain = repository.findById(id);

        Estado nuevoEstado = status.toEstado();

        if(domain.getStatus() == nuevoEstado){
            return mapper.convertToDTO(domain);
        }

        if(nuevoEstado == Estado.INACTIVO &&
                beneficiaryRepository.CategoryisItAssignedToBeneficiary(id)) {

            throw new BeneficiaryTypeInUseException(
                    "No se puede desactivar el tipo porque tiene beneficiarios activos asociados."
            );
        }

        registrarModificacionUseCase.registrar(
                new ModificationsRequestDTO(
                        "Tipo Beneficiario",
                        "estado",
                        domain.getStatus().toString(),
                        nuevoEstado.toString()
                )
        );

        domain.setStatus(nuevoEstado);

        return mapper.convertToDTO(
                repository.update(domain)
        );
    }

}
